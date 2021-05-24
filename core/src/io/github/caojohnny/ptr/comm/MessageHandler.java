package io.github.caojohnny.ptr.comm;

import io.github.caojohnny.ptr.protocol.InMsg;

// An abstract listener class to implement application
// logic upon reception of a packet from one of the
// Arduinos
public interface MessageHandler {
    void handle(Remote remote, InMsg msg);
}
