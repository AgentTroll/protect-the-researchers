package io.github.agenttroll.ptr.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;
import io.github.agenttroll.ptr.ProtectTheResearchers;

public class DesktopLauncher {
    public static void main(String[] arg) {
        SerialPort[] ports = SerialPort.getCommPorts();
        for (SerialPort port : ports) {
            System.out.println(port.getSystemPortName());
        }

        SerialPort port = SerialPort.getCommPort("cu.usbserial-DN069XU7");
        port.openPort();
        port.addDataListener(new SerialPortDataListener() {
            @Override
            public int getListeningEvents() {
                return SerialPort.LISTENING_EVENT_DATA_AVAILABLE;
            }

            @Override
            public void serialEvent(SerialPortEvent event) {
                if (event.getEventType() != SerialPort.LISTENING_EVENT_DATA_AVAILABLE) {
                    return;
                }

                byte[] newData = new byte[port.bytesAvailable()];
                int numRead = port.readBytes(newData, newData.length);
                System.out.println("Read " + numRead + " bytes.");
                System.out.println(new String(newData));
            }
        });

        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        new LwjglApplication(new ProtectTheResearchers(), config);
    }
}
