package AST;

public class BinaryExpression extends Expression {
    private String op;
    private Expression lhs;
    private Expression rhs;

    public BinaryExpression() {
        op = null;
        lhs = null;
        rhs = null;
    }

    public void setOp(String op) {
        this.op = op;
    }

    public String getOp() {
        return op;
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
