package BackEnd;

import IR.Function;
import IR.IRProgram;
import IR.Operand.PhysicalRegister;

import java.util.HashMap;

public class Memoriazation {
    private IRProgram program;
    private HashMap<Function, PhysicalRegister> functionTableMap;

    public Memoriazation(IRProgram program) {
        this.program = program;
    }

    public void run() {
        for(Function function : program.getFunctions().values()) {
            if(function.getType() == Function.FuncType.UserDefined) {
                process(function);
            }
        }
    }

    private boolean deserveMemorization(Function function) {
        return function.isGlobal()
                && function.getUsedGlobalVariables().isEmpty()
                && function.getParameters().size() == 1
                && !function.hasOutput();

    }

    private void process(Function function) {

    }

}
