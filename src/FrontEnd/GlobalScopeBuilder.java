package FrontEnd;

import Scope.*;
import Type.*;

import java.util.*;

public class GlobalScopeBuilder {
    private Map<String, ClassEntity> classes;
    private Scope scope;
    private ClassEntity stringEntity;
    private ClassEntity arrayEntity;

    public GlobalScopeBuilder() {
        scope = new Scope();
        stringEntity = new ClassEntity();
        arrayEntity = new ClassEntity();
        classes = new HashMap<String, ClassEntity>();
        putGlobalBuildinFunction();
        putStringBuildinFunction();
        putArrayBuildinFunciotn();
        classes.put("string", stringEntity);
        classes.put("array", arrayEntity);
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

    private FunctionEntity globalfunction() {
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
        function.setName("length");
        function.setReturnType(new Type("int"));
        List<VariableEntity> parameters = new LinkedList<VariableEntity>();
        function.setParameters(parameters);
        return function;
    }

    private FunctionEntity stringSubstringFunction() {
        FunctionEntity function = new FunctionEntity();
        function.setName("substring");
        function.setReturnType(new Type("string"));
        List<VariableEntity> parameters = new LinkedList<VariableEntity>();
        parameters.add(new VariableEntity(new Type("int"), "left"));
        parameters.add(new VariableEntity(new Type("int"), "right"));
        function.setParameters(parameters);
        return function;
    }

    private FunctionEntity stringParseIntFunction() {
        FunctionEntity function = new FunctionEntity();
        function.setName("parseInt");
        function.setReturnType(new Type("int"));
        List<VariableEntity> parameters = new LinkedList<VariableEntity>();
        function.setParameters(parameters);
        return function;
    }

    private FunctionEntity stringOrdFunction() {
        FunctionEntity function = new FunctionEntity();
        function.setName("ord");
        function.setReturnType(new Type("int"));
        List<VariableEntity> parameters = new LinkedList<VariableEntity>();
        parameters.add(new VariableEntity(new Type("int"), "pos"));
        function.setParameters(parameters);
        return function;
    }

    private FunctionEntity arraySizeFunction() {
        FunctionEntity function = new FunctionEntity();
        function.setName("size");
        function.setReturnType(new Type("int"));
        List<VariableEntity> parameters = new LinkedList<VariableEntity>();
        function.setParameters(parameters);
        return function;
    }

    private void putGlobalBuildinFunction() {
        scope.putFunction("print", globalfunction());
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

    private void putArrayBuildinFunciotn() {
        arrayEntity.getScope().putFunction("size", arraySizeFunction());
    }
}
