package io.github.agenttroll.ptr.game;

import io.github.agenttroll.ptr.comm.Remote;
import io.github.agenttroll.ptr.scene.Scene;

public class Game {
    private final Remote leftRemote;
    private final Remote rightRemote;

    private GamePhase phase;
    private Scene currentScene;

    public Game(Remote leftRemote, Remote rightRemote) {
        this.leftRemote = leftRemote;
        this.rightRemote = rightRemote;

        this.leftRemote.addListener(new PtrListener(this));
        // this.rightRemote.addListener(new PtrListener(this));
    }

    public Remote getLeftRemote() {
        return leftRemote;
    }

    public Remote getRightRemote() {
        return rightRemote;
    }

    public void setPhase(GamePhase phase) {
        this.phase = phase;
    }

    public GamePhase getPhase() {
        return this.phase;
    }

    public Scene getCurrentScene() {
        return currentScene;
    }

    public void setCurrentScene(Scene currentScene) {
        this.currentScene = currentScene;
    }
}
