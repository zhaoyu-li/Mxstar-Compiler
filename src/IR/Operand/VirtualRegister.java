package IR.Operand;

import IR.IRVistor;

public class VirtualRegister extends Register {
    private String name;
    private PhysicalRegister allocatedPhysicalRegister;
    private Memory spillPlace;

    public VirtualRegister(String name) {
        this.name = name;
        allocatedPhysicalRegister = null;
        spillPlace = null;
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

    public void setSpillPlace(Memory spillPlace) {
        this.spillPlace = spillPlace;
    }

    public Memory getSpillPlace() {
        return spillPlace;
    }

    @Override
    public void accept(IRVistor vistor) {
        vistor.visit(this);
    }

}
