package Scope;

import AST.Location;
import Type.Type;

public class VariableEntity extends Entity {
    private Type type;
    private boolean isGlobal;
    private Location location;

    public VariableEntity() {
        type = null;
        isGlobal = false;
        location = new Location();
    }

    public VariableEntity(Type type, String name) {
        this.type = type;
        this.name = name;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    public void setGlobal(boolean global) {
        isGlobal = global;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Location getLocation() {
        return location;
    }
}
