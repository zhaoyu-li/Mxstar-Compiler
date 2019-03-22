package Scope;

import Type.Type;

public class VariableEntity extends Entity {
    private Type type;

    public VariableEntity() {
        type = null;
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

}
