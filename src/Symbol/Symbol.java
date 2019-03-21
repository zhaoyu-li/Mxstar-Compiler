package Symbol;

import AST.Location;

public abstract class Symbol {
    protected String name;
    protected Location location;

    public Symbol() {}

    public String getName() {
        return name;
    }

    public Location getLocation() {
        return location;
    }
}
