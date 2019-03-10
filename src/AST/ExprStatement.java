package AST;

public class ExprStatement extends Statement {
    public Expression expression = null;

    public ExprStatement() {}

    @Override
    public void accept(ASTVistor vistor) {
        vistor.visit(this);
    }
}
