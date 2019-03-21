package AST;

public abstract class Declaration extends Node {
    protected String name;

    Declaration() {
        name = null;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
