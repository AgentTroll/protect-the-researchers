package io.github.agenttroll.ptr.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.fazecast.jSerialComm.SerialPort;
import io.github.agenttroll.ptr.PtrApp;
import io.github.agenttroll.ptr.platform.Platform;

public class DesktopLauncher {
    public static void main(String[] args) {
        // --show-ports argument support
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("--show-ports")) {
                print("Showing all available serial ports:");
                for (SerialPort port : SerialPort.getCommPorts()) {
                    print("- " + port.getSystemPortName() + " (" + port.getDescriptivePortName() + ")");
                }

                return;
            }
        }

        // DEBUG warning
        if (Platform.DEBUG && args.length == 1) {
            Platform.printDebugWarning();

            args = new String[] {args[0], SerialPort.getCommPorts()[0].getSystemPortName()};
        }

        // Sanity check for left and right remote port arguments passed through CLI
        if (args.length != 2) {
            print("Two ports are is required to connect to the Arduinos. Try --show-ports.");
            return;
        }

        String leftPort = args[0];
        String rightPort = args[1];

        if (!isPortValid(leftPort)) {
            print("Unable to find port '" + leftPort + "', try --show-ports.");
            return;
        }

        if (!isPortValid(rightPort)) {
            print("Unable to find port '" + rightPort + "', try --show-ports.");
            return;
        }

        PtrApp app = new PtrApp(leftPort, rightPort);
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        new LwjglApplication(app, config);
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

        // Allow dummy ports if debugging, otherwise return false
        return Platform.DEBUG && test.contains("dummy");
    }
}
