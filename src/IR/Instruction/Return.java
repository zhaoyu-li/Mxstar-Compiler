package IR.Instruction;

import IR.BasicBlock;
import IR.IRVistor;
import IR.Operand.Register;

import java.util.LinkedList;

import static IR.RegisterSet.vrax;

public class Return extends Instruction {
    public Return(BasicBlock bb) {
        super(bb);
    }

    @Override
    public LinkedList<Register> getUsedRegisters(){
        LinkedList<Register> registers = new LinkedList<>();
        if(this.getBB().getFunction().hasReturnValue()) {
            registers.add(vrax);
        }
        return registers;
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
