package IR.Operand;

import IR.IRVistor;
import IR.Operand.Register;

public class PhysicalRegister extends Register {
    private String name;

    public PhysicalRegister() {}

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
