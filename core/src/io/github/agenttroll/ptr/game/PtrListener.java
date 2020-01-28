package io.github.agenttroll.ptr.game;

import io.github.agenttroll.ptr.comm.MessageHandler;
import io.github.agenttroll.ptr.comm.Remote;
import io.github.agenttroll.ptr.protocol.InMsg;
import io.github.agenttroll.ptr.protocol.in.ErrorMsg;
import io.github.agenttroll.ptr.protocol.in.InputStatusMsg;
import io.github.agenttroll.ptr.protocol.out.WindowEndMsg;

public class PtrListener implements MessageHandler {
    private final Remote remote;

    public PtrListener(Remote remote) {
        this.remote = remote;
    }

    @Override
    public void handle(InMsg msg) {
        if (msg instanceof InputStatusMsg) {
            InputStatusMsg input = (InputStatusMsg) msg;
            this.remote.sendPacket(new WindowEndMsg(PlayerId.LEFT));
        }

        if (msg instanceof ErrorMsg) {
            ErrorMsg error = (ErrorMsg) msg;

            RuntimeException ex = new RuntimeException("Arduino error occurred (ec = " + error.getErrorCode() + ")");
            ex.printStackTrace();
        }
    }
}