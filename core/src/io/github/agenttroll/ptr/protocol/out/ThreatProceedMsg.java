package io.github.agenttroll.ptr.protocol.out;

import io.github.agenttroll.ptr.protocol.OutMsg;

// Should be multicasted to the Arduino to let them
// know that they should break out of a waiting loop
// because the other player has caught up to their
// current threat
public class ThreatProceedMsg implements OutMsg {
    @Override
    public void encode(StringBuilder buf) {
    }
}
