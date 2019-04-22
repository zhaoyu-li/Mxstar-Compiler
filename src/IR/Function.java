package IR;

import IR.Instruction.Return;
import IR.Operand.PhysicalRegister;
import IR.Operand.StackSlot;
import IR.Operand.VirtualRegister;
import Scope.VariableEntity;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Stack;

public class Function {
    public enum FuncType {
        UserDefined, Library, External
    }
    private FuncType type;
    private String name;
    private Boolean hasReturnValue;

    private BasicBlock headBB;
    private BasicBlock tailBB;
    private LinkedList<VirtualRegister> parameters;
    private LinkedList<StackSlot> temporaries;

    private HashSet<Function> callee;
    private HashSet<Function> visitedFunction;

    private HashSet<VariableEntity> usedGlobalVariables;
    private HashSet<VariableEntity> usedRecursiveVariables;
    private HashSet<PhysicalRegister> usedPhysicalRegisters;
    private LinkedList<Return> returnList;

    private LinkedList<BasicBlock> basicBlocks;

    private int stackSize;



    public Function(FuncType type, String name, boolean hasReturnValue) {
        this.type = type;
        this.name = name;
        this.hasReturnValue = hasReturnValue;
        parameters = new LinkedList<>();
        temporaries = new LinkedList<>();
        callee = new HashSet<>();
        visitedFunction = new HashSet<>();
        usedGlobalVariables = new HashSet<>();
        usedRecursiveVariables = new HashSet<>();
        usedPhysicalRegisters = new HashSet<>();
        returnList = new LinkedList<>();
        basicBlocks = new LinkedList<>();
    }

    public FuncType getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public Boolean hasReturnValue() {
        return hasReturnValue;
    }

    public void setHeadBB(BasicBlock headBB) {
        this.headBB = headBB;
    }

    public BasicBlock getHeadBB() {
        return headBB;
    }

    public void setTailBB(BasicBlock tailBB) {
        this.tailBB = tailBB;
    }

    public BasicBlock getTailBB() {
        return tailBB;
    }

    public void addParameter(VirtualRegister parameter) {
        parameters.add(parameter);
    }

    public LinkedList<VirtualRegister> getParameters() {
        return parameters;
    }

    public void addTemporary(StackSlot temporary) {
        temporaries.add(temporary);
    }

    public LinkedList<StackSlot> getTemporaries() {
        return temporaries;
    }

    public void addCallee(Function function) {
        callee.add(function);
    }

    public HashSet<Function> getCallees() {
        return callee;
    }

    public void addGlobalVariable(VariableEntity var) {
        usedGlobalVariables.add(var);
    }

    public HashSet<VariableEntity> getUsedGlobalVariables() {
        return usedGlobalVariables;
    }

    public void addUsedRecursiveVariables(Function function) {
        if(visitedFunction.contains(function)) return;
        visitedFunction.add(function);
        for(Function func : function.callee) {
            addUsedRecursiveVariables(func);
        }
        usedRecursiveVariables.addAll(function.usedGlobalVariables);
    }

    public HashSet<VariableEntity> getUsedRecursiveVariables() {
        return usedRecursiveVariables;
    }

    public HashSet<PhysicalRegister> getUsedPhysicalRegisters() {
        return usedPhysicalRegisters;
    }

    public void addReturn(Return ret) {
        returnList.add(ret);
    }

    public LinkedList<Return> getReturnList() {
        return returnList;
    }

    public void addBasicBlock(BasicBlock bb) {
        basicBlocks.add(bb);
    }

    public LinkedList<BasicBlock> getBasicBlocks() {
        return basicBlocks;
    }

    public void accept(IRVistor vistor) {
        vistor.visit(this);
    }
}
