package Type;

public class Type {
    public enum types {
        VOID, INT, BOOL, STRING, CLASS, ARRAY, FUNCTION, NULL
    }

    protected types type;
    protected int allocsize;

    public Type() {
        type = types.NULL;
    }

    public Type(String name) {
        switch (name) {
            case "void":
                type = types.VOID;
                break;
            case "int":
                type = types.INT;
                break;
            case "bool":
                type = types.BOOL;
                break;
            case "string":
                type = types.STRING;
                break;
            case "class":
                type = types.CLASS;
                break;
            default:
                type = types.NULL;
                break;
        }
    }
}
