package AST;

public class MemberExpression extends Expression {
    private Expression expr;
    private Identifier member;

    public MemberExpression() {
        expr = null;
        member = null;
    }

    public void setExpr(Expression expr) {
        this.expr = expr;
    }

    public Expression getExpr() {
        return expr;
    }

    public void serMember(String membername) {
        this.member = new Identifier(membername);
    }

    public Identifier getMember() {
        return member;
    }

    @Override
    public void accept(ASTVistor vistor) {
        vistor.visit(this);
    }
}
