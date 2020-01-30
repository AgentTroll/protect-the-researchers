package io.github.agenttroll.ptr.comm;

import com.fazecast.jSerialComm.SerialPort;
import io.github.agenttroll.ptr.platform.Platform;
import io.github.agenttroll.ptr.protocol.OutMsg;
import io.github.agenttroll.ptr.protocol.Protocol;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

// A wrapper class on a remote connection to an Arduino
// device using jSerialPort
public class Remote {
    // The IO threads used to send packets asynchronously from the main thread
    private final ExecutorService ioPool = Executors.newSingleThreadExecutor();
    private final SerialPort port;

    public Remote(String portId) {
        // No error checking here - should be handled by the app
        this.port = SerialPort.getCommPort(portId);

        // Apparently order matters here, btw
        this.port.openPort();
        this.port.setBaudRate(Platform.BAUD);
    }

    // Adds the given application listener to handle input from this remote
    public void addListener(MessageHandler listener) {
        this.port.addDataListener(new RemoteListener(listener));
    }

    // Sends the given packet to this remote
    public void sendPacket(OutMsg msg) {
        int id = Protocol.getId(msg);

        StringBuilder buf = new StringBuilder();
        buf.append(id).append(" ");
        msg.encode(buf);
        buf.append("\n");

        byte[] data = buf.toString().getBytes(Platform.CHARSET);
        this.ioPool.execute(() -> this.port.writeBytes(data, data.length));
    }

    // Closes resources needed to operate and handle messages to and
    // from this remote device
    public void dispose() {
        this.ioPool.shutdown();

        try {
            this.ioPool.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        this.port.removeDataListener();
        this.port.closePort();
    }
}
