package AST;

public class WhileStatement extends Statement {
    private Expression condition;
    private Statement body;

    public WhileStatement() {
        condition = null;
        body = null;
    }

    public void setCondition(Expression condition) {
        this.condition = condition;
    }

    public Expression getCondition() {
        return condition;
    }

    public void setBody(Statement body) {
        this.body = body;
    }

    public Statement getBody() {
        return body;
    }

    @Override
    public void accept(ASTVistor vistor) {
        vistor.visit(this);
    }
}
