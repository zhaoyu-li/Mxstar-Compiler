package Type;

public class Type {
    public enum types {
        VOID, INT, BOOL, STRING, CLASS, ARRAY, NULL
    }

    protected types type;
    protected String typeName;
    protected int allocsize;

    public Type() {
        type = types.NULL;
        typeName = null;
    }

    public Type(String name) {
        switch (name) {
            case "void":
                type = types.VOID;
                typeName = "void";
                break;
            case "int":
                type = types.INT;
                typeName = "int";
                break;
            case "bool":
                type = types.BOOL;
                typeName = "bool";
                break;
            case "string":
                type = types.STRING;
                typeName = "string";
                break;
            case "class":
                type = types.CLASS;
                break;
            case "array":
                type = types.ARRAY;
                typeName = "array";
                break;
            case "null":
                type = types.NULL;
            default:
                type = types.NULL;
                break;
        }
    }

    public void setType(types type) {
        this.type = type;
    }

    public types getType() {
        return type;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getTypeName() {
        return typeName;
    }

    public boolean isPrimitiveType() {
        switch (type) {
            case INT:
            case BOOL:
            case VOID:
            case STRING:
                return true;
            default:
                return false;
        }
    }

    public boolean isClassType() {
        return type == Type.types.CLASS;
    }

    public boolean isArrayType() {return  type == Type.types.ARRAY; }
}
