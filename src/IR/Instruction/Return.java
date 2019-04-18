package IR.Instruction;

import IR.BasicBlock;
import IR.IRVistor;

public class Return extends Instruction {
    public Return(BasicBlock bb) {
        super(bb);
    }

    @Override
    public void accept(IRVistor vistor) {
        vistor.visit(this);
    }
}
