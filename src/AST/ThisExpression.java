package AST;

public class ThisExpression extends Expression {

    public ThisExpression() {}

    @Override
    public void accept(ASTVistor vistor) {
        vistor.visit(this);
    }
}
