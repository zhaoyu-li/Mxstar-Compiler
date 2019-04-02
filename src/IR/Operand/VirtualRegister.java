package IR.Operand;

import IR.IRVistor;

public class VirtualRegister extends Register {
    private String name;
    private PhysicalRegister forcedPhysicalRegister;

    public VirtualRegister() {
        name = null;
        forcedPhysicalRegister = null;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public void accept(IRVistor vistor) {
        vistor.visit(this);
    }

}
