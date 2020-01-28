package io.github.agenttroll.ptr.protocol;

import io.github.agenttroll.ptr.protocol.in.ErrorMsg;
import io.github.agenttroll.ptr.protocol.in.InputStatusMsg;
import io.github.agenttroll.ptr.protocol.out.WindowBeginMsg;
import io.github.agenttroll.ptr.protocol.out.WindowEndMsg;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class Protocol {
    private static final Map<Integer, Constructor<? extends InMsg>> inPackets =
            new HashMap<>();
    private static final Map<Class<? extends OutMsg>, Integer> outPackets =
            new HashMap<>();

    static {
        insertInPacket(0, InputStatusMsg.class);
        insertInPacket(1, ErrorMsg.class);

        outPackets.put(WindowBeginMsg.class, 0);
        outPackets.put(WindowEndMsg.class, 1);
    }

    private static void insertInPacket(int id, Class<? extends InMsg> cls) {
        try {
            Constructor<? extends InMsg> ctor = cls.getConstructor(String[].class);
            inPackets.put(id, ctor);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

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

    public static int getId(OutMsg msg) {
        Class<? extends OutMsg> cls = msg.getClass();
        Integer id = outPackets.get(cls);
        if (id == null) {
            throw new IllegalArgumentException("Unregistered OUT packet: " + cls.getSimpleName());
        }

        return id;
    }
}
