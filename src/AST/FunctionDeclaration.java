package AST;

import Type.FunctionType;
import Type.Type;

import java.util.List;

public class FunctionDeclaration extends Declaration {
    private TypeNode typeNode;
    private List<VariableDeclaration> parameters;
    private List<Statement> body;

    public FunctionDeclaration() {}

    @Override
    public void accept(ASTVistor vistor) {
        vistor.visit(this);
    }
}
