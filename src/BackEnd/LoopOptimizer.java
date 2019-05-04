package BackEnd;

import IR.BasicBlock;
import IR.Function;
import IR.IRProgram;

public class LoopOptimizer {
    private IRProgram program;
    private BasicBlock curLoopConditionBB;
    private BasicBlock curIfThenBB;

    public LoopOptimizer(IRProgram program) {
        this.program = program;
        this.curLoopConditionBB = null;
        this.curIfThenBB = null;
    }

    public void run() {
        for(Function function : program.getFunctions().values()) {
            if(function.getType() == Function.FuncType.UserDefined) {
                process(function);
            }
        }
    }

    private void process(Function function) {

    }
}
