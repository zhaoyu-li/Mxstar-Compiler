package BackEnd;

import IR.BasicBlock;
import IR.Function;
import IR.IRProgram;
import IR.Instruction.Call;
import IR.Instruction.Instruction;
import IR.Operand.PhysicalRegister;

import java.util.LinkedList;

public class SimpleAlocator {
    private IRProgram program;
    private LinkedList<PhysicalRegister> physicalRegisters;

    public SimpleAlocator(IRProgram program) {
        this.program = program;
    }

    private void processFunction(Function function) {
        for(BasicBlock bb : function.getBasicBlocks()) {
            for(Instruction inst = bb.getHead(); inst != null; inst = inst.getNext()) {
                if(inst instanceof Call) continue;

            }
        }
    }
}
