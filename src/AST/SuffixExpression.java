package AST;

public class SuffixExpression extends Expression {
    private String op;
    private Expression expr;

    public SuffixExpression() {
        op = null;
        expr = null;
    }

    public void setOp(String op) {
        this.op = op;
    }

    public String getOp() {
        return op;
    }

    public void setExpr(Expression expr) {
        this.expr = expr;
    }

    public Expression getExpr() {
        return expr;
    }

    @Override
    public void accept(ASTVistor vistor) {
        vistor.visit(this);
    }
}
