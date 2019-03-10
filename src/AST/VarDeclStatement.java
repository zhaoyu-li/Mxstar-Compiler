package AST;

public class VarDeclStatement extends Statement {
    private VariableDeclaration declaration;

    public VarDeclStatement() {}

    public VariableDeclaration getDeclaration() {
        return declaration;
    }

    @Override
    public void accept(ASTVistor vistor) {
        vistor.visit(this);
    }
}
