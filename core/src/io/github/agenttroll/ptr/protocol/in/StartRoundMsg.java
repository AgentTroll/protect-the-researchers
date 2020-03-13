package io.github.agenttroll.ptr.protocol.in;

import io.github.agenttroll.ptr.game.ShapeId;
import io.github.agenttroll.ptr.protocol.InMsg;

// Sent by the remote to indicate that the round
// has begun

// Params:
// <int:lives remaining, int:shape id, long:round duration millis>
public class StartRoundMsg extends InMsg {
    private final int livesRemaining;
    private final ShapeId shapeId;
    private final long roundDurationMillis;

    public StartRoundMsg(String[] components) {
        super(components);

        this.livesRemaining = Integer.parseInt(components[0]);
        this.shapeId = ShapeId.values()[Integer.parseInt(components[1])];
        this.roundDurationMillis = Long.parseLong(components[2]);
    }

    public int getLivesRemaining() {
        return this.livesRemaining;
    }

    public ShapeId getShapeId() {
        return shapeId;
    }

    public long getRoundDurationMillis() {
        return roundDurationMillis;
    }
}
