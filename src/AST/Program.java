package AST;

import java.util.LinkedList;
import java.util.List;

public class Program extends Node {
    private List<FunctionDeclaration> functions;
    private List<ClassDeclaration> classes;
    private List<VariableDeclaration> variables;

    public Program() {
        this.functions = new LinkedList<>();
        this.classes = new LinkedList<>();
        this.variables = new LinkedList<>();
    }

    public void add(FunctionDeclaration declaration) {
        functions.add(declaration);
    }

    public void add(ClassDeclaration declaration) {
        classes.add(declaration);
    }

    public void addAll(List<VariableDeclaration> declaration) {
        variables.addAll(declaration);
    }


    @Override
    public void accept(ASTVistor vistor) {
        vistor.visit(this);
    }
}
