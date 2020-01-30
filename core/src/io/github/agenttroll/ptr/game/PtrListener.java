package io.github.agenttroll.ptr.game;

import io.github.agenttroll.ptr.comm.MessageHandler;
import io.github.agenttroll.ptr.protocol.InMsg;
import io.github.agenttroll.ptr.protocol.in.ErrorMsg;
import io.github.agenttroll.ptr.protocol.in.InputStatusMsg;
import io.github.agenttroll.ptr.protocol.in.StartGameMsg;
import io.github.agenttroll.ptr.scene.SinglePlayerScene;
import io.github.agenttroll.ptr.scene.TwoPlayerScene;

public class PtrListener implements MessageHandler {
    private final Game game;

    public PtrListener(Game game) {
        this.game = game;
    }

    @Override
    public void handle(InMsg msg) {
        if (msg instanceof InputStatusMsg) {
            if (!this.filterPhase(GamePhase.RUNNING)) {
                return;
            }

            InputStatusMsg input = (InputStatusMsg) msg;
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

            GameMode mode = startGame.getMode();
            switch (mode) {
                case SINGLE_PLAYER:
                    this.game.setCurrentScene(new SinglePlayerScene(this.game));
                    break;
                case TWO_PLAYER:
                    this.game.setCurrentScene(new TwoPlayerScene(this.game));
                    break;
                default:
                    throw new IllegalStateException("Unknown GameMode: " + mode);
            }
        }
    }

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