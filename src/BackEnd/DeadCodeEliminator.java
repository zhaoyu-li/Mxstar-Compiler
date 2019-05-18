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
    private IRProgram program;
    private LivenessAnalyzer livenessAnalyzer;

    public DeadCodeEliminator(IRProgram program) {
        this.program = program;
        this.livenessAnalyzer = new LivenessAnalyzer();
    }

    public void run() {
        for(Function function : program.getFunctions().values()) {
            if(function.getType() == Function.FuncType.UserDefined)
                process(function);
        }
    }

    private HashSet<VirtualRegister> trans(LinkedList<Register> registers) {
        HashSet<VirtualRegister> virtualRegisters = new HashSet<>();
        for(Register reg : registers) {
            VirtualRegister vr = (VirtualRegister) reg;
            virtualRegisters.add(vr);
        }
        return virtualRegisters;
    }

    private void process(Function function) {
        HashMap<BasicBlock, HashSet<VirtualRegister>> liveOut = livenessAnalyzer.getOUTs(function,false);
        for(BasicBlock bb : function.getBasicBlocks()) {
            HashSet<VirtualRegister> live = new HashSet<>(liveOut.get(bb));
            for(Instruction inst = bb.getTail(); inst != null; inst = inst.getPrev()) {
                LinkedList<Register> use = inst instanceof Call ? ((Call) inst).getAllUsedRegister() : inst.getUsedRegisters();
                LinkedList<Register> def = inst.getDefinedRegisters();
                boolean dead = true;
                if(def.isEmpty())
                    dead = false;
                for(Register register : def) {
                    VirtualRegister vr = (VirtualRegister) register;
                    if(live.contains(vr) || vr.getSpillSpace() != null) {
                        dead = false;
                        break;
                    }
                }

                if(dead && isRemovable(inst)) {
                    inst.remove();
                } else {
                    live.removeAll(trans(def));
                    live.addAll(trans(use));
                }
            }
        }
    }

    private boolean isRemovable(Instruction inst) {
        return !(inst instanceof Return || inst instanceof Leave || inst instanceof Call || inst instanceof Cdq
                || inst instanceof Push || inst instanceof Pop || inst instanceof Jump || inst instanceof CJump);
    }

}
