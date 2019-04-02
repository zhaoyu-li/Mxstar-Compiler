package IR.Operand;

import IR.IRVistor;

public class IntImmediate extends Operand {
    private int value;

    public IntImmediate(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    @Override
    public void accept(IRVistor vistor) {
        vistor.visit(this);
    }

}
