package io.github.agenttroll.ptr.protocol.in;

import io.github.agenttroll.ptr.game.GameMode;
import io.github.agenttroll.ptr.protocol.InMsg;

public class StartGameMsg extends InMsg {
    private final GameMode mode;

    public StartGameMsg(String[] components) {
        super(components);

        boolean isSinglePlayer = Boolean.parseBoolean(components[0]);
        this.mode = isSinglePlayer ? GameMode.SINGLE_PLAYER : GameMode.TWO_PLAYER;
    }

    public GameMode getMode() {
        return this.mode;
    }
}
