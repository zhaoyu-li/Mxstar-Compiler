package AST;

import Scope.ClassEntity;

import java.util.LinkedList;
import java.util.List;

public class ClassDeclaration extends Declaration {
    private FunctionDeclaration constructor;
    private List<VariableDeclaration> fields;
    private List<FunctionDeclaration> methods;
    private ClassEntity classEntity;

    public ClassDeclaration() {
        constructor = null;
        fields = new LinkedList<>();
        methods = new LinkedList<>();
        classEntity = null;
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

    public void setClassEntity(ClassEntity classEntity) {
        this.classEntity = classEntity;
    }

    public ClassEntity getClassEntity() {
        return classEntity;
    }

    @Override
    public void accept(ASTVistor vistor) {
        vistor.visit(this);
    }
}
