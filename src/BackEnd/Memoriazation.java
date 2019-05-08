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

    private void process(Function function) {
        if(function.canBeMemorized()) {

        }
    }

}
