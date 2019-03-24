package Type;

public class ArrayType extends Type {
    private Type baseType;

    public ArrayType(Type basetype) {
        type = types.ARRAY;
        this.baseType = basetype;
    }

    public Type getBaseType() {
        return baseType;
    }

}
