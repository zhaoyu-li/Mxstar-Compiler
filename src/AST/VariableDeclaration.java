package AST;


import Symbol.VariableSymbol;

public class VariableDeclaration extends Declaration {
    private TypeNode type;
    private Expression init;
    private VariableSymbol symbol;

    public VariableDeclaration() {
        type = null;
        init = null;
    }

    public VariableDeclaration(TypeNode type, String name, Expression init, Location location) {
        this.type = type;
        this.name = name;
        this.init = init;
        this.location = location;
    }

    public TypeNode getType() {
        return type;
    }

    public void setType(TypeNode type) {
        this.type = type;
    }

    public Expression getInit() {
        return init;
    }

    public void setInit(Expression init) {
        this.init = init;
    }

    @Override
    public void accept(ASTVistor vistor) {
        vistor.visit(this);
    }
}
