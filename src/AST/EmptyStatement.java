package AST;

public class EmptyStatement extends Statement {

    @Override
    public void accept(ASTVistor vistor) {
        vistor.visit(this);
    }
}
