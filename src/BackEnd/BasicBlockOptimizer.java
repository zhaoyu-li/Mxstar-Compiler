package BackEnd;

import IR.BasicBlock;
import IR.Function;
import IR.IRProgram;
import IR.Instruction.CJump;
import IR.Instruction.Instruction;
import IR.Instruction.Jump;

import java.util.ArrayList;

public class BasicBlockOptimizer {
    private IRProgram program;
    private BasicBlock nextBB;

    public BasicBlockOptimizer(IRProgram program) {
        this.program = program;
        nextBB = null;
    }

    public void run() {
        for(Function function : program.getFunctions().values()) {
            if(function.getType() == Function.FuncType.UserDefined) {
                process(function);
            }
        }
    }

    private void process(Function function) {
        ArrayList<BasicBlock> reversePostOrder = new ArrayList<>(function.getReversePostOrder());
        for(int i = 0; i < reversePostOrder.size(); i++) {
            BasicBlock bb = reversePostOrder.get(i);
            nextBB = (i + 1 == reversePostOrder.size()) ? null : reversePostOrder.get(i + 1);
            for(Instruction inst = bb.getHead(); inst != null; inst = inst.getNext()) {
                if(inst instanceof Jump) {
                    for(Instruction extra = inst.getNext(); extra != null; extra = extra.getNext()) {
                        extra.remove();
                    }
                    if(((Jump) inst).getTargetBB() == nextBB) {
                        inst.remove();
                    }
                    break;
                } else if(inst instanceof CJump) {
                    for(Instruction extra = inst.getNext(); extra != null; extra = extra.getNext()) {
                        extra.remove();
                    }
                    if(((CJump) inst).getThenBB() == nextBB) {
                        ((CJump) inst).setOp(((CJump) inst).getNegativeCompareOp());
                        ((CJump) inst).setThenBB(((CJump) inst).getElseBB());
                        ((CJump) inst).setElseBB(nextBB);
                    }
                    break;
                }
            }
        }
        for(int i = 0; i < reversePostOrder.size(); i++) {
            BasicBlock bb = reversePostOrder.get(i);
            nextBB = (i + 1 == reversePostOrder.size()) ? null : reversePostOrder.get(i + 1);
            for(Instruction inst = bb.getHead(); inst != null; inst = inst.getNext()) {
                if(inst instanceof CJump) {
                    if(((CJump) inst).getThenBB() == nextBB) {

                    }
                }
            }
        }
    }
}
