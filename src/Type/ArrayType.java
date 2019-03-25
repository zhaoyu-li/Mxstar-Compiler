package Type;

public class ArrayType extends Type {
    private Type baseType;
    private int dimension;

    public ArrayType(Type basetype, int dimension) {
        type = types.ARRAY;
        this.baseType = basetype;
        this.dimension = dimension;
    }

    public int getDimension() {
        return dimension;
    }

    public Type getBaseType() {
        return baseType;
    }

}
