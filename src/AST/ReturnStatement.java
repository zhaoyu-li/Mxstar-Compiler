package AST;

public class ReturnStatement extends Statement {
    private Expression ret;

    public ReturnStatement() {
        ret = null;
    }

    public Expression getRet() {
        return ret;
    }

    public void setRet(Expression ret) {
        this.ret = ret;
    }

    @Override
    public void accept(ASTVistor vistor) {
        vistor.visit(this);
    }
}
