package io.github.agenttroll.ptr.protocol;

import io.github.agenttroll.ptr.protocol.in.ErrorMsg;
import io.github.agenttroll.ptr.protocol.in.InputStatusMsg;
import io.github.agenttroll.ptr.protocol.in.StartGameMsg;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

// Holds the ID-Message mappings
// Used to identify and construct incoming messages
// Used to obtain the assigned ID of outgoing messages
public class Protocol {
    private static final Map<Integer, Constructor<? extends InMsg>> inPackets =
            new HashMap<>();
    private static final Map<Class<? extends OutMsg>, Integer> outPackets =
            new HashMap<>();

    // Perform the mapping here
    static {
        insertInPacket(0, InputStatusMsg.class);
        insertInPacket(1, ErrorMsg.class);
        insertInPacket(2, StartGameMsg.class);

        // outPackets.put(WindowBeginMsg.class, 0);
    }

    // Shortcut method to locate the default constructor
    // for the InMsg subclass messages
    private static void insertInPacket(int id, Class<? extends InMsg> cls) {
        try {
            Constructor<? extends InMsg> ctor = cls.getConstructor(String[].class);
            inPackets.put(id, ctor);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    // Decodes the packet with the given ID and initializes them
    // with the given String components (DO NOT include ID field in the components)
    public static InMsg decode(int id, String[] components) {
        Constructor<? extends InMsg> ctor = inPackets.get(id);
        if (ctor == null) {
            throw new IllegalArgumentException("No such packet: " + id);
        }

        try {
            return ctor.newInstance((Object) components);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    // Obtains the ID number assigned to be prefixed to the data of the
    // given outgoing packet
    public static int getId(OutMsg msg) {
        Class<? extends OutMsg> cls = msg.getClass();
        Integer id = outPackets.get(cls);
        if (id == null) {
            throw new IllegalArgumentException("Unregistered OUT packet: " + cls.getSimpleName());
        }

        return id;
    }
}
