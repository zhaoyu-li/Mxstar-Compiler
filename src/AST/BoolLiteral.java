package AST;

public class BoolLiteral extends Expression {
    private boolean value;

    public BoolLiteral() {
        value = false;
    }

    public void setValue(String ctx) {
        switch (ctx) {
            case "true":
                this.value = true;
                break;
            case "false":
                this.value = false;
                break;
        }
    }

    public boolean getValue() {
        return value;
    }

    @Override
    public void accept(ASTVistor vistor) {
        vistor.visit(this);
    }
}
