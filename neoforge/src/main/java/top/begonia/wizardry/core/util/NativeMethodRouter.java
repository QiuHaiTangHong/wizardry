package top.begonia.wizardry.core.util;

import java.util.function.Supplier;

public final class NativeMethodRouter {

    private static native void sayHello();

    public static final Supplier<Void> SAY_HELLO = () -> {
        sayHello();
        return null;
    };

    static {
        try {
            System.loadLibrary("native-libs");
        } catch (UnsatisfiedLinkError e) {
            System.err.println("[Wizardry JNI] Failed to load native library!");
            e.printStackTrace();
        }
    }
}