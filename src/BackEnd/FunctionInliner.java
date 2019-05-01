package BackEnd;

import IR.BasicBlock;
import IR.Function;
import IR.IRProgram;
import IR.Instruction.Call;
import IR.Instruction.Instruction;
import IR.Instruction.Jump;
import IR.Instruction.Move;
import IR.Operand.Operand;
import IR.Operand.VirtualRegister;
import Utility.Config;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class FunctionInliner {
    private IRProgram program;
    private Map<Function, FuncInfo> funcInfoMap;

    public FunctionInliner(IRProgram program) {
        this.program = program;
        this.funcInfoMap = new HashMap<>();
    }

    private class FuncInfo {
        private boolean deserveInline = true;
        private int instNumber = 0;
        private int callNumber = 0;
    }

    public void run() {
        for(Function function : program.getFunctions().values()) {
            if(function.getType() == Function.FuncType.UserDefined) {
                preprocess(function);
            }
        }
        for(Function function : program.getFunctions().values()) {
            if(function.getType() == Function.FuncType.UserDefined) {
                process(function);
            }
        }
    }

    private void preprocess(Function function) {
        FuncInfo funcInfo = new FuncInfo();
        for(BasicBlock bb : function.getReversePostOrder()) {
            for(Instruction inst = bb.getHead(); inst != null; inst = inst.getNext()) {
                funcInfo.instNumber++;
            }
        }
        if(funcInfo.instNumber > Config.MAX_INLINE_INST || function.getCallees().contains(function)
                || !function.isGlobal() || !function.getUsedGlobalVariables().isEmpty()) {
            funcInfo.deserveInline = false;
            System.out.println(function.getName());
            System.out.println("!!!");
        }
        funcInfoMap.put(function, funcInfo);
    }

    private void process(Function function) {
        FuncInfo caller = funcInfoMap.get(function);
        for(BasicBlock bb : function.getReversePostOrder()) {
            for(Instruction inst = bb.getHead(); inst != null; inst = inst.getNext()) {
                if(inst instanceof Call) {
                    FuncInfo callee = funcInfoMap.get(((Call) inst).getFunc());
                    if(callee != null) {
                        if(callee.deserveInline) {
                            System.out.println(((Call) inst).getFunc().getName());
                            doInline((Call) inst, ((Call) inst).getFunc());
                        }
                    }
                }
            }
        }
    }

    private void doInline(Call call, Function function) {
        for(BasicBlock bb : function.getReversePostOrder()) {
            for(Instruction inst = bb.getHead(); inst != null; inst = inst.getNext()) {
                Instruction newInst = copy(inst);
                call.prepend(newInst);
            }
        }
        System.out.println("finish");
        call.remove();
    }

    private Instruction copy(Instruction inst) {
        return inst;
    }

}
