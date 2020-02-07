package io.github.agenttroll.ptr.protocol.in;

import io.github.agenttroll.ptr.protocol.InMsg;

// Sent to indicate that a new threat for which a
// string of rounds has started
public class StartThreatMsg extends InMsg {
    public StartThreatMsg(String[] components) {
        super(components);
    }
}
