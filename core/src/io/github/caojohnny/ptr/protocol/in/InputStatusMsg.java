package io.github.caojohnny.ptr.protocol.in;

import io.github.caojohnny.ptr.game.InputStatus;
import io.github.caojohnny.ptr.protocol.InMsg;

// Tells the app that the player has completed some kind
// of input action and whether or not to retain or to
// decrement the player's score

// Format:
// <bool:correct>
public class InputStatusMsg extends InMsg {
    private final InputStatus status;

    public InputStatusMsg(String[] components) {
        super(components);

        int statusOrdinal = Integer.parseInt(components[0]);
        this.status = InputStatus.values()[statusOrdinal];
    }

    public InputStatus getStatus() {
        return this.status;
    }
}
