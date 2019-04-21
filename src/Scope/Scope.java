package Scope;

import Utility.Config;

import java.util.*;

public class Scope {
    private Map<String, VariableEntity> variables;
    private Map<String, FunctionEntity> functions;
    private Map<String, Integer> offsets;
    private Scope parent;
    private Integer curOffset;

    public Scope() {
        this.variables = new LinkedHashMap<String, VariableEntity>();
        this.functions = new LinkedHashMap<String, FunctionEntity>();
        this.offsets = new HashMap<>();
        this.parent = null;
        curOffset = 0;
    }

    public Scope(Scope parent) {
        this.variables = new LinkedHashMap<String, VariableEntity>();
        this.functions = new LinkedHashMap<String, FunctionEntity>();
        this.offsets = new HashMap<>();
        this.parent = parent;
        curOffset = 0;
    }

    public Scope getParent() {
        return parent;
    }

    public void putVariable(String name, VariableEntity variable) {
        variables.put(name, variable);
        offsets.put(name, curOffset);
        curOffset += Config.getRegSize();
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

    public FunctionEntity getRecursiveFunction(String name) {
        FunctionEntity functionEntity = functions.get(name);
        if(functionEntity != null) {
            return functionEntity;
        } else {
            if(parent != null) {
                return parent.getRecursiveFunction(name);
            } else {
                return null;
            }
        }
    }

    public int getVariableOffset(String name) {
        return offsets.get(name);
    }
}
