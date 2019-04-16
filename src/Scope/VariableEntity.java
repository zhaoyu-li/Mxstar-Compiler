package Scope;

import AST.Location;
import IR.Operand.VirtualRegister;
import Type.Type;

public class VariableEntity extends Entity {
    private Type type;
    private boolean isGlobal;
    private Location location;
    private VirtualRegister virtualRegister;

    public VariableEntity() {
        type = null;
        isGlobal = false;
        virtualRegister = null;
    }

    public VariableEntity(Type type, String name) {
        this.type = type;
        this.name = name;
        isGlobal = false;
        virtualRegister = null;
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

    public boolean isGlobal() {
        return isGlobal;
    }

    public void setVirtualRegister(VirtualRegister virtualRegister) {
        this.virtualRegister = virtualRegister;
    }

    public VirtualRegister getVirtualRegister() {
        return virtualRegister;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Location getLocation() {
        return location;
    }
}
