package AST;

import java.util.List;

public class FuncCallExpression extends Expression {
    private String name;
    private List<Expression> arguments;

    public FuncCallExpression() {}

    public String getName() {
        return name;
    }

    @Override
    public void accept(ASTVistor vistor) {
        vistor.visit(this);
    }
}
