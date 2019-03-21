package AST;

public class IfStatement extends Statement {
    private Expression condition;
    private Statement elseStatement;

    public IfStatement() {
        condition = null;
        elseStatement = null;
    }

    public void setCondition(Expression condition) {
        this.condition = condition;
    }

    public Expression getCondition() {
        return condition;
    }

    public void setElseStatement(Statement elseStatement) {
        this.elseStatement = elseStatement;
    }

    public Statement getElseStatement() {
        return elseStatement;
    }

    @Override
    public void accept(ASTVistor vistor) {
        vistor.visit(this);
    }
}
