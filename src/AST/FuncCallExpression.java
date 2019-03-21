package AST;

import Scope.FunctionEntity;

import java.util.List;

public class FuncCallExpression extends Expression {
    private Identifier name;
    private List<Expression> arguments;

    private FunctionEntity functionEntity;

    public FuncCallExpression() {
        name = null;
        arguments = null;

    }

    public void setName(String identifiername) {
        this.name = new Identifier(identifiername);
    }

    public Identifier getName() {
        return name;
    }

    public void setArguments(List<Expression> arguments) {
        this.arguments = arguments;
    }

    public List<Expression> getArguments() {
        return arguments;
    }

    @Override
    public void accept(ASTVistor vistor) {
        vistor.visit(this);
    }
}
