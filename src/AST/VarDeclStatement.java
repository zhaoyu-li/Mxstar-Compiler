package AST;

public class VarDeclStatement extends Statement {
    private VariableDeclaration declaration;

    public VarDeclStatement() {
        declaration = null;
    }

    public VariableDeclaration getDeclaration() {
        return declaration;
    }

    public void setDeclaration(VariableDeclaration declaration) {
        this.declaration = declaration;
    }

    @Override
    public void accept(ASTVistor vistor) {
        vistor.visit(this);
    }
}
