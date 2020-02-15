package io.github.agenttroll.ptr.scene;

public class SinglePlayerStartScene extends TextDebugScreen {
    public SinglePlayerStartScene(boolean isPlayer) {
        super(isPlayer ? "PLAYER" : "COMPUTER");
    }
}
