package AST;

import Symbol.FunctionSymbol;
import Type.FunctionType;
import Type.Type;

import java.util.List;

public class FunctionDeclaration extends Declaration {
    private TypeNode returnType;
    private List<VariableDeclaration> parameters;
    private List<Statement> body;
    private FunctionSymbol symbol;

    public FunctionDeclaration() {
        returnType = null;
        parameters = null;
        body = null;
    }

    public void setReturnType(TypeNode returnType) {
        this.returnType = returnType;
    }

    public TypeNode getReturnType() {
        return returnType;
    }

    public void setParameters(List<VariableDeclaration> parameters) {
        this.parameters = parameters;
    }

    public List<VariableDeclaration> getParameters() {
        return parameters;
    }

    public void setBody(List<Statement> body) {
        this.body = body;
    }

    public List<Statement> getBody() {
        return body;
    }

    @Override
    public void accept(ASTVistor vistor) {
        vistor.visit(this);
    }
}
