package BackEnd;

import IR.BasicBlock;
import IR.Function;
import IR.IRProgram;
import IR.Instruction.*;
import IR.Operand.*;
import Scope.VariableEntity;
import Utility.Config;

import java.util.HashMap;

import static IR.RegisterSet.vargs;
import static IR.RegisterSet.vrax;

public class Memorization {
    private IRProgram program;
    private HashMap<Function, VirtualRegister> functionTableMap;
    private BasicBlock curBB;

    public Memorization(IRProgram program) {
        this.program = program;
        this.functionTableMap = new HashMap<>();
    }

    public void run() {
        for(Function function : program.getFunctions().values()) {
            if(function.getType() == Function.FuncType.UserDefined) {
                process(function);
            }
        }
        for(Function function : program.getFunctions().values()) {
            if(function.getType() == Function.FuncType.UserDefined) {
                function.calcReversePostOrder();
                function.calcReversePrevOrder();
                function.calcUsedRecursiveVariables();
            }
        }
    }

    private void addGlobalAddress(Function function) {
        Function global_init = program.getFunction("global_init");
        StaticVariable var = new StaticVariable(function.getName(), Config.getRegSize());
        VirtualRegister vr = functionTableMap.get(function);
        vr.setSpillSpace(new Memory(var));
        program.addStaticVariable(var);
        VariableEntity addr = new VariableEntity();
        addr.setVirtualRegister(vr);
        function.addGlobalVariable(addr);
        global_init.addGlobalVariable(addr);
    }

    private void addFunctionTable(Function function) {
        Function global_init = program.getFunction("global_init");
        BasicBlock preHeadBB = global_init.getHeadBB();
        curBB = new BasicBlock("alloc", global_init);
        global_init.setHeadBB(curBB);
        VirtualRegister addr = new VirtualRegister("");
        VirtualRegister size = new VirtualRegister("");
        VirtualRegister bytes = new VirtualRegister("");
        curBB.addNextInst(new Move(curBB, size, new IntImmediate(60)));
        curBB.addNextInst(new Lea(curBB, bytes, new Memory(size, Config.getRegSize(), new IntImmediate(Config.getRegSize()))));
        curBB.addNextInst(new Call(curBB, vrax, program.getFunction("malloc"), bytes));
        curBB.addNextInst(new Move(curBB, addr, vrax));
        curBB.addNextInst(new Move(curBB, new Memory(addr), size));

        BasicBlock condBB = new BasicBlock("allocateConditionBB", global_init);
        BasicBlock bodyBB = new BasicBlock("allocateBodyBB", global_init);
        BasicBlock afterBB = new BasicBlock("allocateAfterBB", global_init);
        curBB.addNextJumpInst(new Jump(curBB, condBB));

        condBB.addNextJumpInst(new CJump(condBB, size, CJump.CompareOp.GT, new IntImmediate(0), bodyBB, afterBB));

        curBB = bodyBB;
        curBB.addNextInst(new Move(curBB, new Memory(addr, size, Config.getRegSize()), new IntImmediate(0)));
        curBB.addNextInst(new UnaryOperation(curBB, UnaryOperation.UnaryOp.DEC, size));
        curBB.addNextJumpInst(new Jump(curBB, condBB));

        curBB = afterBB;
        curBB.addNextJumpInst(new Jump(curBB, preHeadBB));

        global_init.calcReversePostOrder();
        global_init.calcReversePrevOrder();
        functionTableMap.put(function, addr);
    }

    private void modify(Function function) {
        VirtualRegister addr = functionTableMap.get(function);
        VirtualRegister idx = function.getParameters().get(0);

        BasicBlock preHeadBB = function.getHeadBB();
        BasicBlock checkBB = new BasicBlock("checkBB", function);
        BasicBlock returnBB = new BasicBlock("returnBB", function);
        BasicBlock updateBB = function.getTailBB();

        curBB = checkBB;
        curBB.addNextInst(new Move(curBB, addr, addr.getSpillSpace()));
        curBB.addNextInst(new Move(curBB, idx, vargs.get(0)));
        Memory memory = new Memory(addr, idx, Config.getRegSize(), new IntImmediate(Config.getRegSize()));
        curBB.addNextJumpInst(new CJump(curBB, memory, CJump.CompareOp.NE, new IntImmediate(0), returnBB, preHeadBB));

        curBB = returnBB;
        curBB.addNextInst(new Move(curBB, vrax, memory));
        curBB.addNextJumpInst(new Jump(curBB, updateBB));

        curBB = updateBB;
        curBB.addPrevInst(new Move(curBB, memory, vrax));

        Instruction retInst = function.getTailBB().getTail();
        retInst.prepend(new Move(function.getTailBB(), addr.getSpillSpace(), addr));
        function.setHeadBB(checkBB);
    }

    private void process(Function function) {
        if(function.canBeMemorized()) {
            System.err.println("======================= memorization ========================");
            addFunctionTable(function);
            addGlobalAddress(function);
            modify(function);
        }
    }

}
