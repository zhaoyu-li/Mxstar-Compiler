package Type;

public class FunctionType extends Type {
    private String name;

    public FunctionType(String name) {
        type = types.FUNCTION;
        this.name = name;
    }
}
