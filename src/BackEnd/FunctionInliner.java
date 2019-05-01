package BackEnd;

import IR.BasicBlock;
import IR.Function;
import IR.IRProgram;
import IR.Instruction.Call;
import IR.Instruction.Instruction;
import Utility.Config;

import java.util.HashMap;
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
                FuncInfo funcInfo = new FuncInfo();
                funcInfoMap.put(function, funcInfo);
            }
        }
        for(Function function : program.getFunctions().values()) {
            if(function.getType() == Function.FuncType.UserDefined) {
                process(function);
            }
        }
    }

    private void process(Function function) {
        FuncInfo funcInfo = funcInfoMap.get(function);
        for(BasicBlock bb : function.getReversePostOrder()) {
            for(Instruction inst = bb.getHead(); inst != null; inst = inst.getNext()) {
                funcInfo.instNumber++;
            }
        }
        if(funcInfo.instNumber > Config.MAX_INLINE_INST) {
            funcInfo.deserveInline = false;
        }
        if(function.getCallees().contains(function)) {
            funcInfo.deserveInline = false;
        }
        
        for(BasicBlock bb : function.getReversePostOrder()) {
            for(Instruction inst = bb.getHead(); inst != null; inst = inst.getNext()) {
                funcInfo.instNumber++;
            }
        }


    }

}
