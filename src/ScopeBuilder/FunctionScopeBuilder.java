package ScopeBuilder;

import FrontEnd.ScopeBuilder;
import Scope.*;
import AST.*;
import Type.Type;
import Utility.SemanticError;

public class FunctionScopeBuilder extends BaseScopeBuilder {
    private GlobalScopeBuilder globalScope;
    private Scope curScope;
    private Type curReturnType;
    private Type curClassType;
    private FunctionEntity curFunction;

    public FunctionScopeBuilder(GlobalScopeBuilder globalScope) {
        this.globalScope = globalScope;
        curScope = globalScope.getScope();
        curReturnType = null;
        curClassType = null;
        curFunction = null;
    }

    public GlobalScopeBuilder getGlobalScope() {
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
        curFunction = curScope.getFunction(node.getName());
        curReturnType.setType(curFunction.getReturnType().getType());
        for(Statement statement : node.getBody()) {
            statement.accept(this);
        }
        curFunction = null;
    }

    @Override
    public void visit(VariableDeclaration node) {
        VariableEntity variableEntity = new VariableEntity();
        variableEntity.setType(resolveType(node.getType()));
        variableEntity.setName(node.getName());
        if(curScope == globalScope.getScope()) {
            variableEntity.setGlobal(true);
        }
        if(curScope.getVariable(node.getName()) != null) {
            throw new SemanticError(node.getLocation(), "Duplicate VariableDeclaration");
        } else {
            curScope.putVariable(variableEntity.getName(), variableEntity);
        }
    }

    @Override
    public void visit(ClassDeclaration node) {
        ClassEntity classEntity = globalScope.getClassEntity(node.getName());
        enterScope(classEntity.getScope());
        if(node.getConstructor() != null) {
            visit(node.getConstructor());
        }
        for(FunctionDeclaration functionDeclaration : node.getMethods()) {
            visit(functionDeclaration);
        }
        exitScope();
    }

    @Override
    public void visit(IfStatement node) {
        node.getCondition().accept(this);
        node.getThenStatement().accept(this);
        if(node.getElseStatement() != null) {
            node.getElseStatement().accept(this);
        }
    }

    @Override
    public void visit(WhileStatement node) {
        node.getCondition().accept(this);
        node.getBody().accept(this);
    }

    @Override
    public void visit(ForStatement node) {
        if(node.getInit() != null) {
            node.getInit().accept(this);
        }
        if(node.getCondition() != null) {
            node.getCondition().accept(this);
        }
        if(node.getUpdate() != null) {
            node.getUpdate().accept(this);
        }
        node.getBody().accept(this);
    }

    @Override
    public void visit(BreakStatement node) {

    }

    @Override
    public void visit(ContinueStatement node) {

    }

    @Override
    public void visit(ReturnStatement node) {
        if(node.getRet() != null) {
            node.getRet().accept(this);
        }
    }

    @Override
    public void visit(ExprStatement node) {
        node.getExpr().accept(this);
    }

    @Override
    public void visit(VarDeclStatement node) {
        node.getDeclaration().accept(this);
    }

    @Override
    public void visit(BlockStatement node) {
        Scope blockScope = new Scope(curScope);
        enterScope(blockScope);
        for(Statement statement : node.getStatements()) {
            statement.accept(this);
        }
        exitScope();
    }

    @Override
    public void visit(Expression node) {

    }

    @Override
    public void visit(ThisExpression node) {

    }

    @Override
    public void visit(NullLiteral node) {
        node.setType(new Type("null"));
    }

    @Override
    public void visit(BoolLiteral node) {
        node.setType(new Type("bool"));
    }

    @Override
    public void visit(IntLiteral node) {
        node.setType(new Type("int"));
    }

    @Override
    public void visit(StringLiteral node) {
        node.setType(new Type("string"));
    }

    @Override
    public void visit(Identifier node) {
        if(curScope.getRecursiveVariable(node.getName()) == null) {
            throw new SemanticError(node.getLocation(), "Cannot find identifier");
        } else {
            node.setVariableEntity(curScope.getRecursiveVariable(node.getName()));
        }
        node.setType(node.getVariableEntity().getType());
    }

    @Override
    public void visit(MemberExpression node) {
        node.getExpr().accept(this);
        if(node.getMember() != null) {
            visit(node.getMember());
            node.setType(node.getMember().getType());
        } else {
            visit(node.getFuncCall());
            node.setType(node.getFuncCall().getType());
        }
    }

    @Override
    public void visit(ArrayExpression node) {
        node.getArr().accept(this);
        node.getIdx().accept(this);
        node.setType(node.getArr().getType());
    }

    @Override
    public void visit(FuncCallExpression node) {
        if(curScope.getRecursiveFunction(node.getName().getName()) == null) {
            throw new SemanticError(node.getLocation(), "Cannot find function");
        } else {
            node.setFunctionEntity(curScope.getRecursiveFunction(node.getName().getName()));
        }
        for(Expression expression : node.getArguments()) {
            expression.accept(this);
        }
        node.setType(node.getFunctionEntity().getReturnType());

    }

    @Override
    public void visit(NewExpression node) {
        if(node.getDimensions() != null) {
            for(Expression expression : node.getDimensions()) {
                expression.accept(this);
            }
        }
        node.setType(resolveType(node.getTypeNode()));
    }

    @Override
    public void visit(SuffixExpression node) {
        node.getExpr().accept(this);
        node.setType(node.getExpr().getType());
    }

    @Override
    public void visit(PrefixExpression node) {
        node.getExpr().accept(this);
        node.setType(node.getExpr().getType());
    }

    @Override
    public void visit(BinaryExpression node) {
        node.getLhs().accept(this);
        node.getRhs().accept(this);
        switch (node.getOp()) {
            case "<":
            case ">":
            case "<=":
            case ">=":
            case "==":
            case "!=":
                node.setType(new Type("bool"));
                break;
            default:
                node.setType(node.getLhs().getType());
                break;
        }
    }

    @Override
    public void visit(AssignExpression node) {
        node.getLhs().accept(this);
        node.getRhs().accept(this);
        node.setType(new Type("void"));
    }
}
