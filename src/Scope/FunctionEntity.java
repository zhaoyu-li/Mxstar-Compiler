package Scope;

import IR.Function;
import Type.Type;

import java.util.*;

public class FunctionEntity extends Entity {
    private Type returnType;
    private List<VariableEntity> parameters;
    private Map<String, VariableEntity> variables;
    private HashSet<VariableEntity> globalVariables;
    private Scope scope;

    public FunctionEntity() {
        returnType = new Type();
        parameters = new LinkedList<>();
        variables = new HashMap<>();
        globalVariables = new HashSet<>();
        scope = new Scope();
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

    public void addGlobalVariable(VariableEntity globalVariable) {
        globalVariables.add(globalVariable);
    }

    public HashSet<VariableEntity> getGlobalVariables() {
        return globalVariables;
    }

    public void setScope(Scope scope) {
        this.scope = scope;
    }

    public Scope getScope() {
        return scope;
    }
}
