package Scope;

import Type.Type;

import java.util.List;
import java.util.Map;

public class FunctionEntity extends Entity {
    private Type returnType;
    private List<VariableEntity> parameters;
    private Map<String, VariableEntity> variables;
    private Map<String, VariableEntity> globalVariables;
    private Scope scope;

    public FunctionEntity() {
        returnType = null;
        parameters = null;
        variables = null;
        globalVariables = null;
        scope = null;
    }

    public void setReturnType(Type returnType) {
        this.returnType = returnType;
    }

    public Type getReturnType() {
        return returnType;
    }

    public void setParameters(List<VariableEntity> parameters) {
        this.parameters = parameters;
    }

    public List<VariableEntity> getParameters() {
        return parameters;
    }

    public void putVariables(String name, VariableEntity variableEntity) {
        variables.put(name, variableEntity);
    }

    public Map<String, VariableEntity> getVariables() {
        return variables;
    }

    public void setGlobalVariables(Map<String, VariableEntity> globalVariables) {
        this.globalVariables = globalVariables;
    }

    public Map<String, VariableEntity> getGlobalVariables() {
        return globalVariables;
    }

    public void setScope(Scope scope) {
        this.scope = scope;
    }

    public Scope getScope() {
        return scope;
    }
}
