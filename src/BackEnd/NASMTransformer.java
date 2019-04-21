package BackEnd;

import IR.*;
import IR.Instruction.*;
import IR.Operand.*;
import Scope.VariableEntity;

import java.util.HashSet;


public class NASMTransformer implements IRVistor {
    public NASMTransformer() {
    }

    @Override
    public void visit(IRProgram node) {
        for(Function function : node.getFunctions().values()) {
            function.accept(this);
        }
    }

    @Override
    public void visit(Function node) {
        for(BasicBlock bb : node.getBasicBlocks()) {
            bb.accept(this);
        }
    }

    @Override
    public void visit(BasicBlock node) {
        for(Instruction inst = node.getHead(); inst != null; inst = inst.getNext()) {
            inst.accept(this);
        }
    }

    @Override
    public void visit(Jump node) {

    }

    @Override
    public void visit(CJump node) {

    }

    @Override
    public void visit(Return node) {

    }

    @Override
    public void visit(BinaryOperation node) {

    }

    @Override
    public void visit(UnaryOperation node) {

    }

    @Override
    public void visit(Move node) {

    }

    @Override
    public void visit(Lea node) {

    }

    private PhysicalRegister getPhysical(Operand v) {
        if(v instanceof VirtualRegister)
            return ((VirtualRegister) v).getAllocatedPhysicalRegister();
        else
            return null;
    }

    @Override
    public void visit(Load node) {
        node.append(new Move(node.getBB(), node.getDst(), node.getSrc()));
        node.remove();
        if(node.getSrc() instanceof Memory && node.getDst() instanceof Memory) {
            VirtualRegister vr = new VirtualRegister("");
            node.prepend(new Move(node.getBB(), vr, node.getSrc()));
            node.setSrc(vr);
        } else {
            PhysicalRegister pdst = getPhysical(node.getDst());
            PhysicalRegister psrc = getPhysical(node.getSrc());
            if(pdst != null && node.getSrc() instanceof Memory) {
                VirtualRegister vr = new VirtualRegister("");
                node.prepend(new Move(node.getBB(), vr, node.getSrc()));
                node.setSrc(vr);
            } else if(psrc != null && node.getDst() instanceof Memory) {
                VirtualRegister vr = new VirtualRegister("");
                node.prepend(new Move(node.getBB(), vr, node.getDst()));
                node.setDst(vr);
            }
        }
    }

    @Override
    public void visit(Store node) {
        node.append(new Move(node.getBB(), node.getDst(), node.getSrc()));
        node.remove();
        if(node.getSrc() instanceof Memory && node.getDst() instanceof Memory) {
            VirtualRegister vr = new VirtualRegister("");
            node.prepend(new Move(node.getBB(), vr, node.getSrc()));
            node.setSrc(vr);
        } else {
            PhysicalRegister pdst = getPhysical(node.getDst());
            PhysicalRegister psrc = getPhysical(node.getSrc());
            if(pdst != null && node.getSrc() instanceof Memory) {
                VirtualRegister vr = new VirtualRegister("");
                node.prepend(new Move(node.getBB(), vr, node.getSrc()));
                node.setSrc(vr);
            } else if(psrc != null && node.getDst() instanceof Memory) {
                VirtualRegister vr = new VirtualRegister("");
                node.prepend(new Move(node.getBB(), vr, node.getDst()));
                node.setDst(vr);
            }
        }
    }

    @Override
    public void visit(Call node) {
        Function caller = node.getBB().getFunction();
        Function callee = node.getFunc();
        HashSet<VariableEntity> callerUsed = caller.getUsedGlobalVariables();
        HashSet<VariableEntity> calleeUsed = callee.getUsedRecursiveVariables();
        for(VariableEntity var : callerUsed) {
            if(calleeUsed.contains(var)) {
                node.prepend(new Store(node.getBB(), var.getVirtualRegister().getSpillPlace(), var.getVirtualRegister()));
                node.getPrev().accept(this);
            }
        }
        while(node.getArgs().size() > 6) {
            node.prepend(new Push(node.getBB(), node.getArgs().removeLast()));
        }
        for(int i = node.getArgs().size() - 1; i >= 0; i--) {
            node.prepend(new Store(node.getBB(), RegisterSet.vargs.get(i), node.getArgs().get(i)));
            node.getPrev().accept(this);
        }
        for(VariableEntity var : callerUsed) {
            if(calleeUsed.contains(var)) {
                node.append(new Move(node.getBB(), var.getVirtualRegister(), var.getVirtualRegister().getSpillPlace()));
            }
        }
    }

    @Override
    public void visit(Push node) {

    }

    @Override
    public void visit(Pop node) {

    }

    @Override
    public void visit(Memory node) {

    }

    @Override
    public void visit(StackSlot node) {

    }

    @Override
    public void visit(VirtualRegister node) {

    }

    @Override
    public void visit(PhysicalRegister node) {

    }

    @Override
    public void visit(IntImmediate node) {

    }

    @Override
    public void visit(StaticVariable node) {

    }

    @Override
    public void visit(StaticString node) {

    }
}
