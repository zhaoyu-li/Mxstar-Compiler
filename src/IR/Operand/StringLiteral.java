package IR.Operand;

import IR.IRVistor;

public class StringLiteral extends Operand {
    private String value;

    public StringLiteral(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public void accept(IRVistor vistor) {
        vistor.visit(this);
    }
}
