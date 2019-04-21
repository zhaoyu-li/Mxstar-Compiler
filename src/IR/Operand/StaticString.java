package IR.Operand;

import IR.IRVistor;

public class StaticString extends StaticData {
    private String value;

    public StaticString(String value) {
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
