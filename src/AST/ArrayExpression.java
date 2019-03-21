package AST;

public class ArrayExpression extends Expression {
    private Expression arr;
    private Expression idx;

    public ArrayExpression() {
        arr = null;
        idx = null;
    }

    public void setArr(Expression arr) {
        this.arr = arr;
    }

    public Expression getArr() {
        return arr;
    }

    public void setIdx(Expression idx) {
        this.idx = idx;
    }

    public Expression getIdx() {
        return idx;
    }

    @Override
    public void accept(ASTVistor vistor) {
        vistor.visit(this);
    }
}
