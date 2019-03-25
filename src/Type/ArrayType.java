package Type;

public class ArrayType extends Type {
    private Type baseType;

    public ArrayType(Type basetype) {
        type = types.ARRAY;
        this.baseType = basetype;
    }

    @Override
    public types getType() {
        return super.getType();
    }

    public Type getBaseType() {
        return baseType;
    }

}
