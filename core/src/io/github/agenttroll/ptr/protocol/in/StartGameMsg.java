package io.github.agenttroll.ptr.protocol.in;

import io.github.agenttroll.ptr.game.GameMode;
import io.github.agenttroll.ptr.protocol.InMsg;

// Sent by an Arduino to tell the app that the game should
// start and the screen should update with the necessary
// information

// Format:
// <bool:is single player>
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
