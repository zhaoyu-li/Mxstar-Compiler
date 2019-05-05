package IR.Operand;

import IR.IRVistor;

public class VirtualRegister extends Register {
    private String name;
    private PhysicalRegister allocatedPhysicalRegister;
    private Memory spillSpace;

    public VirtualRegister(String name) {
        this.name = name;
        allocatedPhysicalRegister = null;
        spillSpace = null;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setAllocatedPhysicalRegister(PhysicalRegister allocatedPhysicalRegister) {
        this.allocatedPhysicalRegister = allocatedPhysicalRegister;
    }

    public PhysicalRegister getAllocatedPhysicalRegister() {
        return allocatedPhysicalRegister;
    }

    public void setSpillSpace(Memory spillSpace) {
        this.spillSpace = spillSpace;
    }

    public Memory getSpillSpace() {
        return spillSpace;
    }

    @Override
    public void accept(IRVistor vistor) {
        vistor.visit(this);
    }

}
