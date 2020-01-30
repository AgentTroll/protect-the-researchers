// ---------- Protocol handling primitives ------------

static int get_argc(String input) {
    int cnt = 1; // Count the last arg first
    for (char c : input) {
        if (c == ' ') {
            cnt++;
        }
    }

    return cnt;
}

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

static void populate_components(String components[], String args[], int argc) {
    for (int cmp_idx = 0, arg_idx = 1; arg_idx < argc; cmp_idx++, arg_idx++) {
        components[cmp_idx] = args[arg_idx];
    }
}

// ---------- Packet egress wrappers ----------

static const String TRUE_STR = "true";
static const String FALSE_STR = "false";

static const String bool_to_str(bool b) {
    return b ? TRUE_STR : FALSE_STR;
}

static void send_error_packet(int ec) {
    String ec_str = String(ec);
    Serial.println("1 " + ec_str);
}

static void send_input_status(int player, bool correct) {
    String player_id = String(player);
    String correct_str = bool_to_str(correct);
    Serial.println("0 " + player_id + " " + correct_str);
}

static void send_start_game(bool single_player) {
    String sp_str = bool_to_str(single_player);
    Serial.println("2 " + sp_str);
}

// ------------ Packet handling --------------

static void handle_msg(int id, String components[]) {
    switch (id) {
        case 0: // WindowBeginMsg
            send_error_packet(400);
            break;
        case 1: // WindowEndMsg
            send_error_packet(401);
            break;
        default:
            send_error_packet(0);
    }
}

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

static float sonar_rtt(int pin) {
    pinMode(pin, OUTPUT);
    digitalWrite(pin, LOW);
    delayMicroseconds(2);
    digitalWrite(pin, HIGH);
    delayMicroseconds(10);
    digitalWrite(pin, LOW);

    pinMode(pin, INPUT);
    return pulseIn(pin, HIGH, 3000); // 3000 usec timeout, ultrasound stops working too well sometime around 2500 usec
}

// --------------- Protocol constants ----------------

static const int PLAYER_LEFT = 0;
static const int PLAYER_RIGHT = 1;

static const int ARROW_UP = 0;
static const int ARROW_DOWN = 1;
static const int ARROW_LEFT = 2;
static const int ARROW_RIGHT = 3;
static const int ARROW_NULL = 4;

static const int SHAPE_TRIANGLE = 0;
static const int SHAPE_SQUARE = 1;
static const int SHAPE_CIRCLE = 2;
static const int SHAPE_PENTAGON = 3;
static const int SHAPE_NULL = 4;

// ----------- Button state protection ------------

bool single_player_btn = false;

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

static const int SONAR_PIN = 10;

void setup() {
    Serial.begin(BAUD);

    pinMode(SINGLE_PLAYER_BTN_PIN, INPUT);
}

void loop() { 
    if (check_btn(SINGLE_PLAYER_BTN_PIN, &single_player_btn)) {
        send_start_game(true); 
    }

    // Serial.println(sonar_rtt(SONAR_PIN));
    
    ingest_packets();
}
