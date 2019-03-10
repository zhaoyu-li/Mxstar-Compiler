package AST;

public class IfStatement extends Statement {
    private Expression condition;
    private Statement thenStatement;
    private Statement elseStatement;

    public IfStatement() {}

    @Override
    public void accept(ASTVistor vistor) {
        vistor.visit(this);
    }
}
