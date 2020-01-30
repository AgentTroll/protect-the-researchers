package io.github.agenttroll.ptr.comm;

import io.github.agenttroll.ptr.protocol.InMsg;

// An abstract listener class to implement application
// logic upon reception of a packet from one of the
// Arduinos
public interface MessageHandler {
    void handle(InMsg msg);
}
