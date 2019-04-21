package IR;

import IR.Operand.StaticData;
import IR.Operand.StaticString;
import IR.Function.FuncType;
import IR.Operand.StaticVariable;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class IRProgram {
    private Map<String, Function> functions;
    private LinkedList<StaticVariable> staticVariables;
    private LinkedList<StaticString> staticStrings;

    private static Function print;
    private static Function println;
    private static Function getString;
    private static Function getInt;
    private static Function toString;
    private static Function string_length;
    private static Function string_substring;
    private static Function string_parseInt;
    private static Function string_ord;
    private static Function global_init;
    private static Function string_concat;
    private static Function string_compare;
    private static Function malloc;

    private void addBuiltinFunctions() {
        print = new Function(FuncType.Library, "print");
        println = new Function(FuncType.Library, "println");
        getString = new Function(FuncType.Library, "getString");
        getInt = new Function(FuncType.Library, "getInt");
        toString = new Function(FuncType.Library, "toString");
        string_length = new Function(FuncType.Library, "string.length");
        string_substring = new Function(FuncType.Library, "string.substring");
        string_parseInt = new Function(FuncType.Library, "string.parseInt");
        string_ord = new Function(FuncType.Library, "string.ord");
        global_init = new Function(FuncType.Library, "globalInit");
        string_concat = new Function(FuncType.Library, "string.concat");
        string_compare = new Function(FuncType.Library, "string.compare");
        malloc = new Function(FuncType.External, "malloc");

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
