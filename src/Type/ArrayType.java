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

    @Override
    public boolean match(Type other) {
        if(other instanceof ArrayType) {
            return baseType.match(((ArrayType) other).getBaseType());
        } else if(other.getType() == types.NULL) {
            return true;
        } else {
            return false;
        }
    }
}
