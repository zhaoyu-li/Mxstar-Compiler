package Scope;

import Type.Type;

import java.util.List;

public class FunctionEntity extends Entity {
    private Type returnType;
    private List<VariableEntity> parameters;

    public FunctionEntity() {
        returnType = null;
        parameters = null;
    }

    public FunctionEntity(Type returnType, String name, List<VariableEntity> parameters) {
        this.returnType = returnType;
        this.name = name;
        this.parameters = parameters;
    }

    public void setReturnType(Type returnType) {
        this.returnType = returnType;
    }

    public void setParameters(List<VariableEntity> parameters) {
        this.parameters = parameters;
    }
}
