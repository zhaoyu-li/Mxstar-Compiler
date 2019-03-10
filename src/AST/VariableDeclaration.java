package AST;


public class VariableDeclaration extends Declaration {
    private TypeNode type;
    private Expression init;

    public VariableDeclaration() {}

    public VariableDeclaration(TypeNode type, String name, Expression init, Location location) {
        this.type = type;
        this.name = name;
        this.init = init;
        this.location = location;
    }

    public TypeNode getType() {
        return type;
    }

    public Expression getInit() {
        return init;
    }

    @Override
    public void accept(ASTVistor vistor) {
        vistor.visit(this);
    }
}
