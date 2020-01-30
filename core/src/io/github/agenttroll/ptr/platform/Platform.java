package io.github.agenttroll.ptr.platform;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public final class Platform {
    public static final Charset CHARSET = StandardCharsets.US_ASCII;
    public static final int BAUD = 2_000_000;
    public static final boolean DEBUG = true;

    private Platform() {
    }

    public static void printDebugWarning() {
        if (DEBUG) {
            RuntimeException warning = new RuntimeException("DEBUG MODE IS ON!");
            warning.printStackTrace();
        }
    }
}
