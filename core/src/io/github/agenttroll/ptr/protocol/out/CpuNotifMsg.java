package io.github.agenttroll.ptr.protocol.out;

import io.github.agenttroll.ptr.protocol.OutMsg;

// Sent to a remote when the other remote is known to
// be the player and that this one should be the
// "computer"
public class CpuNotifMsg implements OutMsg {
    @Override
    public void encode(StringBuilder buf) {
    }
}
