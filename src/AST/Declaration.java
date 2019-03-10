package AST;

public abstract class Declaration extends Node {
    protected String name;

    Declaration() {}

    public String getName() {
        return name;
    }
}
