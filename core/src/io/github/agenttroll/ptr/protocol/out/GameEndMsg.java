package io.github.agenttroll.ptr.protocol.out;

import io.github.agenttroll.ptr.protocol.OutMsg;

// Sent to the arduino that does not dispatch the end
// game message to forward the notification from the
// other arduino
// Params:
// <bool:target arduino won?>
public class GameEndMsg implements OutMsg {
    private final boolean won;

    public GameEndMsg(boolean won) {
        this.won = won;
    }

    @Override
    public void encode(StringBuilder buf) {
        buf.append(this.won);
    }
}
