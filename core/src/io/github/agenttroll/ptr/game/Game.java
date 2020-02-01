package io.github.agenttroll.ptr.game;

import io.github.agenttroll.ptr.comm.Remote;
import io.github.agenttroll.ptr.protocol.in.InputStatusMsg;
import io.github.agenttroll.ptr.scene.*;

// State-holder class to pass around to the listeners
// and different scenes that will be utilized to show
// the graphics on the screen
public class Game {
    private final Remote leftRemote;
    private final Remote rightRemote;

    private GamePhase phase;
    private GameMode mode;
    private Scene currentScene;

    public Game(Remote leftRemote, Remote rightRemote) {
        this.leftRemote = leftRemote;
        this.rightRemote = rightRemote;
    }

    public void initGame() {
        this.leftRemote.addListener(new PtrListener(this));
        this.rightRemote.addListener(new PtrListener(this));

        this.setPhase(GamePhase.START);
        this.setCurrentScene(new StartScene());
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

    public void setMode(GameMode mode) {
        this.mode = mode;
    }

    public GameMode getMode() {
        return this.mode;
    }

    public Scene getCurrentScene() {
        return currentScene;
    }

    public void setCurrentScene(Scene currentScene) {
        Scene prevScene = this.currentScene;
        if (prevScene != null) {
            prevScene.dispose();
        }

        this.currentScene = currentScene;
    }

    public void handleRoundStart() {
        switch (this.mode) {
            case SINGLE_PLAYER:
                this.setCurrentScene(new SinglePlayerScene(this));
                break;
            case TWO_PLAYER:
                this.setCurrentScene(new TwoPlayerScene(this));
                break;
            default:
                throw new UnsupportedOperationException(this.mode.name() + " is not supported");
        }
    }

    public void handleInput(InputStatusMsg msg) {
        System.out.printf("Received ISM: status=%s%n", msg.getStatus());
        this.setCurrentScene(new TextDebugScreen(msg.getStatus().name()));
    }
}
