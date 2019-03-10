package Type;

abstract public class Type {
    public enum types {
        VOID, INT, BOOL, STRING, CLASS, ARRAY, FUNCTION, NULL
    }

    protected types type;
    protected int allocsize;
}
