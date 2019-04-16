package IR;

import IR.Operand.StaticData;
import IR.Operand.StaticString;
import IR.Function.FuncType;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class IRProgram {
    private Map<String, Function> functions;
    private List<StaticData> staticDataList;
    private Map<String, StaticString> staticStrings;

    private static Function print;
    private static Function println;
    private static Function getString;
    private static Function getInt;
    private static Function toString;
    private static Function string_length;
    private static Function string_substring;
    private static Function string_parseInt;
    private static Function string_ord;
    private static Function variable_init;

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
        variable_init = new Function(Function.FuncType.Library, "variable_init");

        addFunction(print);
        addFunction(println);
        addFunction(getString);
        addFunction(getInt);
        addFunction(toString);
        addFunction(string_length);
        addFunction(string_substring);
        addFunction(string_parseInt);
        addFunction(string_ord);
        addFunction(variable_init);
    }

    public IRProgram() {
        functions = new HashMap<>();
        staticDataList = new LinkedList<>();
        addBuiltinFunctions();
    }

    public void addFunction(Function func) {
        functions.put(func.getName(), func);
    }

    public Function getFunction(String name) {
        return functions.get(name);
    }

    public void addStaticData(StaticData data) {
        staticDataList.add(data);
    }

    public void addStaticString(StaticString str) {
        staticStrings.put(str.getValue(), str);
    }

    public StaticString getStaticString(String name) {
        return staticStrings.get(name);
    }

    public void accept(IRVistor vistor) {
        vistor.visit(this);
    }
}
