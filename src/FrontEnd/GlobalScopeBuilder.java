package FrontEnd;

import Scope.*;
import Type.*;

import java.util.*;

public class GlobalScopeBuilder {
    private Map<String, ClassEntity> classes;
    private Scope scope;
    private ClassEntity stringEntity;

    public GlobalScopeBuilder() {
        scope = new Scope();
        stringEntity = new ClassEntity();
        classes = new HashMap<String, ClassEntity>();
        putGlobalBuildinFunction();
        putStringBuildinFunction();
        classes.put("string", stringEntity);
    }

    public Scope getScope() {
        return scope;
    }

    public void putClassEntity(String name, ClassEntity classEntity) {
        classes.put(name, classEntity);
    }

    public ClassEntity getClassEntity(String name) {
        return classes.get(name);
    }

    private FunctionEntity globalPrintFunction() {
        FunctionEntity printFunction = new FunctionEntity();
        printFunction.setName("print");
        printFunction.setReturnType(new Type("void"));
        List<VariableEntity> parameters = new LinkedList<VariableEntity>();
        parameters.add(new VariableEntity(new Type("string"), "str"));
        printFunction.setParameters(parameters);
        return printFunction;
    }

    private FunctionEntity globalPrintlnFunction() {
        FunctionEntity printFunction = new FunctionEntity();
        printFunction.setName("println");
        printFunction.setReturnType(new Type("void"));
        List<VariableEntity> parameters = new LinkedList<VariableEntity>();
        parameters.add(new VariableEntity(new Type("string"), "str"));
        printFunction.setParameters(parameters);
        return printFunction;
    }

    private FunctionEntity globalGetStringFunction() {
        FunctionEntity printFunction = new FunctionEntity();
        printFunction.setName("getString");
        printFunction.setReturnType(new Type("string"));
        List<VariableEntity> parameters = new LinkedList<VariableEntity>();
        printFunction.setParameters(parameters);
        return printFunction;
    }

    private FunctionEntity globalGetIntFunction() {
        FunctionEntity printFunction = new FunctionEntity();
        printFunction.setName("getInt");
        printFunction.setReturnType(new Type("int"));
        List<VariableEntity> parameters = new LinkedList<VariableEntity>();
        printFunction.setParameters(parameters);
        return printFunction;
    }

    private FunctionEntity globalToStringFunction() {
        FunctionEntity printFunction = new FunctionEntity();
        printFunction.setName("toString");
        printFunction.setReturnType(new Type("string"));
        List<VariableEntity> parameters = new LinkedList<VariableEntity>();
        parameters.add(new VariableEntity(new Type("int"), "i"));
        printFunction.setParameters(parameters);
        return printFunction;
    }

    private FunctionEntity stringLengthFunction() {
        FunctionEntity printFunction = new FunctionEntity();
        printFunction.setName("length");
        printFunction.setReturnType(new Type("int"));
        List<VariableEntity> parameters = new LinkedList<VariableEntity>();
        printFunction.setParameters(parameters);
        return printFunction;
    }

    private FunctionEntity stringSubstringFunction() {
        FunctionEntity printFunction = new FunctionEntity();
        printFunction.setName("substring");
        printFunction.setReturnType(new Type("string"));
        List<VariableEntity> parameters = new LinkedList<VariableEntity>();
        parameters.add(new VariableEntity(new Type("int"), "left"));
        parameters.add(new VariableEntity(new Type("int"), "right"));
        printFunction.setParameters(parameters);
        return printFunction;
    }

    private FunctionEntity stringParseIntFunction() {
        FunctionEntity printFunction = new FunctionEntity();
        printFunction.setName("parseInt");
        printFunction.setReturnType(new Type("int"));
        List<VariableEntity> parameters = new LinkedList<VariableEntity>();
        printFunction.setParameters(parameters);
        return printFunction;
    }

    private FunctionEntity stringOrdFunction() {
        FunctionEntity printFunction = new FunctionEntity();
        printFunction.setName("ord");
        printFunction.setReturnType(new Type("int"));
        List<VariableEntity> parameters = new LinkedList<VariableEntity>();
        parameters.add(new VariableEntity(new Type("int"), "pos"));
        printFunction.setParameters(parameters);
        return printFunction;
    }

    private void putGlobalBuildinFunction() {
        scope.putFunction("print", globalPrintFunction());
        scope.putFunction("println", globalPrintlnFunction());
        scope.putFunction("getString", globalGetStringFunction());
        scope.putFunction("getInt", globalGetIntFunction());
        scope.putFunction("toString", globalToStringFunction());
    }

    private void putStringBuildinFunction() {
        stringEntity.getScope().putFunction("length", stringLengthFunction());
        stringEntity.getScope().putFunction("substring", stringSubstringFunction());
        stringEntity.getScope().putFunction("parseInt", stringParseIntFunction());
        stringEntity.getScope().putFunction("ord", stringOrdFunction());
    }


}
