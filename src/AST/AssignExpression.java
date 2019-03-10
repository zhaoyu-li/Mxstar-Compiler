package AST;

public class AssignExpression extends Expression {
    private Expression lhs;
    private Expression rhs;

    public AssignExpression(Expression lhs, Expression rhs, Location location) {
        this.lhs = lhs;
        this.rhs = rhs;
        this.location = location;
    }

    public Expression getLhs() {
        return lhs;
    }

    public Expression getRhs() {
        return rhs;
    }

    @Override
    public void accept(ASTVistor vistor) {
        vistor.visit(this);
    }
}
