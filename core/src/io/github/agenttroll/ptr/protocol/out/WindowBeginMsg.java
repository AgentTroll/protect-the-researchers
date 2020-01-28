package io.github.agenttroll.ptr.protocol.out;

import io.github.agenttroll.ptr.game.ArrowId;
import io.github.agenttroll.ptr.game.PlayerId;
import io.github.agenttroll.ptr.game.ShapeId;
import io.github.agenttroll.ptr.protocol.OutMsg;

public class WindowBeginMsg implements OutMsg {
    private final PlayerId player;
    private final ArrowId arrowId;
    private final ShapeId shapeId;

    public WindowBeginMsg(PlayerId player, ArrowId arrowId, ShapeId shapeId) {
        this.player = player;
        this.arrowId = arrowId;
        this.shapeId = shapeId;
    }

    @Override
    public void encode(StringBuilder buf) {
        buf.append(player.ordinal()).append(" ")
                .append(arrowId.ordinal()).append(" ")
                .append(shapeId.ordinal());
    }
}
