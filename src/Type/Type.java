package Type;

import Utility.Config;

public class Type {
    public enum types {
        VOID, INT, BOOL, STRING, CLASS, ARRAY, NULL
    }
    protected types type;
    protected String typeName;

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
        switch (type) {
            case VOID:
                typeName = "void";
                break;
            case INT:
                typeName = "int";
                break;
            case BOOL:
                typeName = "bool";
                break;
            case STRING:
                typeName = "string";
                break;
            case CLASS:
                break;
            case ARRAY:
                typeName = "array";
                break;
            case NULL:
                typeName = "null";
                break;
        }
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

    public boolean match(Type other) {
        return type == other.getType();
    }

    public boolean isStringType() {
        return type == types.STRING;
    }

    public boolean isBoolType() {
        return type == types.BOOL;
    }

    public boolean isVoidType() {
        return type == types.VOID;
    }

    public boolean isIntType() {
        return type == types.INT;
    }

    public int getBytes() {
        return Config.getRegSize();
    }
}
