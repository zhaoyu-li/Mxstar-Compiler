package IR.Instruction;

import IR.IRVistor;

public class Phi extends Instruction {
    @Override
    public void accept(IRVistor vistor) {
        vistor.visit(this);
    }
}
