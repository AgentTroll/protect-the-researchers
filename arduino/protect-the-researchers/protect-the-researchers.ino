// --------------- Error codes ---------------

static const int UNKNOWN_PACKET_ID = 0;
static const int INVALID_GAME_STATE = 1;
static const int INVALID_ROUND_STATE = 2;

// ---------- Protocol handling primitives ------------

// Counts the number of space-separate arguments in the given string
static int get_argc(String input) {
    int cnt = 1; // Count the last arg first
    for (char c : input) {
        if (c == ' ') {
            cnt++;
        }
    }

    return cnt;
}

// Fills the given (empty!) array with each space-separated
// argument from the input string
// argc obtained from get_argc(...), of course
static void parse_args(String args[], String input, int argc) {
    String tmp_str = "";
    for (int i = 0, idx = 0; i < argc; i++) {
        for (; idx < input.length(); idx++) {
            char c = input[idx];
            if (c != ' ') {
                tmp_str.concat(input[idx]);
            } else {
                args[i] = tmp_str;
                tmp_str = "";
                idx++;
                break;
            }
        }
    }
}

// Fill components array, which contains only the message contents
// (not the ID, which prefixes the message as a sort of header)
static void populate_components(String components[], String args[], int argc) {
    for (int cmp_idx = 0, arg_idx = 1; arg_idx < argc; cmp_idx++, arg_idx++) {
        components[cmp_idx] = args[arg_idx];
    }
}

// ---------- Packet egress wrappers ----------

static const String TRUE_STR = "true";
static const String FALSE_STR = "false";

// Converts a boolean into a "true" or "false" string
static const String bool_to_str(bool b) {
    return b ? TRUE_STR : FALSE_STR;
}

// Sends a message indicating that an error as occurred
// using the given arbitrary error code to help debugging
static void send_error_packet(int ec) {
    String ec_str = String(ec);
    Serial.println("1 " + ec_str);
}

// Sends an input status message, which tells the app
// what input was received (or not received)
static void send_input_status(int in_status) {
    String in_status_str = String(in_status);
    Serial.println("0 " + in_status_str);
}

// Indicate to the app that the game state has changed to
// switch screens to the correct game mode
static void send_start_game(bool single_player) {
    String sp_str = bool_to_str(single_player);
    Serial.println("2 " + sp_str);
}

static void send_start_round() {
    Serial.println("3");
}

// ------------ Packet handling --------------

// Performs high-level processing (i.e. deciding what the
// game should do as a result of receiving the message)
static void handle_msg(int id, String components[]) {
    switch (id) {
        default:
            send_error_packet(UNKNOWN_PACKET_ID);
    }
}

// Reads raw serial input and then decodes messages
// coming from the app and then passes it off to handle_msg(...)
static void ingest_packets() {
    while (Serial.available() > 0) {
        String input = Serial.readStringUntil('\n');
        int argc = get_argc(input);

        String args[argc];
        parse_args(args, input, argc);

        int packet_id = args[0].toInt();
        String components[argc - 1];

        handle_msg(packet_id, components);
    }
}

// --------------- Sonar handling -----------------

// 3000 usec timeout, ultrasound stops working too well sometime around 2500 usec
static const int PULSE_MAX_WAIT_USEC = 3000;
static const int SONAR_SAMPLE_SIZE = 20;

// Sends a ping and measures the RTT for the ping
// to reach the distance sensor
static float sonar_rtt(int pin) {
    pinMode(pin, OUTPUT);
    digitalWrite(pin, LOW);
    delayMicroseconds(2);
    digitalWrite(pin, HIGH);
    delayMicroseconds(10);
    digitalWrite(pin, LOW);

    pinMode(pin, INPUT);
    return pulseIn(pin, HIGH, PULSE_MAX_WAIT_USEC);
}

static void sonar_rtt_sample(int pin, double *mean, double *stdev) {
    float sample_data[SONAR_SAMPLE_SIZE];
    for (int i = 0; i < SONAR_SAMPLE_SIZE; i++) {
        float rtt = sonar_rtt(pin);
        *mean += rtt;
        sample_data[i] = rtt;
    }
    *mean /= SONAR_SAMPLE_SIZE;

    for (float rtt : sample_data) {
        *stdev += (rtt - *mean) * (rtt - *mean);
    }
    *stdev = sqrt(*stdev / (SONAR_SAMPLE_SIZE - 1));
}

// --------------- Protocol constants ----------------

static const int PLAYER_LEFT = 0;
static const int PLAYER_RIGHT = 1;

static const int SHAPE_SQUARE = 0;
static const int SHAPE_CIRCLE = 1;
static const int SHAPE_TRIANGLE = 2;
static const int SHAPE_STAR = 3;

static const bool SINGLE_PLAYER = true;
static const bool MULTI_PLAYER = false;

static const int IN_STATUS_CORRECT = 0;
static const int IN_STATUS_INCORRECT = 1;
static const int IN_STATUS_TIME_OUT = 2;

// ----------- Button state protection ------------

bool single_player_btn = false;

// Check button with hold-down guard
// Basically, when the button is pressed, indicate that
// the button is held until the button changes back to
// unreleased to the loop() from reading multiple button
// presses if the button is held down
static bool check_btn(int pin, bool *active_state) {
    int reading = digitalRead(pin);
    if (reading == HIGH && !*active_state) {
        *active_state = true;
        return true;
    } else if (reading == LOW) {
        *active_state = false;
    }

    return false;
}

// ---------------- Arduino program -------------------

static const long BAUD = 2000000;

static const int SINGLE_PLAYER_BTN_PIN = 4;
static const int SONAR_PINC = 1;
const int SONAR_PINS[] = { 10 };
const int SONAR_IDX_SHAPE_MAP[] = { SHAPE_SQUARE };
static const double STDEV_MUL = 5;

static const int GAME_STATE_AWAIT_START = -1;
static const int GAME_STATE_START = 0;
static const int GAME_STATE_RUNNING = 1;
static const int GAME_STATE_END = 2;

static const int ROUND_STATE_START = 0;
static const int ROUND_STATE_RUNNING = 1;
static const int ROUND_STATE_END = 2;

static const long INITIAL_ROUND_LIMIT_MS = 5000;
static const long ROUND_INTERIM_PAUSE_MS = 2000;

double sonar_rtt_means[SONAR_PINC] = {0};
double sonar_rtt_stdev[SONAR_PINC] = {0};

int game_state = GAME_STATE_AWAIT_START;

int round_state;
int expected_shape;
unsigned long round_begin_ms;
unsigned long cur_round_limit_ms = INITIAL_ROUND_LIMIT_MS;

void setup() {
    Serial.begin(BAUD);

    pinMode(SINGLE_PLAYER_BTN_PIN, INPUT);
}

// This looks a little unwieldy but trust me this is a lot easier to maintain
// once you get the hang of it

// The game is a state machine, this allows the Arduino to be responsive to
// any messages being sent to it from the app (at the time of writing, nothing
// is being sent actually, so this might not even be useful in the future).
// The key is to reduce the latency and prevent using delay*() which will stall
// the Arduino and prevent it from doing ANYTHING - things such as checking with
// the sensors will be put on hold, so things can be missed here.

// The game state goes basically from pre_start -> start -> run -> end.
// With minimal use of objects, a state machine allows the code to be pretty darn
// clean and allows for a very basic level of separation of responsibility. It
// is easy to understand the lifecycle and to do control flow using a state machine.
// On top of that, it allows me to make changes to each stage independent to other
// stages, thereby making it extremely flexible and easy to change as we are still
// prototyping and figuring out what features to and not to implement at the time
// of writing this.

// The run phase is a state machine inside the state machine. While the game is in
// the run phase, it will handle rounds in exactly the same way with a start round ->
// run round -> and end round. Again, this allows for the greatest level of
// responsiveness and utilization of the loop function without having to rely on
// the hardware to keep time.

int await_start_game_func() {
    if (check_btn(SINGLE_PLAYER_BTN_PIN, &single_player_btn)) {
        send_start_game(SINGLE_PLAYER);

        return GAME_STATE_START;
    }

    return GAME_STATE_AWAIT_START;
}

int start_game_func() {
    round_state = ROUND_STATE_START;

    // Resample the unobstructed ultrasound at the beginning
    // of the game, just in case
    for (int i = 0; i < SONAR_PINC; i++) {
        sonar_rtt_sample(SONAR_PINS[i],
                sonar_rtt_means + i,
                sonar_rtt_stdev + i);
        sonar_rtt_stdev[i] *= STDEV_MUL;
    }

    return GAME_STATE_RUNNING;
}

int start_round_func() {
    round_begin_ms = millis();

    // Randomize the expected shape
    // The shape values use the protocol 0-3, so
    // simply limit to 4 here
    expected_shape = random(4);

    send_start_round();

    return ROUND_STATE_RUNNING;
}

int running_round_func() {
    // Ping every sonar device
    for (int i = 0; i < SONAR_PINC; i++) {
        int pin = SONAR_PINS[i];
        float rtt = sonar_rtt(pin);

        float delta = sonar_rtt_means[i] - rtt;
        float stdev = sonar_rtt_stdev[i];

        // If the difference between the initial value
        // and the value read is greater than 3 standard deviations
        // then there's a good chance something has passed the sensor
        if (delta > stdev) {
            int mapped_shape = SONAR_IDX_SHAPE_MAP[i];
            send_input_status(mapped_shape == expected_shape ? IN_STATUS_CORRECT : IN_STATUS_INCORRECT);

            // Terminate the round early irrespective of the right or wrong shape
            round_begin_ms = millis();
            return ROUND_STATE_END;
        }
    }

    // Timeout code, if nothing passed through the sensor, then
    // the prior code would not have terminated early and the
    // timeout will handle the fact that no input was given in
    // every other case
    long cur_ms = millis();
    long elapsed_ms = cur_ms - round_begin_ms;
    if (elapsed_ms >= cur_round_limit_ms) {
        send_input_status(IN_STATUS_TIME_OUT);

        round_begin_ms = cur_ms;
        return ROUND_STATE_END;
    }

    return ROUND_STATE_RUNNING;
}

int end_round_func() {
    // Wait for a few seconds to allow players to see anything
    // and prepare for the next round
    long cur_ms = millis();
    long elapsed_ms = cur_ms - round_begin_ms;
    if (elapsed_ms >= ROUND_INTERIM_PAUSE_MS) {
        return ROUND_STATE_START;
    }
  
    return ROUND_STATE_END;
}

int running_game_func() {
    if (round_state == ROUND_STATE_START) {
        round_state = start_round_func();
    } else if (round_state == ROUND_STATE_RUNNING) {
        round_state = running_round_func();
    } else if (round_state == ROUND_STATE_END) {
        round_state = end_round_func();
    } else {
        send_error_packet(INVALID_ROUND_STATE);
        return GAME_STATE_END;
    }
  
    return GAME_STATE_RUNNING;
}

int end_game_func() {
    return GAME_STATE_START;
}

void loop() {
    if (game_state == GAME_STATE_AWAIT_START) {
        game_state = await_start_game_func();
    } else if (game_state == GAME_STATE_START) {
        game_state = start_game_func();
    } else if (game_state == GAME_STATE_RUNNING) {
        game_state = running_game_func();
    } else if (game_state == GAME_STATE_END) {
        game_state = end_game_func();
    } else {
        send_error_packet(INVALID_GAME_STATE);
        game_state = GAME_STATE_AWAIT_START;
    }
    
    ingest_packets();
}
