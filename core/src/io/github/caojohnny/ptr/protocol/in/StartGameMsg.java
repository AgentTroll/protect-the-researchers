package io.github.caojohnny.ptr.protocol.in;

import io.github.caojohnny.ptr.game.GameMode;
import io.github.caojohnny.ptr.protocol.InMsg;

// Sent by an Arduino to tell the app that the game should
// start and the screen should update with the necessary
// information

// Format:
// <int:game mode>
public class StartGameMsg extends InMsg {
    private final GameMode mode;

    public StartGameMsg(String[] components) {
        super(components);

        int gameModeOrdinal = Integer.parseInt(components[0]);
        this.mode = GameMode.values()[gameModeOrdinal];
    }

    public GameMode getMode() {
        return this.mode;
    }
}
