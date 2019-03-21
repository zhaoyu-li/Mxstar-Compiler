package AST;

import Symbol.ClassSymbol;

import java.util.List;

public class ClassDeclaration extends Declaration {
    private FunctionDeclaration constructor;
    private List<VariableDeclaration> fields;
    private List<FunctionDeclaration> methods;
    private ClassSymbol symbol;

    public ClassDeclaration() {
        constructor = null;
        fields = null;
        methods = null;
    }

    public void setConstructor(FunctionDeclaration constructor) {
        this.constructor = constructor;
    }

    public FunctionDeclaration getConstructor() {
        return constructor;
    }

    public void setFields(List<VariableDeclaration> fields) {
        this.fields = fields;
    }

    public List<VariableDeclaration> getFields() {
        return fields;
    }

    public void setMethods(List<FunctionDeclaration> methods) {
        this.methods = methods;
    }

    public List<FunctionDeclaration> getMethods() {
        return methods;
    }

    @Override
    public void accept(ASTVistor vistor) {
        vistor.visit(this);
    }
}
