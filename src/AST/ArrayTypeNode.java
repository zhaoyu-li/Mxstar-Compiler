package AST;

import Type.Type;

public class ArrayTypeNode extends TypeNode {
    private int dimension;

    public ArrayTypeNode() {
        dimension = 1;
    }

    public ArrayTypeNode(Type type, Location location, int dimension) {
        this.type = type;
        this.location = location;
        this.dimension = dimension;
    }

    public ArrayTypeNode(TypeNode typeNode, int dimension) {
        this.type = typeNode.type;
        this.location = typeNode.getLocation();
        this.dimension = dimension;
    }

    public void setDimension(int dimension) {
        this.dimension = dimension;
    }

    public int getDimension() {
        return dimension;
    }

    @Override
    public void accept(ASTVistor vistor) {
        vistor.visit(this);
    }
}
