package AST;

public class ForStatement extends Statement {
    private Expression initExpr = null;
    private Expression conditionExpr = null;
    private Expression updateExpr = null;
    private BlockStatement body = null;

    public ForStatement() {}

    @Override
    public void accept(ASTVistor vistor) {
        vistor.visit(this);
    }
}
