package AST;

public class ReturnStatement extends Statement {
    private Expression returnExpression;

    public ReturnStatement() {}

    @Override
    public void accept(ASTVistor vistor) {
        vistor.visit(this);
    }
}
