package AST;

public class ArrayExpression extends Expression {


    @Override
    public void accept(ASTVistor vistor) {
        vistor.visit(this);
    }
}
