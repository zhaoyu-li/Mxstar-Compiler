package IR;

import IR.Operand.StaticString;
import IR.Function.FuncType;
import IR.Operand.StaticVariable;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class IRProgram {
    private Map<String, Function> functions;
    private LinkedList<StaticVariable> staticVariables;
    private LinkedList<StaticString> staticStrings;

    private void addBuiltinFunctions() {
        Function print = new Function(FuncType.Library, "print", false, true);
        Function println = new Function(FuncType.Library, "println", false, true);
        Function getString = new Function(FuncType.Library, "getString", true, true);
        Function getInt = new Function(FuncType.Library, "getInt", true, true);
        Function toString = new Function(FuncType.Library, "toString", true, true);
        Function string_length = new Function(FuncType.Library, "string_length", true, true);
        Function string_substring = new Function(FuncType.Library, "string_substring", true, true);
        Function string_parseInt = new Function(FuncType.Library, "string_parseInt", true, true);
        Function string_ord = new Function(FuncType.Library, "string_ord", true, true);
        Function string_concat = new Function(FuncType.Library, "string_concat", true, true);
        Function string_compare = new Function(FuncType.Library, "string_compare", true, true);
        Function malloc = new Function(FuncType.External, "malloc", false, true);
        Function global_init = new Function(FuncType.UserDefined, "global_init", true, true);

        addFunction(print);
        addFunction(println);
        addFunction(getString);
        addFunction(getInt);
        addFunction(toString);
        addFunction(string_length);
        addFunction(string_substring);
        addFunction(string_parseInt);
        addFunction(string_ord);
        addFunction(global_init);
        addFunction(string_concat);
        addFunction(string_compare);
        addFunction(malloc);
    }

    public IRProgram() {
        functions = new HashMap<>();
        staticVariables = new LinkedList<>();
        staticStrings = new LinkedList<>();
        addBuiltinFunctions();
    }

    public void addFunction(Function func) {
        functions.put(func.getName(), func);
    }

    public Function getFunction(String name) {
        return functions.get(name);
    }

    public void addStaticVariable(StaticVariable var) {
        staticVariables.add(var);
    }

    public void addStaticString(StaticString str) {
        staticStrings.add(str);
    }

    public Map<String, Function> getFunctions() {
        return functions;
    }

    public LinkedList<StaticVariable> getStaticVariables() {
        return staticVariables;
    }

    public LinkedList<StaticString> getStaticStrings() {
        return staticStrings;
    }

    public void accept(IRVistor vistor) {
        vistor.visit(this);
    }
}
