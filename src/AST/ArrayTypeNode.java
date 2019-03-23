package AST;

import Type.Type;

public class ArrayTypeNode extends TypeNode {
    private TypeNode baseType;
    private int dimension;

    public ArrayTypeNode() {
        type.setType(Type.types.ARRAY);
        baseType = new TypeNode();
        dimension = 1;
    }

    public ArrayTypeNode(TypeNode typeNode, int dimension) {
        type.setType(Type.types.ARRAY);
        location = typeNode.getLocation();
        baseType = typeNode;
        this.dimension = dimension;
    }

    public TypeNode getBaseType() {
        return baseType;
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
