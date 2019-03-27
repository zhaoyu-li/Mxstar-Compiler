package AST;

public class NullLiteral extends Expression {

    @Override
    public void accept(ASTVistor vistor) {
        vistor.visit(this);
    }
}
