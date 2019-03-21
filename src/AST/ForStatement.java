package AST;

public class ForStatement extends Statement {
    private Expression init;
    private Expression condition;
    private Expression update;
    private Statement body;

    public ForStatement() {
        init = null;
        condition = null;
        update = null;
        body = null;
    }

    public void setInit(Expression init) {
        this.init = init;
    }

    public Expression getInit() {
        return init;
    }

    public void setCondition(Expression condition) {
        this.condition = condition;
    }

    public Expression getCondition() {
        return condition;
    }

    public void setUpdate(Expression update) {
        this.update = update;
    }

    public Expression getUpdate() {
        return update;
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
