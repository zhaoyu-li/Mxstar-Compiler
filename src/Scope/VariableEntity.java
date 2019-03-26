package Scope;

import Type.Type;

public class VariableEntity extends Entity {
    private Type type;
    private boolean isGlobal;
    private boolean isMutable;

    public VariableEntity() {
        type = null;
        isGlobal = false;
        isMutable = false;
    }

    public VariableEntity(Type type, String name) {
        this.type = type;
        this.name = name;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    public void setGlobal(boolean global) {
        isGlobal = global;
    }

    public void setMmutable(boolean mutable) {
        isMutable = mutable;
    }

    public boolean isMutable() {
        return isMutable;
    }
}
