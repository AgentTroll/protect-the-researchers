package io.github.caojohnny.ptr.platform;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

// Represents some platform constants that are used for
// encoding, communications, etc.
public final class Platform {
    public static final Charset CHARSET = StandardCharsets.US_ASCII;
    public static final int BAUD = 2_000_000;
    public static final boolean DEBUG = true;

    public static final float ANIMATION_INTERVAL_SEC = 1F / 8;

    private Platform() {
    }

    public static void printDebugWarning() {
        if (DEBUG) {
            RuntimeException warning = new RuntimeException("DEBUG MODE IS ON!");
            warning.printStackTrace();
        }
    }
}
