package io.github.agenttroll.ptr.protocol.in;

import io.github.agenttroll.ptr.protocol.InMsg;

// Indicates that the game is resetting to the start screen
public class GameResetMsg extends InMsg {
    public GameResetMsg(String[] components) {
        super(components);
    }
}
