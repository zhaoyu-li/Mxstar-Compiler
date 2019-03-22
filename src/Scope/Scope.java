package Scope;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Scope {
    private Map<String, VariableEntity> variables;
    private Map<String, FunctionEntity> functions;
    private Scope parent;
    private List<Scope> children;

    public Scope() {
        this.variables = new LinkedHashMap<String, VariableEntity>();
        this.functions = new LinkedHashMap<String, FunctionEntity>();
        this.parent = null;
        this.children = new LinkedList<Scope>();
    }

    public Scope(Scope parent) {
        this.variables = new LinkedHashMap<String, VariableEntity>();
        this.functions = new LinkedHashMap<String, FunctionEntity>();
        this.parent = parent;
        this.children = new LinkedList<Scope>();
    }

    public Scope getParent() {
        return parent;
    }

    public void putVariable(String name, VariableEntity variable) {
        variables.put(name, variable);
    }

    public VariableEntity getVariable(String name) {
        return variables.get(name);
    }

    public VariableEntity getRecursiveVariable(String name) {
        VariableEntity variableEntity = variables.get(name);
        if(variableEntity != null) {
            return variableEntity;
        } else {
            if(parent != null) {
                return parent.getRecursiveVariable(name);
            } else {
                return null;
            }
        }
    }

    public void putFunction(String name, FunctionEntity function) {
        functions.put(name, function);
    }

    public FunctionEntity getFunction(String name) {
        return functions.get(name);
    }



}
