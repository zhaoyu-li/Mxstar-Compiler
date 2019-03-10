package AST;

public class WhileStatement extends Statement {
    private Expression conditionExpression;
    private BlockStatement body;

    public WhileStatement() {}

    @Override
    public void accept(ASTVistor vistor) {
        vistor.visit(this);
    }
}
