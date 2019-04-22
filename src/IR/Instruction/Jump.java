package IR.Instruction;

import IR.BasicBlock;
import IR.IRVistor;
import IR.Operand.Register;

import java.util.LinkedList;

public class Jump extends Instruction {
    private BasicBlock targetBB;

    public Jump(BasicBlock bb, BasicBlock targetBB) {
        super(bb);
        this.targetBB = targetBB;
    }

    public BasicBlock getTargetBB() {
        return targetBB;
    }

    @Override
    public LinkedList<Register> getUsedRegisters(){
        return new LinkedList<>();
    }

    @Override
    public LinkedList<Register> getDefinedRegisters() {
        return new LinkedList<>();
    }

    @Override
    public void accept(IRVistor vistor) {
        vistor.visit(this);
    }
}
