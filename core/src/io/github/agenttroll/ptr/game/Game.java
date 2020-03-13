package io.github.agenttroll.ptr.game;

import io.github.agenttroll.ptr.comm.Remote;
import io.github.agenttroll.ptr.platform.Platform;
import io.github.agenttroll.ptr.protocol.in.EndGameMsg;
import io.github.agenttroll.ptr.protocol.in.InputStatusMsg;
import io.github.agenttroll.ptr.protocol.in.StartGameMsg;
import io.github.agenttroll.ptr.protocol.in.StartRoundMsg;
import io.github.agenttroll.ptr.protocol.out.CpuNotifMsg;
import io.github.agenttroll.ptr.protocol.out.GameEndMsg;
import io.github.agenttroll.ptr.protocol.out.ThreatProceedMsg;
import io.github.agenttroll.ptr.scene.*;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

// TODO: Remove usages of TextDebugScene
// TODO: Figure out multiplayer

// State-holder class to pass around to the listeners
// and different scenes that will be utilized to show
// the graphics on the screen
public class Game {
    private final Remote leftRemote;
    private final Remote rightRemote;

    private final Map<Remote, PlayerData> playerData =
            new IdentityHashMap<>();

    private GamePhase phase;
    private GameMode mode;
    private Remote singlePlayerRemote;
    private Scene currentScene;

    private Remote currentWaitingRemote;
    private Threat threat;

    public Game(Remote leftRemote, Remote rightRemote) {
        this.leftRemote = leftRemote;
        this.rightRemote = rightRemote;

        this.playerData.put(this.leftRemote, new PlayerData());
        this.playerData.put(this.rightRemote, new PlayerData());

        if (Platform.DEBUG) {
            System.out.printf("DEBUG: LEFT=%s RIGHT=%s%n",
                    this.leftRemote.getPortId(),
                    this.rightRemote.getPortId());
        }
    }

    public void initGame() {
        this.leftRemote.addListener(new PtrListener(this));
        this.rightRemote.addListener(new PtrListener(this));

        this.setPhase(GamePhase.START);
        this.setCurrentScene(new MenuScene());
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

    public void setRemoteScreen(Remote source, SingleScene newScene) {
        if (!(this.currentScene instanceof SplitScene)) {
            throw new IllegalStateException("Not a SplitScene");
        }

        SplitScene scene = (SplitScene) this.currentScene;
        if (source == this.leftRemote) {
            scene.setLeft(newScene);
        } else if (source == this.rightRemote) {
            scene.setRight(newScene);
        } else {
            throw new IllegalArgumentException("Cannot decide if source is left or right");
        }
    }

    public Remote getOtherRemote(Remote curRemote) {
        if (curRemote == this.leftRemote) {
            return this.rightRemote;
        } else if (curRemote == this.rightRemote) {
            return this.leftRemote;
        } else {
            throw new IllegalArgumentException("Cannot decide if source is left or right");
        }
    }

    // Handles the start game signal when the player decides
    // which game mode to play
    public void handleStartGame(Remote source, StartGameMsg msg) {
        GameMode newMode = msg.getMode();
        if (newMode == GameMode.CREDITS) {
            GamePhase currentPhase = this.getPhase();
            if (currentPhase == GamePhase.CREDITS) {
                this.setCurrentScene(new MenuScene());
                this.setPhase(GamePhase.START);
            } else {
                this.setCurrentScene(new CreditsScene());
                this.setPhase(GamePhase.CREDITS);
            }

            return;
        }

        // Set game phase, scene and mode
        this.setPhase(GamePhase.RUNNING);
        this.setCurrentScene(new SplitScene());

        this.setMode(newMode);

        // Update the screens
        // If single player, let the player know which side of the screen he is on
        // If multiplayer, show the correct side for each arduino
        if (newMode == GameMode.SINGLE_PLAYER) {
            this.singlePlayerRemote = source;
            this.setRemoteScreen(this.singlePlayerRemote, new TextDebugScreen("You"));

            Remote cpuRemote = this.getOtherRemote(this.singlePlayerRemote);
            this.setRemoteScreen(cpuRemote, new TextDebugScreen("Computer"));

            // Let the computer arduino know
            cpuRemote.sendPacket(new CpuNotifMsg());
        } else {
            this.setRemoteScreen(this.getLeftRemote(), new MultiplayerStartScene(true));
            this.setRemoteScreen(this.getRightRemote(), new MultiplayerStartScene(false));
        }
    }

    // Handles the beginning of a new threat and the following
    // string of inputs needed
    public void handleStartThreat(Remote source) {
        if (this.currentWaitingRemote == null) {
            // No players are waiting, so this arduino is the first to
            // finish the threat. Wait here.

            this.currentWaitingRemote = source;
            this.setRemoteScreen(source, new TextDebugScreen("WAITING FOR OTHER PLAYER"));
        } else {
            // The other player has finished their threat
            // since the new remote has caught up, they can now both break
            // out of the wait loop
            int currentThreatIdx = ThreadLocalRandom.current().nextInt(Threat.values().length);
            this.threat = Threat.values()[currentThreatIdx];

            this.setPhase(GamePhase.NEW_THREAT);
            this.setCurrentScene(new StartThreatScene(threat));
            this.currentWaitingRemote = null;

            ThreatProceedMsg msg = new ThreatProceedMsg();
            this.getLeftRemote().sendPacket(msg);
            this.getRightRemote().sendPacket(msg);
        }
    }

    // Handles the beginning of the input window
    public void handleStartRound(Remote source, StartRoundMsg msg) {
        if (this.getPhase() == GamePhase.NEW_THREAT) {
            BackgroundedSplitScreen scene = new BackgroundedSplitScreen();

            AnimatedSingleScene background = new AnimatedSingleScene(threat.getAssetName(), threat.getAtlasPath());
            scene.setBackground(background);

            this.setCurrentScene(scene);
            this.setPhase(GamePhase.RUNNING);
        }

        int livesRemaining = msg.getLivesRemaining();

        PlayerData data = this.playerData.get(source);
        data.setLivesRemaining(livesRemaining);
        this.setRemoteScreen(source, new InputScene(data, msg));
    }

    // Handles the input being detected and processed by the game
    public void handleInput(Remote source, InputStatusMsg msg) {
        PlayerData data = this.playerData.get(source);
        int lives = data.getLivesRemaining();

        // If not correct, subtract and update
        if (msg.getStatus() != InputStatus.CORRECT) {
            lives--;
            data.setLivesRemaining(lives);
        }

        this.setRemoteScreen(source, new InputResponseScene(data, msg.getStatus() == InputStatus.CORRECT));
    }

    // Handles end game signal when someone wins or loses
    public void handleEndGame(Remote source, EndGameMsg msg) {
        this.setPhase(GamePhase.END);

        EndScene winScene = new EndScene(true);
        EndScene loseScene = new EndScene(false);

        // If single player, set the whole screen to say whether or not they won
        // Otherwise, show the respective players who won
        Remote other = this.getOtherRemote(source);
        if (this.getMode() == GameMode.SINGLE_PLAYER) {
            boolean playerWon = (source == this.singlePlayerRemote) == msg.isWin();
            this.setCurrentScene(playerWon ? winScene : loseScene);
        } else {
            this.setRemoteScreen(source, msg.isWin() ? winScene : loseScene);
            this.setRemoteScreen(other, msg.isWin() ? winScene : loseScene);
        }

        // Tell the other arduino to stop its game
        other.sendPacket(new GameEndMsg(!msg.isWin()));
    }

    // Handles the reset game signal to set the screen back
    // to the menu screen
    public void handleGameReset() {
        this.setPhase(GamePhase.START);
        this.setCurrentScene(new MenuScene());
        this.singlePlayerRemote = null;
        this.currentWaitingRemote = null;
    }
}
