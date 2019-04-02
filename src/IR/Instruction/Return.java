package IR.Instruction;

import IR.BasicBlock;
import IR.IRVistor;
import IR.Operand.Operand;

public class Return extends Instruction {
    private Operand ret;

    public Return(BasicBlock bb, Operand ret) {
        super(bb);
        this.ret = ret;
    }

    public Operand getRet() {
        return ret;
    }

    @Override
    public void accept(IRVistor vistor) {
        vistor.visit(this);
    }
}
