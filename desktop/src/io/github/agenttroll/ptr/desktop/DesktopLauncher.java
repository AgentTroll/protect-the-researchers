package io.github.agenttroll.ptr.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.fazecast.jSerialComm.SerialPort;
import io.github.agenttroll.ptr.ProtectTheResearchers;

public class DesktopLauncher {
    public static void main(String[] args) {
        if (args.length == 0) {
            print("A port argument is required to connect to the Arduino. Try --show-ports.");
            return;
        }

        if (args.length != 1) {
            print("Provide only a single argument to this program in order to run.");
            return;
        }

        String arg = args[0];
        if (arg.equalsIgnoreCase("--show-ports")) {
            print("Showing all available serial ports:");
            for (SerialPort port : SerialPort.getCommPorts()) {
                print("- " + port.getSystemPortName() + " (" + port.getDescriptivePortName() + ")");
            }

            return;
        }

        if (!isPortValid(arg)) {
            print("Unable to find port '" + arg + "', try --show-ports.");
            return;
        }

        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        new LwjglApplication(new ProtectTheResearchers(arg), config);
    }

    private static void print(String string) {
        System.out.println(string);
    }

    private static boolean isPortValid(String test) {
        for (SerialPort port : SerialPort.getCommPorts()) {
            if (port.getSystemPortName().equals(test)) {
                return true;
            }
        }

        return false;
    }
}
