package AST;

public class IntLiteral extends Expression {
    private int value;

    public IntLiteral() {
        value = 0;
    }

    public void setValue(String ctx) {
        this.value = Integer.parseInt(ctx);
    }

    public int getValue() {
        return value;
    }

    @Override
    public void accept(ASTVistor vistor) {
        vistor.visit(this);
    }
}
