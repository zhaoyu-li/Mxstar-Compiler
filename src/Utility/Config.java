package Utility;

public class Config {
    final private static int REG_SIZE = 8;
    final public static int MAX_INLINE_INST = 30;
    final public static int MAX_INLINE_DEPTH = 5;

    public static int getRegSize() {
        return REG_SIZE;
    }
}
