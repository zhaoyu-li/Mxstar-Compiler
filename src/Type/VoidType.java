package Type;

public class VoidType extends Type {
    static private VoidType voidtype = new VoidType();

    public VoidType() {
        type = types.VOID;
    }
}
