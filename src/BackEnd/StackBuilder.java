package BackEnd;

import IR.BasicBlock;
import IR.Function;
import IR.IRProgram;
import IR.Instruction.*;
import IR.Operand.IntImmediate;
import IR.Operand.PhysicalRegister;
import IR.Operand.StackSlot;
import Utility.Config;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

import static IR.RegisterSet.*;

public class StackBuilder {
    private IRProgram program;

    public StackBuilder(IRProgram program) {
        this.program = program;
    }

    public void build() {
        for(Function function : program.getFunctions().values()) {
            if(function.getType() == Function.FuncType.UserDefined)
                buildStack(function);
        }
    }

    private void buildStack(Function function) {
        int parameterOffset = Config.getRegSize() * 2;
        int temporaryOffset = -Config.getRegSize();
        LinkedList<StackSlot> restParameters = new LinkedList<>();
        if(function.getParameters().size() > 6) {
            for(int i = 6; i < function.getParameters().size(); i++) {
                restParameters.add((StackSlot) function.getParameters().get(i).getSpillSpace());
            }
        }

        for(BasicBlock bb : function.getBasicBlocks()) {
            for(Instruction inst = bb.getHead(); inst != null; inst = inst.getNext()) {
                LinkedList<StackSlot> slots = inst.getStackSlots();
                for(StackSlot ss : slots) {
                    if(!restParameters.contains(ss)) {
                        function.addTemporary(ss);
                    }
                }
            }
        }

        for(StackSlot parameter : restParameters) {
            parameter.setBase(rbp);
            parameter.setOffset(new IntImmediate(parameterOffset));
            parameterOffset += Config.getRegSize();
        }
        for(StackSlot temporary : function.getTemporaries()) {
            temporary.setBase(rbp);
            temporary.setOffset(new IntImmediate(temporaryOffset));
            temporaryOffset -= Config.getRegSize();
        }

        int stackSize = Config.getRegSize() * (restParameters.size() + function.getTemporaries().size() + 1);

        BasicBlock headBB = function.getHeadBB();
        Instruction headInst = headBB.getHead();
        headInst.prepend(new Push(headBB, rbp));
        headInst.prepend(new Move(headBB, rbp, rsp));
        for(PhysicalRegister pr : function.getUsedPhysicalRegisters()) {
            headInst.prepend(new Push(headBB, pr));
        }
        headInst.prepend(new BinaryOperation(headBB, rsp, BinaryOperation.BinaryOp.SUB, new IntImmediate(stackSize)));
        BasicBlock tailBB = function.getTailBB();
        Instruction tailInst = tailBB.getTail();
        for(PhysicalRegister pr : function.getUsedPhysicalRegisters()) {
            tailInst.prepend(new Pop(tailBB, pr));
        }
    }

}
