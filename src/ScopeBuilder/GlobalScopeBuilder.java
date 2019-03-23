package ScopeBuilder;

import AST.*;
import Scope.*;
import Type.*;
import Utility.SemanticError;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class GlobalScopeBuilder extends BaseScopeBuilder {
    private Map<String, ClassEntity> classes;
    private Scope globalScope;
    private Scope curScope;
    private ClassEntity stringEntity;

    public GlobalScopeBuilder() {
        globalScope = new Scope();
        curScope = globalScope;
        classes = new HashMap<String, ClassEntity>();
    }

    public Scope getScope() {
        return globalScope;
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
        globalScope.putFunction("print", globalPrintFunction());
        globalScope.putFunction("println", globalPrintlnFunction());
        globalScope.putFunction("getString", globalGetStringFunction());
        globalScope.putFunction("getInt", globalGetIntFunction());
        globalScope.putFunction("toString", globalToStringFunction());
    }

    private void putStringBuildinFunction() {
        stringEntity.getScope().putFunction("length", stringLengthFunction());
        stringEntity.getScope().putFunction("substring", stringSubstringFunction());
        stringEntity.getScope().putFunction("parseInt", stringParseIntFunction());
        stringEntity.getScope().putFunction("ord", stringOrdFunction());
    }

    private void setBuildinFunction() {
        stringEntity = new ClassEntity();
        putGlobalBuildinFunction();
        putStringBuildinFunction();
        classes.put("string", stringEntity);
    }

    private void enterScope(Scope scope) {
        curScope = scope;
    }

    private void exitScope() {
        curScope = curScope.getParent();
    }

    private VariableEntity getVariableEntity (VariableDeclaration variableDeclaration) {
        VariableEntity variableEntity = new VariableEntity();
        variableEntity.setType(variableDeclaration.getType().getType());
        variableEntity.setName(variableDeclaration.getName());
        return variableEntity;
    }

    private void checkMainFunction() {
        FunctionEntity mainFunction = globalScope.getFunction("main");
        if(mainFunction == null) {
            throw new SemanticError(new Location(0,0), "Cannot find main function");
        } else {
            if(!mainFunction.getReturnType().getType().equals(Type.types.INT)) {
                throw new SemanticError(new Location(0,0), "main function's return type should be int");
            }
            if(!mainFunction.getParameters().isEmpty()) {
                throw new SemanticError(new Location(0,0), "main funciotn should have no parameters");
            }
        }
    }

    @Override
    public void visit(Program program) {
        setBuildinFunction();
        for(ClassDeclaration classDeclaration : program.getClasses()) {
            visit(classDeclaration);
        }

        for(FunctionDeclaration functionDeclaration : program.getFunctions()) {
            visit(functionDeclaration);
        }
        for(VariableDeclaration variableDeclaration : program.getVariables()) {
            visit(variableDeclaration);
        }
        checkMainFunction();
    }

    @Override
    public void visit(ClassDeclaration node) {
        ClassEntity classEntity = new ClassEntity();
        classEntity.setScope(new Scope(globalScope));
        enterScope(classEntity.getScope());
        if(node.getConstructor() != null) {
            visit(node.getConstructor());
        }
        for(FunctionDeclaration functionDeclaration : node.getMethods()) {
            visit(functionDeclaration);
        }
        for(VariableDeclaration variableDeclaration : node.getFields()) {
            visit(variableDeclaration);
        }
        exitScope();
        if(classes.get(node.getName()) != null) {
            throw new SemanticError(node.getLocation(), "Duplicate ClassDeclaration");
        } else {
            classes.put(classEntity.getName(), classEntity);
        }
    }

    @Override
    public void visit(FunctionDeclaration node) {
        FunctionEntity functionEntity = new FunctionEntity();
        functionEntity.setName(node.getName());
        functionEntity.setReturnType(node.getReturnType().getType());
        List<VariableEntity> parameters = new LinkedList<VariableEntity>();
        for(VariableDeclaration variableDeclaration : node.getParameters()) {
            parameters.add(getVariableEntity(variableDeclaration));
        }
        functionEntity.setParameters(parameters);
        //node.setFunctionEntity(functionEntity);
        if(curScope.getFunction(functionEntity.getName()) != null) {
            throw new SemanticError(node.getLocation(), "Duplicate FunctionDeclaration");
        } else {
            curScope.putFunction(functionEntity.getName(), functionEntity);
        }
    }

    @Override
    public void visit(VariableDeclaration node) {
        VariableEntity variableEntity = new VariableEntity();
        checkInitType(node);
        variableEntity.setType(resolveType(node.getType()));
        variableEntity.setName(node.getName());
        if(curScope == globalScope) {
            variableEntity.setGlobal(true);
        }
        if(curScope.getVariable(node.getName()) != null) {
            throw new SemanticError(node.getLocation(), "Duplicate VariableDeclaration");
        } else {
            curScope.putVariable(variableEntity.getName(), variableEntity);
        }
    }

}
