package AST;

import Scope.VariableEntity;

public class ThisExpression extends Expression {
    private VariableEntity variableEntity;

    public ThisExpression() {
        variableEntity = new VariableEntity();
    }

    public void setVariableEntity(VariableEntity variableEntity) {
        this.variableEntity = variableEntity;
    }

    public VariableEntity getVariableEntity() {
        return variableEntity;
    }

    @Override
    public void accept(ASTVistor vistor) {
        vistor.visit(this);
    }
}
