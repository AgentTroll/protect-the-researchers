package io.github.caojohnny.ptr.protocol.in;

import io.github.caojohnny.ptr.protocol.InMsg;

// Sent to indicate that the game has ended

// Params: <bool:did player win>
public class EndGameMsg extends InMsg {
    private final boolean win;

    public EndGameMsg(String[] components) {
        super(components);

        this.win = Boolean.parseBoolean(components[0]);
    }

    public boolean isWin() {
        return this.win;
    }
}
