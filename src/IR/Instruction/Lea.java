package IR.Instruction;

import IR.BasicBlock;
import IR.IRVistor;
import IR.Operand.Memory;
import IR.Operand.Register;

public class Lea extends Instruction {
    private Register dst;
    private Memory src;

    public Lea(BasicBlock bb, Register dst, Memory src) {
        super(bb);
        this.dst = dst;
        this.src = src;
    }

    @Override
    public void accept(IRVistor vistor) {
        vistor.visit(this);
    }
}
