package io.github.caojohnny.ptr.scene;

public class MultiplayerStartScene extends TextDebugScreen {
    public MultiplayerStartScene(boolean isLeft) {
        super(isLeft ? "LEFT PLAYER" : "RIGHT PLAYER");
    }
}
