package IR.Instruction;

import IR.BasicBlock;
import IR.IRVistor;
import IR.Operand.Operand;

public class Push extends Instruction {
    private Operand src;

    public Push(BasicBlock bb, Operand src) {
        super(bb);
        this.src = src;
    }

    @Override
    public void accept(IRVistor vistor) {
        vistor.visit(this);
    }
}
