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
        if(name.equals("void")) type = types.VOID;
        else if(name.equals("int")) type = types.INT;
        else if(name.equals("bool")) type = types.BOOL;
        else if(name.equals("string")) type = types.STRING;
    }
}
