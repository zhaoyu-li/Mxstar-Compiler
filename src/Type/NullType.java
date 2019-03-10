package Type;

public class NullType extends Type {
    static private NullType nulltype = new NullType();

    public NullType() {
        type = types.NULL;
    }
}
