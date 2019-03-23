package AST;

public class MemberExpression extends Expression {
    private Expression expr;
    private Identifier member;
    private FuncCallExpression funcCall;

    public MemberExpression() {
        expr = null;
        member = null;
        funcCall = null;
    }

    public void setExpr(Expression expr) {
        this.expr = expr;
    }

    public Expression getExpr() {
        return expr;
    }

    public void setMember(Identifier member) {
        this.member = member;
    }

    public Identifier getMember() {
        return member;
    }

    public void setFuncCall(FuncCallExpression funcCall) {
        this.funcCall = funcCall;
    }

    public FuncCallExpression getFuncCall() {
        return funcCall;
    }

    @Override
    public void accept(ASTVistor vistor) {
        vistor.visit(this);
    }
}
