package AST;

public class ContinueStatement extends Statement {
    ContinueStatement() {}

    @Override
    public void accept(ASTVistor vistor) {
        vistor.visit(this);
    }
}
