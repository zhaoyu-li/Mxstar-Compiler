package BackEnd;

import IR.BasicBlock;
import IR.Function;
import IR.IRProgram;
import IR.Instruction.CJump;
import IR.Instruction.Instruction;
import IR.Instruction.Jump;
import IR.Operand.Register;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

public class LivenessAnalyzer {
    private IRProgram program;
    private HashMap<Function, HashSet<Register>> liveOuts;

    public LivenessAnalyzer(IRProgram program) {
        this.program = program;
        this.liveOuts = new HashMap<>();
    }

    public void analysis() {
        for(Function function : program.getFunctions().values()) {
            analysis(function);
        }
    }

    private void analysis(Function function) {
        LinkedList<BasicBlock> reversePreOrder = function.getReversePrevOrder();
        HashSet<Register> liveIn = new HashSet<>();
        HashSet<Register> liveOut = new HashSet<>();
        boolean changed = true;
        while(changed) {
            changed = false;
            for(BasicBlock bb : reversePreOrder) {
                for(Instruction inst = bb.getTail(); inst != null; inst = inst.getPrev()) {
                    liveIn.clear();
                    liveOut.clear();
                    if(inst instanceof Jump) {
                        liveOut.addAll(((Jump) inst).getTargetBB().getHead().getLiveIn());
                    } else if(inst instanceof CJump) {
                        liveOut.addAll(((CJump) inst).getThenBB().getHead().getLiveIn());
                        liveOut.addAll(((CJump) inst).getElseBB().getHead().getLiveIn());
                    } else {
                        if(inst.getNext() != null) {
                            liveOut.addAll(inst.getNext().getLiveIn());
                        }
                    }
                    liveIn.addAll(liveOut);
                    liveOut.removeAll(inst.getDefinedRegisters());
                    liveIn.addAll(inst.getUsedRegisters());
                    if(!inst.getLiveIn().equals(liveIn)) {
                        changed = true;
                        inst.getLiveIn().clear();
                        inst.getLiveIn().addAll(liveIn);
                    }
                    if(!inst.getLiveOut().equals(liveOut)) {
                        changed = true;
                        inst.getLiveOut().clear();
                        inst.getLiveOut().addAll(liveOut);
                    }
                }
            }
        }
        liveOuts.put(function, liveOut);
    }

    public HashSet<Register> getLiveout(Function function) {
        return liveOuts.get(function);
    }
}
