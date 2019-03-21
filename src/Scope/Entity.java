package Scope;

import AST.Location;

public abstract class Entity {
    protected String name;

    public Entity() {
        name = null;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
