package io.github.agenttroll.ptr.comm;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortEvent;
import com.fazecast.jSerialComm.SerialPortMessageListener;
import io.github.agenttroll.ptr.platform.Platform;
import io.github.agenttroll.ptr.protocol.InMsg;
import io.github.agenttroll.ptr.protocol.Protocol;

import java.util.regex.Pattern;

public class RemoteListener implements SerialPortMessageListener {
    private static final byte[] LF = "\n".getBytes(Platform.CHARSET);
    private static final Pattern SPACE_PATTERN = Pattern.compile(" ");
    private static final String[] EMPTY_ARGS = new String[0];

    private final MessageHandler listener;

    public RemoteListener(MessageHandler listener) {
        this.listener = listener;
    }

    @Override
    public byte[] getMessageDelimiter() {
        return LF;
    }

    @Override
    public boolean delimiterIndicatesEndOfMessage() {
        return true;
    }

    @Override
    public int getListeningEvents() {
        return SerialPort.LISTENING_EVENT_DATA_RECEIVED;
    }

    @Override
    public void serialEvent(SerialPortEvent event) {
        try {
            byte[] data = event.getReceivedData();
            String dataString = new String(data, Platform.CHARSET);

            int firstSpace = dataString.indexOf(' ');
            String idString = dataString.substring(0, firstSpace).trim();

            int id = Integer.parseInt(idString);
            String[] components;
            if (dataString.length() > firstSpace + 1) {
                String componentString = dataString.substring(firstSpace + 1).trim();
                components = SPACE_PATTERN.split(componentString);
            } else {
                components = EMPTY_ARGS;
            }

            InMsg msg = Protocol.decode(id, components);
            this.listener.handle(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
