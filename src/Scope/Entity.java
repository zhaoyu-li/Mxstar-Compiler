package Scope;

import AST.Location;

public abstract class Entity {
    protected String name;
    protected Location location;

    public Entity() {}

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Location getLocation() {
        return location;
    }
}
