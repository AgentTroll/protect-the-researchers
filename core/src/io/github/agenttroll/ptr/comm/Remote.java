package io.github.agenttroll.ptr.comm;

import com.fazecast.jSerialComm.SerialPort;
import io.github.agenttroll.ptr.platform.Platform;
import io.github.agenttroll.ptr.protocol.OutMsg;
import io.github.agenttroll.ptr.protocol.Protocol;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Remote {
    private final ExecutorService ioPool = Executors.newSingleThreadExecutor();
    private final SerialPort port;

    public Remote(String portId) {
        this.port = SerialPort.getCommPort(portId);

        // Apparently order matters here, btw
        this.port.openPort();
        this.port.setBaudRate(2000000);
    }

    public void addListener(MessageHandler listener) {
        this.port.addDataListener(new RemoteListener(listener));
    }

    public void sendPacket(OutMsg msg) {
        int id = Protocol.getId(msg);

        StringBuilder buf = new StringBuilder();
        buf.append(id).append(" ");
        msg.encode(buf);
        buf.append("\n");

        byte[] data = buf.toString().getBytes(Platform.CHARSET);
        this.ioPool.execute(() -> this.port.writeBytes(data, data.length));
    }

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
