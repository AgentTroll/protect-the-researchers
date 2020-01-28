// ---------- Protocol handling primitives ------------

int get_argc(String input) {
    int cnt = 1; // Count the last arg first
    for (char c : input) {
        if (c == ' ') {
            cnt++;
        }
    }

    return cnt;
}

void parse_args(String args[], String input, int argc) {
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

void populate_components(String components[], String args[], int argc) {
    for (int cmp_idx = 0, arg_idx = 1; arg_idx < argc; cmp_idx++, arg_idx++) {
        components[cmp_idx] = args[arg_idx];
    }
}

// ---------- Packet egress wrappers ----------

void send_error_packet(int ec) {
    String ec_str = String(ec);
    Serial.println("1 " + ec_str);
}

void send_input_status(int player, bool correct) {
    String player_id = String(player);
    String correct_str = correct ? "true" : "false";
    Serial.println("0 " + player_id + " " + correct_str);
}

// ------------ Packet handling --------------

void handle_msg(int id, String components[]) {
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

void ingest_packets() {
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

// --------------- State variables ----------------

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

// ---------------- Arduino program -------------------

static const long BAUD = 2000000;
static const int BTN_PIN = 4;

void setup() {
    Serial.begin(BAUD);

    pinMode(BTN_PIN, INPUT);
}

void loop() {
    int reading = digitalRead(BTN_PIN);
    if (reading == HIGH) {
        send_input_status(PLAYER_LEFT, true);
    }
    
    ingest_packets();
}
