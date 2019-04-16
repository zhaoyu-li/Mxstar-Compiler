package IR.Instruction;

import AST.Expression;

public class VariableInit {
    private String name;
    private Expression init;

    public VariableInit(String name, Expression init) {
        this.name = name;
        this.init = init;
    }

    public String getName() {
        return name;
    }

    public Expression getInit() {
        return init;
    }
}
