package AST;

public class BinaryExpression extends Expression {
    private String op;
    private Expression lhs;
    private Expression rhs;

    public BinaryExpression(Expression lhs, Expression rhs, Location location) {
        this.lhs = lhs;
        this.rhs = rhs;
        this.location = location;
    }

    @Override
    public void accept(ASTVistor vistor) {
        vistor.visit(this);
    }
}
