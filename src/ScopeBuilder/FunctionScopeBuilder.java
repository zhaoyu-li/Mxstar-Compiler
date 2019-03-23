package ScopeBuilder;

import Scope.*;
import AST.*;
import Type.Type;

public class FunctionScopeBuilder extends BaseScopeBuilder {
    private Scope globalScope;
    private Scope curScope;
    private Type curReturnType;
    private Type curClassType;
    private FunctionEntity curFunction;

    public FunctionScopeBuilder(Scope globalScope) {
        this.globalScope = globalScope;
        curScope = globalScope;
        curReturnType = null;
        curClassType = null;
        curFunction = null;
    }

    public Scope getGlobalScope() {
        return globalScope;
    }

    private void enterScope(Scope scope) {
        curScope = scope;
    }

    private void exitScope() {
        curScope = curScope.getParent();
    }

    @Override
    public void visit(Program node) {
        for(ClassDeclaration classDeclaration : node.getClasses()) {
            visit(classDeclaration);
        }
        for(FunctionDeclaration functionDeclaration : node.getFunctions()) {
            visit(functionDeclaration);
        }
    }

    @Override
    public void visit(FunctionDeclaration node) {

    }
}
