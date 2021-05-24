package io.github.caojohnny.ptr.game;

import io.github.caojohnny.ptr.comm.MessageHandler;
import io.github.caojohnny.ptr.comm.Remote;
import io.github.caojohnny.ptr.protocol.InMsg;
import io.github.caojohnny.ptr.protocol.in.*;

import java.util.Arrays;

import static io.github.caojohnny.ptr.game.GamePhase.*;

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
        if (msg instanceof InputStatusMsg) {
            if (!this.filterPhase(RUNNING)) {
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
            if (!this.filterPhase(START, CREDITS)) {
                return;
            }

            this.game.handleStartGame(remote, (StartGameMsg) msg);
        }

        if (msg instanceof StartThreatMsg) {
            if (!this.filterPhase(RUNNING)) {
                return;
            }

            this.game.handleStartThreat(remote);
        }

        if (msg instanceof StartRoundMsg) {
            if (!this.filterPhase(RUNNING, NEW_THREAT)) {
                return;
            }

            this.game.handleStartRound(remote, (StartRoundMsg) msg);
        }

        if (msg instanceof EndGameMsg) {
            if (!this.filterPhase(RUNNING)) {
                return;
            }

            this.game.handleEndGame(remote, (EndGameMsg) msg);
        }

        if (msg instanceof GameResetMsg) {
            if (!this.filterPhase(END)) {
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