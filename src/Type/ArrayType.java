package Type;

import java.lang.module.Configuration;

public class ArrayType extends Type {
    private Type basetype;

    static private ArrayType arraytype = new ArrayType(null);

    public ArrayType(Type basetype) {
        type = types.ARRAY;
        this.basetype = basetype;
    }

    public Type getBasetype() {
        return basetype;
    }

}
