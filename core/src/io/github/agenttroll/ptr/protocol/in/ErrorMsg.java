package io.github.agenttroll.ptr.protocol.in;

import io.github.agenttroll.ptr.protocol.InMsg;

// A packet that represents that an error has occurred on the
// Arduino that the app should know about and log

// Format: <int:error code>
public class ErrorMsg extends InMsg {
    private final int errorCode;

    public ErrorMsg(String[] components) {
        super(components);
        this.errorCode = Integer.parseInt(components[0]);
    }

    public int getErrorCode() {
        return errorCode;
    }
}
