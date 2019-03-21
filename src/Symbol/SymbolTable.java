package Symbol;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class SymbolTable {
    private Map<String, VariableSymbol> variables;
    private Map<String, FunctionSymbol> functions;
    private SymbolTable parent;
    private List<SymbolTable> children;

    public SymbolTable(SymbolTable parent) {
        this.variables = new LinkedHashMap<String, VariableSymbol>();
        this.functions = new LinkedHashMap<String, FunctionSymbol>();
        this.parent = parent;
        this.children = new LinkedList<SymbolTable>();
    }

    public VariableSymbol getVariableSymbol(String name) {
        return variables.get(name);
    }

    public void putVariableSymbol(String name, VariableSymbol symbol) {
        variables.put(name, symbol);
    }

    public FunctionSymbol getFunctionSymbol(String name) {
        return functions.get(name);
    }

    public void putFunctionSymbol(String name, FunctionSymbol symbol) {
        functions.put(name, symbol);
    }
    public SymbolTable getParent() {
        return parent;
    }
}
