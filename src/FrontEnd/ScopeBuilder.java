package FrontEnd;

import AST.*;
import Scope.*;
import Type.Type;

import java.util.LinkedList;
import java.util.List;

public class ScopeBuilder implements ASTVistor {
    private GlobalScopeBuilder globalScope;
    private Scope currentScope;

    public ScopeBuilder() {
        globalScope = new GlobalScopeBuilder();
        currentScope = globalScope.getScope();
    }

    private void enterScope(Scope scope) {
        currentScope = scope;
    }

    private void exitScope() {
        currentScope = currentScope.getParent();
    }

    private Type getType(TypeNode typeNode) {
        return typeNode.getType();
    }

    private VariableEntity getVariableEntity (VariableDeclaration variableDeclaration) {
        VariableEntity variableEntity = new VariableEntity();
        variableEntity.setType(getType(variableDeclaration.getType()));
        return variableEntity;
    }

    private void addClass(ClassDeclaration classDeclaration) {
        ClassEntity classEntity = new ClassEntity();
        classEntity.setName(classDeclaration.getName());
        classEntity.setScope(new Scope(globalScope.getScope()));
        classDeclaration.setClassEntity(classEntity);
        globalScope.putClassEntity(classEntity.getName(), classEntity);
    }

    private void addClassFunction(ClassDeclaration classDeclaration) {
        ClassEntity classEntity = globalScope.getClassEntity(classDeclaration.getName());
        enterScope(classEntity.getScope());
        if(classDeclaration.getConstructor() != null) {
            addFunction(classDeclaration.getConstructor());
        }
        for(FunctionDeclaration functionDeclaration : classDeclaration.getMethods()) {
            addFunction(functionDeclaration);
        }
        exitScope();
    }

    private void addFunction(FunctionDeclaration functionDeclaration) {
        FunctionEntity functionEntity = new FunctionEntity();
        functionEntity.setName(functionDeclaration.getName());
        functionEntity.setReturnType(getType(functionDeclaration.getReturnType()));
        List<VariableEntity> parameters = new LinkedList<VariableEntity>();
        for(VariableDeclaration variableDeclaration : functionDeclaration.getParameters()) {
            parameters.add(getVariableEntity(variableDeclaration));
        }
        functionEntity.setParameters(parameters);
        currentScope.putFunction(functionEntity.getName(), functionEntity);
    }

    @Override
    public void visit(Program node) {
        for(ClassDeclaration classDeclaration : node.getClasses()) {
            addClass(classDeclaration);
        }
        for(FunctionDeclaration functionDeclaration : node.getFunctions()) {
            addFunction(functionDeclaration);
        }

    }

    @Override
    public void visit(Declaration node) {

    }
    @Override
    public void visit(FunctionDeclaration node) {

    }
    @Override
    public void visit(ClassDeclaration node) {}
    @Override
    public void visit(VariableDeclaration node) {}
    @Override
    public void visit(TypeNode node) {}
    @Override
    public void visit(ArrayTypeNode node) {}

    @Override
    public void visit(Statement node) {}
    @Override
    public void visit(IfStatement node) {}
    @Override
    public void visit(WhileStatement node) {}
    @Override
    public void visit(ForStatement node) {}
    @Override
    public void visit(BreakStatement node) {}
    @Override
    public void visit(ContinueStatement node) {}
    @Override
    public void visit(ReturnStatement node) {}
    @Override
    public void visit(ExprStatement node) {}
    @Override
    public void visit(VarDeclStatement node) {}
    @Override
    public void visit(BlockStatement node) {}

    @Override
    public void visit(Expression node) {}
    @Override
    public void visit(ThisExpression node) {}
    @Override
    public void visit(NullLiteral node) {}
    @Override
    public void visit(BoolLiteral node) {}
    @Override
    public void visit(IntLiteral node) {}
    @Override
    public void visit(StringLiteral node) {}
    @Override
    public void visit(Identifier node) {}
    @Override
    public void visit(MemberExpression node) {}
    @Override
    public void visit(ArrayExpression node) {}
    @Override
    public void visit(FuncCallExpression node) {}
    @Override
    public void visit(NewExpression node) {}
    @Override
    public void visit(SuffixExpression node) {}
    @Override
    public void visit(PrefixExpression node) {}
    @Override
    public void visit(BinaryExpression node) {}
    @Override
    public void visit(AssignExpression node) {}

}
