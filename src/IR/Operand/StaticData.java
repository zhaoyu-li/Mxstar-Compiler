package IR.Operand;

public abstract class StaticData extends Constant {
    private String name;

    public StaticData(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
