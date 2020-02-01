package io.github.agenttroll.ptr.comm;

import io.github.agenttroll.ptr.protocol.OutMsg;

// This is an abstraction of a device that is used
// to control the actual game and the game input
// Should, at least in theory, be thread-safe
public interface Remote {
    // Adds the given application listener to handle input from this remote
    void addListener(MessageHandler msgHandler);

    // Sends the given packet to this remote
    void sendPacket(OutMsg msg);

    // Closes resources needed to operate and handle messages to and
    // from this remote device
    void dispose();
}
