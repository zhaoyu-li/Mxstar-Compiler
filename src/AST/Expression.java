package AST;

import Type.Type;

public abstract class Expression extends Node {
    protected Type type;

    public Expression() {
        type = null;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Type getType() {
        return type;
    }
}
