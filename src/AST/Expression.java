package AST;

import Type.Type;

public abstract class Expression extends Node {
    protected Type type;
    protected boolean isMutable;

    public Expression() {
        type = new Type();
        isMutable = true;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    public void setMutable(boolean mutable) {
        isMutable = mutable;
    }

    public boolean isMutable() {
        return isMutable;
    }
}
