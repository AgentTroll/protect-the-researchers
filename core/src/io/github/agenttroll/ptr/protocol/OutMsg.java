package io.github.agenttroll.ptr.protocol;

// An abstract superclass for all outgoing packets
public interface OutMsg {
    // Adds the space-separated data to the given
    // data buffer when it is encoded and sent
    // to the Remote
    void encode(StringBuilder buf);
}
