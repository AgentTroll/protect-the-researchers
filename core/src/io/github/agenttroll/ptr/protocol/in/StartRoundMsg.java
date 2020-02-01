package io.github.agenttroll.ptr.protocol.in;

import io.github.agenttroll.ptr.protocol.InMsg;

// Sent by the remote to indicate that the round
// has begun
public class StartRoundMsg extends InMsg {
    public StartRoundMsg(String[] components) {
        super(components);
    }
}
