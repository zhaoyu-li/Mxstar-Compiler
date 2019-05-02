package BackEnd;

import IR.BasicBlock;
import IR.Function;
import IR.IRProgram;
import IR.Instruction.*;
import IR.Operand.*;


import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

public class DeadCodeEliminator {
    IRProgram program;
    LivenessAnalyzer livenessAnalyzer;
    private HashMap<BasicBlock, HashSet<VirtualRegister>> liveOut;

    public DeadCodeEliminator(IRProgram program) {
        this.program = program;
        this.livenessAnalyzer = new LivenessAnalyzer(program);
    }

    public void run() {
        for(Function function : program.getFunctions().values()) {
            process(function);
        }
    }

    private LinkedList<VirtualRegister> trans(LinkedList<Register> set) {
        LinkedList<VirtualRegister> retSet = new LinkedList<>();
        for(Register register : set) {
            assert register instanceof VirtualRegister;
            retSet.add((VirtualRegister) register);
        }
        return retSet;
    }

    private boolean isRemovable(Instruction inst) {
        return !(inst instanceof Return || inst instanceof Leave || inst instanceof Call || inst instanceof Cdq
                || inst instanceof Push || inst instanceof Pop || inst instanceof Jump || inst instanceof CJump);
    }

    private void process(Function function) {
        liveOut = livenessAnalyzer.getLiveOuts(function);
        for(BasicBlock bb : function.getBasicBlocks()) {
            HashSet<VirtualRegister> liveSet = new HashSet<>(liveOut.get(bb));
            for(Instruction inst = bb.getTail(); inst != null; inst = inst.getPrev()) {
                LinkedList<Register> usedSet = inst instanceof Call ? ((Call) inst).getCallUsed() : inst.getUsedRegisters();
                LinkedList<Register> definedSet = inst.getDefinedRegisters();
                boolean dead = true;
                if(definedSet.isEmpty())
                    dead = false;
                for(Register register : definedSet) {
                    VirtualRegister vr = (VirtualRegister)register;
                    if(!dead) break;
                    if(liveSet.contains(vr) || vr.getSpillSpace() != null) {
                        dead = false;
                        break;
                    }
                }
                if(dead && isRemovable(inst)) {
                    inst.remove();
                } else {
                    liveSet.removeAll(trans(definedSet));
                    liveSet.addAll(trans(usedSet));
                }
            }
        }
    }

}
