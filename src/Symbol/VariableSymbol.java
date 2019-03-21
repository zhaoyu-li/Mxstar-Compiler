package Symbol;

import AST.Location;
import Type.Type;

public class VariableSymbol extends Symbol {
    private Type type;
    //private boolean isClassField;
    //private boolean isGlobalVariable;

    public VariableSymbol(String name, Location location, Type type) {
        this.name = name;
        this.type = type;
        this.location = location;
    }

    public Type getType() {
        return type;
    }
}
