package io.github.agenttroll.ptr.protocol.in;

import io.github.agenttroll.ptr.game.PlayerId;
import io.github.agenttroll.ptr.protocol.InMsg;

public class InputStatusMsg extends InMsg {
    private final PlayerId player;
    private final boolean correct;

    public InputStatusMsg(String[] components) {
        super(components);

        int playerOrdinal = Integer.parseInt(components[0]);
        this.player = PlayerId.values()[playerOrdinal];
        this.correct = Boolean.parseBoolean(components[1]);
    }

    public PlayerId getPlayer() {
        return player;
    }

    public boolean isCorrect() {
        return correct;
    }
}
