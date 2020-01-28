package io.github.agenttroll.ptr.comm;

import io.github.agenttroll.ptr.protocol.InMsg;

public interface MessageHandler {
    void handle(InMsg msg);
}
