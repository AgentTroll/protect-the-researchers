package io.github.agenttroll.ptr.comm;

import io.github.agenttroll.ptr.protocol.OutMsg;

public class DummyRemote implements Remote {
    private final String portId;

    public DummyRemote(String portId) {
        this.portId = portId;
    }

    @Override
    public String getPortId() {
        return this.portId;
    }

    @Override
    public void addListener(MessageHandler msgHandler) {
        System.out.printf("DEBUG: Added message handler '%s' to '%s'%n",
                msgHandler.getClass().getSimpleName(), this.portId);
    }

    @Override
    public void sendPacket(OutMsg msg) {
        System.out.printf("DEBUG: Sent packet '%s' to '%s'%n",
                msg.getClass().getSimpleName(), this.portId);
    }

    @Override
    public void dispose() {
        System.out.printf("DEBUG: Disposed '%s'%n", this.portId);
    }
}
