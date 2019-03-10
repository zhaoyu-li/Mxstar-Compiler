package AST;

import java.util.List;

public class ClassDeclaration extends Declaration {
    private List<VariableDeclaration> fields;
    private List<FunctionDeclaration> methods;
    private FunctionDeclaration constructor;

    public ClassDeclaration() {}

    @Override
    public void accept(ASTVistor vistor) {
        vistor.visit(this);
    }
}
