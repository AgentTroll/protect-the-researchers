package io.github.agenttroll.ptr.game;

import io.github.agenttroll.ptr.comm.MessageHandler;
import io.github.agenttroll.ptr.comm.Remote;
import io.github.agenttroll.ptr.platform.Platform;
import io.github.agenttroll.ptr.protocol.InMsg;
import io.github.agenttroll.ptr.protocol.in.*;

import java.util.Arrays;

// NOT THREAD-SAFE
// This class should be called on the GUI thread

// This class is the application packet listener and performs
// logic in response to messages received from the Arduinos
public class PtrListener implements MessageHandler {
    private final Game game;

    public PtrListener(Game game) {
        this.game = game;
    }

    @Override
    public void handle(Remote remote, InMsg msg) {
        if (Platform.DEBUG) {
            System.out.printf("DEBUG: RECV '%s' FROM %s%n", msg.getClass().getSimpleName(),
                    remote.getPortId());
        }

        if (msg instanceof InputStatusMsg) {
            if (!this.filterPhase(GamePhase.RUNNING)) {
                return;
            }

            InputStatusMsg input = (InputStatusMsg) msg;
            this.game.handleInput(remote, input);
        }

        if (msg instanceof ErrorMsg) {
            ErrorMsg error = (ErrorMsg) msg;

            RuntimeException ex = new RuntimeException("Arduino error occurred (ec = " + error.getErrorCode() + ") on " +
                    remote.getPortId());
            ex.printStackTrace();
        }

        if (msg instanceof StartGameMsg) {
            if (!this.filterPhase(GamePhase.START)) {
                return;
            }

            this.game.handleStartGame(remote, (StartGameMsg) msg);
        }

        if (msg instanceof StartThreatMsg) {
            if (!this.filterPhase(GamePhase.RUNNING)) {
                return;
            }

            this.game.handleStartThreat(remote);
        }

        if (msg instanceof StartRoundMsg) {
            if (!this.filterPhase(GamePhase.RUNNING)) {
                return;
            }

            this.game.handleStartRound(remote);
        }

        if (msg instanceof EndGameMsg) {
            if (!this.filterPhase(GamePhase.RUNNING)) {
                return;
            }

            this.game.handleEndGame(remote, (EndGameMsg) msg);
        }

        if (msg instanceof GameResetMsg) {
            if (!this.filterPhase(GamePhase.END)) {
                return;
            }

            this.game.handleGameReset();
        }
    }

    // Ensure that the game is in the given phase(s)
    // If not in that phase, return false and do not proceed
    // with processing
    private boolean filterPhase(GamePhase... phases) {
        GamePhase cur = this.game.getPhase();
        for (GamePhase phase : phases) {
            if (phase == cur) {
                return true;
            }
        }

        RuntimeException ex = new RuntimeException("Unexpected phase " + cur + " when it should be " + Arrays.toString(phases));
        ex.printStackTrace();

        return false;
    }
}