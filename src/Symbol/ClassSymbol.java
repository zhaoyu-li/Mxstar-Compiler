package Symbol;

import AST.Location;

public class ClassSymbol extends Symbol {
    private SymbolTable symbolTable;

    public ClassSymbol(String name, Location location, SymbolTable parent) {
        this.name = name;
        this.location = location;
        this.symbolTable = new SymbolTable(parent);
    }
    public ClassSymbol(String name, Location location) {
        this.name = name;
        this.location = location;
    }

    public SymbolTable getSymbolTable() {
        return symbolTable;
    }
}
