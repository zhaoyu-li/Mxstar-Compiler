package AST;

public class BreakStatement extends Statement {
    public BreakStatement() {}

    @Override
    public void accept(ASTVistor vistor) {
        vistor.visit(this);
    }
}
