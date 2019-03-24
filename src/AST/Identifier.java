package AST;

import Scope.VariableEntity;

public class Identifier extends Expression {
    private String name;
    private VariableEntity variableEntity;

    public Identifier() {
        name = null;
        variableEntity = new VariableEntity();
    }

    public Identifier(String name) {
        this.name = name;
        variableEntity = new VariableEntity();
    }

    public void setVariableEntity(VariableEntity variableEntity) {
        this.variableEntity = variableEntity;
    }

    public VariableEntity getVariableEntity() {
        return variableEntity;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public void accept(ASTVistor vistor) {
        vistor.visit(this);
    }
}
