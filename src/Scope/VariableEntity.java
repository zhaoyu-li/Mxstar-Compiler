package Scope;

import Type.Type;

public class VariableEntity extends Entity {
    private Type type;

    public VariableEntity() {}

    public void setType(Type type) {
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    //    public VariableEntity(Type type, String name) {
//        this.type = type;
//        this.name = name;
//    }

}
