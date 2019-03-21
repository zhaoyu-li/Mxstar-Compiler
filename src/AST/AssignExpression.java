package AST;

public class AssignExpression extends Expression {
    private Expression lhs;
    private Expression rhs;

    public AssignExpression() {
        lhs = null;
        rhs = null;
    }

    public void setLhs(Expression lhs) {
        this.lhs = lhs;
    }

    public Expression getLhs() {
        return lhs;
    }

    public void setRhs(Expression rhs) {
        this.rhs = rhs;
    }

    public Expression getRhs() {
        return rhs;
    }

    @Override
    public void accept(ASTVistor vistor) {
        vistor.visit(this);
    }
}
