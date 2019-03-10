package AST;

public class BreakStatement extends Statement {
    BreakStatement() {}

    @Override
    public void accept(ASTVistor vistor) {
        vistor.visit(this);
    }
}
