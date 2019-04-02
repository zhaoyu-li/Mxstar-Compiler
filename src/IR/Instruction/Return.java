package IR.Instruction;

import IR.IRVistor;

public class Return extends Instruction {
    @Override
    public void accept(IRVistor vistor) {
        vistor.visit(this);
    }
}
