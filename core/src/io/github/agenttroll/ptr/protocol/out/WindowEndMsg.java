package io.github.agenttroll.ptr.protocol.out;

import io.github.agenttroll.ptr.game.PlayerId;
import io.github.agenttroll.ptr.protocol.OutMsg;

public class WindowEndMsg implements OutMsg {
    private final PlayerId player;

    public WindowEndMsg(PlayerId player) {
        this.player = player;
    }

    @Override
    public void encode(StringBuilder buf) {
        buf.append(player.ordinal()).append(" ");
    }
}
