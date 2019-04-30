package BackEnd;

import IR.BasicBlock;
import IR.Function;
import IR.IRProgram;
import IR.Instruction.*;
import IR.Operand.IntImmediate;
import IR.Operand.PhysicalRegister;
import IR.Operand.StackSlot;
import IR.RegisterSet;
import Utility.Config;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

import static IR.RegisterSet.*;

public class StackBuilder {
    private IRProgram program;
    private HashMap<Function, Frame> framesMap;

    class Frame {
        public LinkedList<StackSlot> parameters = new LinkedList<>();
        public LinkedList<StackSlot> temporaries = new LinkedList<>();
        public int getFrameSize() {
            int bytes = Config.getRegSize() * (parameters.size() + temporaries.size());
            bytes = (bytes + 16 - 1) / 16 * 16; //  round up to a multiply of 16
            return bytes;
        }
    }

    public StackBuilder(IRProgram program) {
        this.program = program;
        this.framesMap = new HashMap<>();
    }

    public void build() {
        for(Function function : program.getFunctions().values()) {
            if(function.getType() == Function.FuncType.UserDefined)
                buildStack(function);
        }
    }

    private void buildStack(Function function) {
        Frame frame = new Frame();
        framesMap.put(function, frame);
        if(function.getParameters().size() > 6) {
            for(int i = 6; i < function.getParameters().size(); i++) {
                frame.parameters.add((StackSlot) function.getParameters().get(i).getSpillSpace());
            }
        }
        HashSet<StackSlot> slotsSet = new HashSet<>();
        for(BasicBlock bb : function.getBasicBlocks()) {
            for(Instruction inst = bb.getHead(); inst != null; inst = inst.getNext()) {
                LinkedList<StackSlot> slots = inst.getStackSlots();
                for(StackSlot ss : slots) {
                    if(!frame.parameters.contains(ss)) {
                        slotsSet.add(ss);
                    }
                }
            }
        }
        frame.temporaries.addAll(slotsSet);
        for(int i = 0; i < frame.parameters.size(); i++) {
            StackSlot ss = frame.parameters.get(i);
            ss.setBase(rbp);
            ss.setOffset(new IntImmediate(16 + 8 * i));
        }
        for(int i = 0; i < frame.temporaries.size(); i++) {
            StackSlot ss = frame.temporaries.get(i);
            ss.setBase(rbp);
            ss.setOffset(new IntImmediate(-8 - 8 * i));
        }
        BasicBlock headBB = function.getHeadBB();
        headBB.addPrevInst(new BinaryOperation(headBB, rsp, BinaryOperation.BinaryOp.SUB, new IntImmediate(frame.getFrameSize())));
        Instruction headInst = headBB.getHead();
        headInst.prepend(new Push(headBB, rbp));
        headInst.prepend(new Move(headBB, rbp, rsp));

        for(PhysicalRegister pr : function.getUsedPhysicalRegisters()) {
            headInst.append(new Push(headBB, pr));
        }

        BasicBlock tailBB = function.getTailBB();
        Instruction tailInst = tailBB.getTail();
        for(PhysicalRegister pr : function.getUsedPhysicalRegisters()) {
            tailInst.prepend(new Pop(tailBB, pr));
        }
        tailInst.prepend(new Leave(tailBB));
    }

}
