// --------------- Error codes ---------------

static const int UNKNOWN_PACKET_ID = 0;
static const int INVALID_GAME_STATE = 1;
static const int INVALID_ROUND_STATE = 2;
static const int DEBUG_MODE = 3;
static const int PLACEHOLDER = 4;

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
                break;
            }
        }

        args[i] = tmp_str;
        tmp_str = "";
        idx++;
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
static String bool_to_str(bool b) {
    return b ? TRUE_STR : FALSE_STR;
}

// Convert a "true" or "false" string to bool
static bool str_to_bool(String str) {
    return str == TRUE_STR;
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
static void send_start_game(int game_mode) {
    String game_mode_str = String(game_mode);
    Serial.println("2 " + game_mode_str);
}

// Indicates to the app that a new round for a single
// input has started
static void send_start_round(int lives_remaining, int shape, long round_duration_millis) {
    String lives_remaining_str = String(lives_remaining);
    String shape_str = String(shape);
    String duration_str = String(round_duration_millis);
    Serial.println("3 " + lives_remaining_str + " " + shape_str + " " + round_duration_millis);
}

// Indicates that a new threat (set of rounds) has started
// for a string of inputs
static void send_start_threat() {
    Serial.println("4");
}

// Indicates that the game has ended given whether or not
// the player on this Arduino has won
static void send_end_game(bool win) {
    String win_str = bool_to_str(win);
    Serial.println("5 " + win_str);
}

// Indicates that the game should be reset and the game
// state stored by the app should be cleared
static void send_game_reset() {
    Serial.println("6");
}

// ------------ Packet handling --------------

// Handles notification to set this arduino as the "computer"
// player and therefore this arduino will produce output
// rather than listening for it
static void handle_cpu_notif();

// Handles the notification that the other player has "caught
// up" to this arduino and the next threat can be presented
static void handle_threat_proceed();

// Handles the game being ended by the other player
static void handle_game_end(String components[]);

// Performs high-level processing (i.e. deciding what the
// game should do as a result of receiving the message)
static void handle_msg(int id, String components[]) {
    switch (id) {
        case 0:
            handle_cpu_notif();
            break;
        case 1:
            handle_threat_proceed();
            break;
        case 2:
            handle_game_end(components);
            break;
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
        populate_components(components, args, argc);

        handle_msg(packet_id, components);
    }
}

// --------------- Sonar handling -----------------

// 3000 usec timeout, ultrasound stops working too well sometime around 2500 usec
static const int PULSE_MAX_WAIT_USEC = 1000;
static const int SONAR_SAMPLE_SIZE = 20;

// Sends a ping and measures the RTT for the ping
// to reach the distance sensor
// Based on: https://dronebotworkshop.com/hc-sr04-ultrasonic-distance-sensor-arduino/
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

// Samples the "default" distance measured by the ultrasonic
// sensors and fills the given mean and stdev arrays with the
// data
static void sonar_rtt_sample(int pin, double *mean, double *stdev) {
    float sample_data[SONAR_SAMPLE_SIZE];
    for (int i = 0; i < SONAR_SAMPLE_SIZE; i++) {
        float rtt = sonar_rtt(pin);
        *mean += rtt;
        sample_data[i] = rtt;

        // Something to do with pulse rtt from the last round
        // interfering with the measurement sometimes
        delay(1);
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
static const int SHAPE_STAR = 1;
static const int SHAPE_TRIANGLE = 2;
static const int SHAPE_HEXAGON = 3;

static const bool SINGLE_PLAYER = true;
static const bool MULTI_PLAYER = false;

static const int IN_STATUS_CORRECT = 0;
static const int IN_STATUS_INCORRECT = 1;
static const int IN_STATUS_TIME_OUT = 2;

static const bool END_GAME_WIN = true;
static const bool END_GAME_LOSE = false;

// ---------------- Arduino program -------------------

static const long BAUD = 2000000;
static const bool DEBUG = false;

static const int SINGLE_PLAYER_BTN_PIN = 12;
static const int SLOT_COUNT = 4;
const int SONAR_PINS[] = { 7, 6, 5, 4 };
const int SHAPE_IDX_MAP[] = { SHAPE_SQUARE, SHAPE_STAR, SHAPE_TRIANGLE, SHAPE_HEXAGON };
const int LED_PINS[] = { 11, 10, 9, 8 };
static const double STDEV_MUL = 5;
static const float DELTA_THRESH = 100;

static const int GAME_STATE_AWAIT_START = -1;
static const int GAME_STATE_START = 0;
static const int GAME_STATE_RUNNING = 1;
static const int GAME_STATE_END = 2;

static const int TOTAL_THREATS = 3;
static const int INITIAL_ROUNDS_REQ = 5;
static const int INITIAL_LIVES = 3;

static const int ROUND_STATE_START = 0;
static const int ROUND_STATE_RUNNING = 1;
static const int ROUND_STATE_END = 2;
static const int ROUND_STATE_EXIT = 3;

static const long INITIAL_ROUND_LIMIT_MS = 5000;
static const long ROUND_LIMIT_DECR_MS = 250;
static const long ROUND_INTERIM_PAUSE_MS = 2000;

static const int END_STATE_PROCEED = 0;
static const int END_STATE_WIN = 1;
static const int END_STATE_LOSE = 2;
static const int END_STATE_NOTIFIED = 3;

static const long INPUT_CHANCE_NUM = 1;
static const long INPUT_CHANCE_DEN = 30000;
static const long INPUT_CORRECT_NUM = 50;
static const long INPUT_CORRECT_DEN = 100;

double sonar_rtt_means[SLOT_COUNT] = {};
double sonar_rtt_stdev[SLOT_COUNT] = {}; // Unused, this turns out to be really unreliable

bool is_cpu = false;
bool is_threat_waiting = false;

bool is_credits = false;
int game_state = GAME_STATE_AWAIT_START;
int end_state = END_STATE_PROCEED;

int threat_num = 0;
int rounds_req = INITIAL_ROUNDS_REQ;
int rounds_complete = INITIAL_ROUNDS_REQ;

int round_state = ROUND_STATE_START;
int expected_shape;
unsigned long round_begin_ms;
static void start_timer() { round_begin_ms = millis(); }
unsigned long cur_round_limit_ms = INITIAL_ROUND_LIMIT_MS;

int lives_remaining = INITIAL_LIVES;

void setup() {
    Serial.begin(BAUD);

    pinMode(SINGLE_PLAYER_BTN_PIN, INPUT);

    for (int i = 0; i < SLOT_COUNT; i++) {
        pinMode(LED_PINS[i], OUTPUT);
        digitalWrite(LED_PINS[i], LOW);
    }

    // Let console know we're in debug mode, just in case
    if (DEBUG) {
        send_error_packet(DEBUG_MODE);
    }
}

// Packet handling function definitions

static void handle_cpu_notif() {
    is_cpu = true;

    // The other arduino has started the game, enter
    // start game mode
    start_timer();
    game_state = GAME_STATE_START;
}

static void handle_threat_proceed() {
    start_timer();
    is_threat_waiting = false;
}

static void handle_game_end(String components[]) {
    end_state = END_STATE_NOTIFIED;

    start_timer();
    round_state = ROUND_STATE_EXIT;
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
    for (int i = 0; i < SLOT_COUNT; i++) {
        int pin = SONAR_PINS[i];
        int rtt = sonar_rtt(pin);
        if (rtt > 0) {
            int shape = SHAPE_IDX_MAP[i];
            if (shape == SHAPE_TRIANGLE) {
                if (is_credits) {
                    delay(250);
                    is_credits = false;
                    send_start_game(2);
                } else {
                    send_start_game(0);
                    return GAME_STATE_START;
                }
            } else if (shape == SHAPE_SQUARE) {
                if (!is_credits) {
                    send_start_game(1);
                    return GAME_STATE_START;
                }
            } else if (shape == SHAPE_STAR) {
                if (!is_credits) {
                    is_credits = true;
                    send_start_game(2);
                }
            }
        }
    }
 
    return GAME_STATE_AWAIT_START;
}

int start_game_func() {
    // Allow the start game screen to linger some before
    // actually beginning any logic
    long cur_ms = millis();
    long elapsed_ms = cur_ms - round_begin_ms;
    if (elapsed_ms < ROUND_INTERIM_PAUSE_MS) {
        return GAME_STATE_START;
    }
  
    round_state = ROUND_STATE_START;

    // Resample the unobstructed ultrasound at the beginning
    // of the game, just in case
    for (int i = 0; i < SLOT_COUNT; i++) {
        sonar_rtt_sample(SONAR_PINS[i],
                sonar_rtt_means + i,
                sonar_rtt_stdev + i);
        sonar_rtt_stdev[i] *= STDEV_MUL;
    }

    return GAME_STATE_RUNNING;
}

int start_round_func() {
    // The number of rounds to end the threat has elapsed
    // with the player entering everything in correctly
    if (rounds_complete == rounds_req) {
        // Start a timer
        start_timer();

        // If this is the last threat, end the game;
        // the player has won
        if (threat_num == TOTAL_THREATS) {
            // Notify the app
            send_end_game(END_GAME_WIN);
            end_state = END_STATE_WIN;

            return ROUND_STATE_END;
        }

        // Notify the app that a new threat has started
        // and advance the game state accordingly
        send_start_threat();
        threat_num++;
        rounds_complete = 0;
        is_threat_waiting = true;
    }

    // Don't allow the round to start if the threat has
    // not been passed by the prior player
    if (is_threat_waiting) {
        return ROUND_STATE_START;
    }

    // Wait for the player to see the screen using the
    // threat completion timer; otherwise we'll be
    // using the timer from the previous round which
    // will proceed past the if statement
    long cur_ms = millis();
    long elapsed_ms = cur_ms - round_begin_ms;
    if (elapsed_ms < ROUND_INTERIM_PAUSE_MS) {
        return ROUND_STATE_START;
    }

    // A new round has started, increment the
    // number of passed rounds counter
    rounds_complete++;

    // Start the timer for the current round
    start_timer();

    if (DEBUG) {
        expected_shape = SHAPE_SQUARE;
    } else {
        // Randomize the expected shape
        // The shape values use the protocol 0-3, so
        // simply limit to 4 here
        expected_shape = random(4);
    }

    // Light up the right LED
    for (int i = 0; i < SLOT_COUNT; i++) {
        if (SHAPE_IDX_MAP[i] == expected_shape) {
            digitalWrite(LED_PINS[i], HIGH);
        }
    }

    // Let the app know a new round has started
    // TODO: Reduce round duration
    send_start_round(lives_remaining, expected_shape, cur_round_limit_ms);
    cur_round_limit_ms -= ROUND_LIMIT_DECR_MS;

    return ROUND_STATE_RUNNING;
}

int get_input_status() {
    if (is_cpu) {
        bool provide_input = random(INPUT_CHANCE_DEN) < INPUT_CHANCE_NUM;
        if (provide_input) {
            bool correct = random(INPUT_CORRECT_DEN) < INPUT_CORRECT_NUM;
            if (correct) {
                return IN_STATUS_CORRECT;
            } else {
                return IN_STATUS_INCORRECT;
            }
        } else {
            return IN_STATUS_TIME_OUT; // take as no input given
        }
    } else {
        // Ping every sonar device
        for (int i = 0; i < SLOT_COUNT; i++) {
            int pin = SONAR_PINS[i];
            float rtt = sonar_rtt(pin);

            float delta = sonar_rtt_means[i] - rtt;
            float stdev = sonar_rtt_stdev[i];

            // The sensor reads as 0 if the timeout occurs
            // therefore, if there is anything in front of
            // the sensor, it will return the pulse time
            // which will be a non-zero number
            if (rtt > 0) {
                int mapped_shape = SHAPE_IDX_MAP[i];
                bool correct = mapped_shape == expected_shape;

                if (correct) {
                    return IN_STATUS_CORRECT;
                } else {
                    return IN_STATUS_INCORRECT;
                }
            }
        }

        return IN_STATUS_TIME_OUT;
    }
}

int running_round_func() {
    int input_status = get_input_status();

    if (input_status != IN_STATUS_TIME_OUT) {
        // If correct, let the app know about it
        // Otherwise, decrement the number of lives
        if (input_status == IN_STATUS_CORRECT) {
            send_input_status(input_status);
        } else if (input_status == IN_STATUS_INCORRECT) {
            lives_remaining--;

            // If no lives left, lose the game
            // and then flag the state machine to exit
            if (lives_remaining == 0) {
                // Notify the app
                send_end_game(END_GAME_LOSE);
                end_state = END_STATE_LOSE;
            } else {
                // Otherwise, let the app know the input was incorrect
                send_input_status(input_status);

                // TODO: Reset the threat?
            }
        }

        // Input response must be immediate and start a timer
        // to await the app to show whatever screen comes up next
        // ROUND_STATE_END to handle next action
        start_timer();
        return ROUND_STATE_END;
    }

    // Timeout code, if nothing passed through the sensor, then
    // the prior code would not have terminated early and the
    // timeout will handle the fact that no input was given in
    // every other case
    long cur_ms = millis();
    long elapsed_ms = cur_ms - round_begin_ms;
    if (elapsed_ms >= cur_round_limit_ms) {
        lives_remaining--;

        // If no lives left, lose the game
        // and then flag the state machine to exit
        if (lives_remaining == 0) {
            // Notify the app
            send_end_game(END_GAME_LOSE);
            end_state = END_STATE_LOSE;
        } else {
            // Otherwise the player isn't losing yet
            // update the screen accordingly
            send_input_status(IN_STATUS_TIME_OUT);
        }

        start_timer();
        return ROUND_STATE_END;
    }

    return ROUND_STATE_RUNNING;
}

int end_round_func() {
    // Turn off all LEDs
    for (int i = 0; i < SLOT_COUNT; i++) {
        pinMode(LED_PINS[i], OUTPUT);
        digitalWrite(LED_PINS[i], LOW);
    }
    
    // Wait for a few seconds to allow players to see anything
    // from the app and prepare for the next round
    long cur_ms = millis();
    long elapsed_ms = cur_ms - round_begin_ms;
    if (elapsed_ms >= ROUND_INTERIM_PAUSE_MS) {
        // Loop back to a new round if we should proceed
        // otherwise, the player has either lost or won
        // the game and the run loop should exit accordingly
        if (end_state == END_STATE_PROCEED) {
            return ROUND_STATE_START;
        } else {
            return ROUND_STATE_EXIT;
        }
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
    } else if (round_state == ROUND_STATE_EXIT) {
        return GAME_STATE_END;
    } else {
        send_error_packet(INVALID_ROUND_STATE);
        return GAME_STATE_END;
    }
  
    return GAME_STATE_RUNNING;
}

int end_game_func() {
    if (end_state != END_STATE_NOTIFIED) {
        send_game_reset();
    }
  
    // Cleanup and reset state for the next game
    is_cpu = false;
    is_threat_waiting = false;

    end_state = END_STATE_PROCEED;
    
    threat_num = 0;
    rounds_req = INITIAL_ROUNDS_REQ;
    rounds_complete = INITIAL_ROUNDS_REQ;
    round_state = ROUND_STATE_START;
    
    cur_round_limit_ms = INITIAL_ROUND_LIMIT_MS;
    lives_remaining = INITIAL_LIVES;

    return GAME_STATE_AWAIT_START;
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
