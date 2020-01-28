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
        byte[] data = event.getReceivedData();
        String dataString = new String(data, Platform.CHARSET);

        System.out.println(dataString);

        int firstSpace = dataString.indexOf(' ');
        String idString = dataString.substring(0, firstSpace);
        String componentString = dataString.substring(firstSpace + 1);

        int id = Integer.parseInt(idString);
        String[] components = SPACE_PATTERN.split(componentString);

        InMsg msg = Protocol.decode(id, components);
        this.listener.handle(msg);
    }
}
