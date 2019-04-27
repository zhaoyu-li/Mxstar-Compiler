package Scope;

import Type.*;

import java.util.*;

public class GlobalScope extends Scope {
    private Map<String, ClassEntity> classes;
    private HashSet<VariableEntity> globalInitVariables;

    public GlobalScope() {
        classes = new HashMap<String, ClassEntity>();
        globalInitVariables = new HashSet<>();
        putGlobalBuildinFunction();
        putStringBuildinFunction();
        putArrayBuildinFunction();
    }

    public void putClassEntity(String name, ClassEntity classEntity) {
        classes.put(name, classEntity);
    }

    public ClassEntity getClassEntity(String name) {
        return classes.get(name);
    }

    public void putGlobalVariable(VariableEntity variableEntity) {
        globalInitVariables.add(variableEntity);
    }

    public HashSet<VariableEntity> getGlobalInitVariables() {
        return globalInitVariables;
    }

    private FunctionEntity globalPrintFunction() {
        FunctionEntity function = new FunctionEntity();
        function.setName("print");
        function.setReturnType(new Type("void"));
        List<VariableEntity> parameters = new LinkedList<VariableEntity>();
        parameters.add(new VariableEntity(new Type("string"), "str"));
        function.setParameters(parameters);
        return function;
    }

    private FunctionEntity globalPrintlnFunction() {
        FunctionEntity function = new FunctionEntity();
        function.setName("println");
        function.setReturnType(new Type("void"));
        List<VariableEntity> parameters = new LinkedList<VariableEntity>();
        parameters.add(new VariableEntity(new Type("string"), "str"));
        function.setParameters(parameters);
        return function;
    }

    private FunctionEntity globalGetStringFunction() {
        FunctionEntity function = new FunctionEntity();
        function.setName("getString");
        function.setReturnType(new Type("string"));
        List<VariableEntity> parameters = new LinkedList<VariableEntity>();
        function.setParameters(parameters);
        return function;
    }

    private FunctionEntity globalGetIntFunction() {
        FunctionEntity function = new FunctionEntity();
        function.setName("getInt");
        function.setReturnType(new Type("int"));
        List<VariableEntity> parameters = new LinkedList<VariableEntity>();
        function.setParameters(parameters);
        return function;
    }

    private FunctionEntity globalToStringFunction() {
        FunctionEntity function = new FunctionEntity();
        function.setName("toString");
        function.setReturnType(new Type("string"));
        List<VariableEntity> parameters = new LinkedList<VariableEntity>();
        parameters.add(new VariableEntity(new Type("int"), "i"));
        function.setParameters(parameters);
        return function;
    }

    private FunctionEntity stringLengthFunction() {
        FunctionEntity function = new FunctionEntity();
        function.setName("string.length");
        function.setReturnType(new Type("int"));
        List<VariableEntity> parameters = new LinkedList<VariableEntity>();
        function.setParameters(parameters);
        return function;
    }

    private FunctionEntity stringSubstringFunction() {
        FunctionEntity function = new FunctionEntity();
        function.setName("string.substring");
        function.setReturnType(new Type("string"));
        List<VariableEntity> parameters = new LinkedList<VariableEntity>();
        parameters.add(new VariableEntity(new Type("int"), "left"));
        parameters.add(new VariableEntity(new Type("int"), "right"));
        function.setParameters(parameters);
        return function;
    }

    private FunctionEntity stringParseIntFunction() {
        FunctionEntity function = new FunctionEntity();
        function.setName("string.parseInt");
        function.setReturnType(new Type("int"));
        List<VariableEntity> parameters = new LinkedList<VariableEntity>();
        function.setParameters(parameters);
        return function;
    }

    private FunctionEntity stringOrdFunction() {
        FunctionEntity function = new FunctionEntity();
        function.setName("string.ord");
        function.setReturnType(new Type("int"));
        List<VariableEntity> parameters = new LinkedList<VariableEntity>();
        parameters.add(new VariableEntity(new Type("int"), "pos"));
        function.setParameters(parameters);
        return function;
    }

    private FunctionEntity arraySizeFunction() {
        FunctionEntity function = new FunctionEntity();
        function.setName("array.size");
        function.setReturnType(new Type("int"));
        List<VariableEntity> parameters = new LinkedList<VariableEntity>();
        function.setParameters(parameters);
        return function;
    }

    private void putGlobalBuildinFunction() {
        putFunction("print", globalPrintFunction());
        putFunction("println", globalPrintlnFunction());
        putFunction("getString", globalGetStringFunction());
        putFunction("getInt", globalGetIntFunction());
        putFunction("toString", globalToStringFunction());
    }

    private void putStringBuildinFunction() {
        putFunction("string.length", stringLengthFunction());
        putFunction("string.substring", stringSubstringFunction());
        putFunction("string.parseInt", stringParseIntFunction());
        putFunction("string.ord", stringOrdFunction());
    }

    private void putArrayBuildinFunction() {
        putFunction("array.size", arraySizeFunction());
    }
}
