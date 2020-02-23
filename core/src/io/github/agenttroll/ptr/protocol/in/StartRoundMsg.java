package io.github.agenttroll.ptr.protocol.in;

import io.github.agenttroll.ptr.protocol.InMsg;

// Sent by the remote to indicate that the round
// has begun

// Params:
// <int:lives remaining>
public class StartRoundMsg extends InMsg {
    private final int livesRemaining;

    public StartRoundMsg(String[] components) {
        super(components);

        this.livesRemaining = Integer.parseInt(components[0]);
    }

    public int getLivesRemaining() {
        return this.livesRemaining;
    }
}
