package AST;

import Type.Type;

public class TypeNode extends Node{
    protected Type type;

    public TypeNode() {
        type = new Type();
    }

    public TypeNode(Type type, Location location) {
        this.type = type;
        this.location = location;
    }

    public TypeNode(TypeNode typeNode) {
        type = typeNode.getType();
    }

    public TypeNode(Type type) {
        this.type = type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    public boolean isPrimitiveType() {
        switch (type.getType()) {
            case INT:
            case BOOL:
            case VOID:
            case STRING:
                return true;
            default:
                return false;
        }
    }

    public boolean isClassType() {
        return type.getType() == Type.types.CLASS;
    }

    public boolean isArrayType() {return  type.getType() == Type.types.ARRAY; }

    @Override
    public void accept(ASTVistor vistor) {
        vistor.visit(this);
    }
}
