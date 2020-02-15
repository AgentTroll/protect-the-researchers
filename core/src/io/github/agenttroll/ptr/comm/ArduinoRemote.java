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
public class ArduinoRemote implements Remote {
    // The IO threads used to send packets asynchronously from the main thread
    private final ExecutorService ioPool = Executors.newSingleThreadExecutor();
    private final String portId;
    private final SerialPort port;

    public ArduinoRemote(String portId) {
        // No error checking here - should be handled by the app
        this.port = SerialPort.getCommPort(portId);
        this.portId = portId;

        // Apparently order matters here, btw
        this.port.openPort();
        this.port.setBaudRate(Platform.BAUD);
    }

    @Override
    public String getPortId() {
        return this.portId;
    }

    @Override
    public void addListener(MessageHandler listener) {
        this.port.addDataListener(new RemoteListener(this, listener));
    }

    @Override
    public void sendPacket(OutMsg msg) {
        int id = Protocol.getId(msg);

        // String manip with the StringBuilder is relatively fast
        // State for packets should usually be immutable but do
        // encoding on the GUI thread just in case
        StringBuilder buf = new StringBuilder();
        buf.append(id).append(" ");
        msg.encode(buf);
        buf.append("\n");

        String bufStr = buf.toString().trim();
        byte[] data = bufStr.getBytes(Platform.CHARSET);
        this.ioPool.execute(() -> {
            this.port.writeBytes(data, data.length);

            if (Platform.DEBUG) {
                System.out.printf("DEBUG: SENT '%s' (%s) TO %s%n",
                        msg.getClass().getSimpleName(),
                        bufStr,
                        this.portId);
            }
        });
    }

    @Override
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
