package AST;

public abstract class Node {
    protected Location location;

    public Node() {}

    abstract public void accept(ASTVistor vistor);
}
