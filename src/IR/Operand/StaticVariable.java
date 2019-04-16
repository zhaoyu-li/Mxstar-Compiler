package IR.Operand;

import IR.IRVistor;

public class StaticVariable extends StaticData {
    private int length;

    public StaticVariable(String name, int length) {
        super(name);
        this.length = length;
    }

    public int getLength() {
        return length;
    }

    @Override
    public void accept(IRVistor vistor) {
        vistor.visit(this);
    }
}
