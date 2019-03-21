package Scope;

import Type.Type;

public class VariableEntity extends Entity {
    private Type type;

    public VariableEntity(Type type, String name) {
        this.type = type;
        this.name = name;
    }

}
