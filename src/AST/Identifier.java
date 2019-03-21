package AST;

public class Identifier extends Expression {
    private String name;

    public Identifier() {
        name = null;
    }

    public Identifier(String name) {
        this.name = name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public void accept(ASTVistor vistor) {
        vistor.visit(this);
    }
}
