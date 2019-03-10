package Type;

public class ClassType extends Type {
    private String name;

    public ClassType(String name) {
        type = types.CLASS;
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
