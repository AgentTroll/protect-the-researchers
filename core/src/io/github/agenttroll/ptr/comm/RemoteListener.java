package io.github.agenttroll.ptr.comm;

import com.badlogic.gdx.Gdx;
import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortEvent;
import com.fazecast.jSerialComm.SerialPortMessageListener;
import io.github.agenttroll.ptr.platform.Platform;
import io.github.agenttroll.ptr.protocol.InMsg;
import io.github.agenttroll.ptr.protocol.Protocol;

import java.util.regex.Pattern;

// Internal implementation of a serial listener used to
// handle serial input from a remote and transform it
// into messages that the application listener can understand
public class RemoteListener implements SerialPortMessageListener {
    private static final byte[] LF = "\n".getBytes(Platform.CHARSET);
    private static final String[] EMPTY_ARGS = new String[0];
    private static final ThreadLocal<Pattern> SPACE_PATTERN = ThreadLocal.withInitial(() -> Pattern.compile(" "));

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
            // Convert to String
            byte[] data = event.getReceivedData();
            String dataString = new String(data, Platform.CHARSET);

            // Identify where the ID portion is and decode it
            int firstSpace = dataString.indexOf(' ');
            String idString = dataString.substring(0, firstSpace).trim();

            int id = Integer.parseInt(idString);

            // If the message has extra fields, throw them into the
            // components array
            String[] components;
            if (dataString.length() > firstSpace + 1) {
                String componentString = dataString.substring(firstSpace + 1).trim();
                components = SPACE_PATTERN.get().split(componentString);
            } else {
                components = EMPTY_ARGS;
            }

            // Initialize packet with the information received
            InMsg msg = Protocol.decode(id, components);

            // Run the app handling code on the GUI thread to ensure safety
            Gdx.app.postRunnable(() -> this.listener.handle(msg));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
