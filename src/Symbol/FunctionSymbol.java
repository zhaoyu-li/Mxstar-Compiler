package Symbol;

import Type.Type;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

public class FunctionSymbol extends Symbol {
    private Type returnType;
    private List<Type> parameterTypes;
    private List<String> parameterNames;
    private SymbolTable symbolTable;

    public FunctionSymbol() {
        this.parameterTypes = new LinkedList<>();
        this.parameterNames = new LinkedList<>();
    }
}
