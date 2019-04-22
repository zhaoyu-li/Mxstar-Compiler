package BackEnd;

import IR.BasicBlock;
import IR.Function;
import IR.IRProgram;
import IR.Instruction.Call;
import IR.Instruction.Instruction;
import IR.Operand.Operand;
import IR.Operand.PhysicalRegister;
import IR.Operand.VirtualRegister;

import static IR.RegisterSet.*;

import java.util.LinkedList;

public class SimpleAlocator {
    private IRProgram program;
    private LinkedList<PhysicalRegister> physicalRegisters;

    public SimpleAlocator(IRProgram program) {
        this.program = program;
        this.physicalRegisters.add(rbx);
        this.physicalRegisters.add(r10);
        this.physicalRegisters.add(r11);
        this.physicalRegisters.add(r12);
        this.physicalRegisters.add(r13);
        this.physicalRegisters.add(r14);
        this.physicalRegisters.add(r15);
    }

    public void allocateRegisters() {
        for(Function function : program.getFunctions().values()) {
            allocateRegsiters(function);
        }
    }
    private PhysicalRegister getPhysialRegister(Operand vr) {
        if(vr instanceof VirtualRegister) {
            return ((VirtualRegister) vr).getAllocatedPhysicalRegister();
        } else return null;
    }

    private void allocateRegsiters(Function function) {
        for(BasicBlock bb : function.getBasicBlocks()) {
            for(Instruction inst = bb.getHead(); inst != null; inst = inst.getNext()) {
                if(inst instanceof Call) continue;

                
            }
        }
    }
}
