package io.github.agenttroll.ptr.game;

import io.github.agenttroll.ptr.comm.MessageHandler;
import io.github.agenttroll.ptr.comm.Remote;
import io.github.agenttroll.ptr.protocol.InMsg;
import io.github.agenttroll.ptr.protocol.in.ErrorMsg;
import io.github.agenttroll.ptr.protocol.in.InputStatusMsg;
import io.github.agenttroll.ptr.protocol.in.StartGameMsg;
import io.github.agenttroll.ptr.protocol.in.StartRoundMsg;

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
            if (!this.filterPhase(GamePhase.RUNNING)) {
                return;
            }

            InputStatusMsg input = (InputStatusMsg) msg;
            this.game.handleInput(input);
        }

        if (msg instanceof ErrorMsg) {
            ErrorMsg error = (ErrorMsg) msg;

            RuntimeException ex = new RuntimeException("Arduino error occurred (ec = " + error.getErrorCode() + ")");
            ex.printStackTrace();
        }

        if (msg instanceof StartGameMsg) {
            if (!this.filterPhase(GamePhase.START)) {
                return;
            }

            StartGameMsg startGame = (StartGameMsg) msg;
            this.game.setPhase(GamePhase.RUNNING);
            this.game.setMode(startGame.getMode());
        }

        if (msg instanceof StartRoundMsg) {
            if (!this.filterPhase(GamePhase.RUNNING)) {
                return;
            }

            this.game.handleRoundStart();
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

        return false;
    }
}