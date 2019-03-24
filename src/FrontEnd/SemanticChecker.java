package FrontEnd;

import AST.*;
import Scope.*;;
import Type.Type;
import Utility.SemanticError;

import java.util.LinkedList;
import java.util.List;

public class SemanticChecker implements ASTVistor {
    private GlobalScopeBuilder globalScope;
    private FunctionEntity curFunctionEntity;

    public SemanticChecker(GlobalScopeBuilder globalScope) {
        this.globalScope = globalScope;
        curFunctionEntity = null;
    }

    private void checkMainFunction() {
        FunctionEntity mainFunction = globalScope.getScope().getFunction("main");
        if(mainFunction == null) {
            throw new SemanticError(new Location(0,0), "Cannot find main function");
        } else {
            if(!mainFunction.getReturnType().getType().equals(Type.types.INT)) {
                throw new SemanticError(new Location(0,0), "main function's return type should be int");
            }
            if(!mainFunction.getParameters().isEmpty()) {
                throw new SemanticError(new Location(0,0), "main funciotn should have no parameters");
            }
        }
    }

    @Override
    public void visit(Program node) {
        checkMainFunction();
        for(FunctionDeclaration functionDeclaration : node.getFunctions()) {
            visit(functionDeclaration);
        }
        for(ClassDeclaration classDeclaration : node.getClasses()) {
            visit(classDeclaration);
        }
        for(VariableDeclaration variableDeclaration : node.getVariables()) {
            visit(variableDeclaration);
        }
    }

    @Override
    public void visit(Declaration node) {

    }

    @Override
    public void visit(FunctionDeclaration node) {
        curFunctionEntity = node.getFunctionEntity();
        for(Statement statement : node.getBody()) {
            statement.accept(this);
        }
    }

    @Override
    public void visit(ClassDeclaration node) {
        if(node.getConstructor() != null) {
            visit(node.getConstructor());
        }
        for(FunctionDeclaration functionDeclaration : node.getMethods()) {
            visit(functionDeclaration);
        }
    }

    @Override
    public void visit(VariableDeclaration node) {
        if (node.getInit() != null) {
            node.getInit().accept(this);
        }
        if(node.getType().getType().getType() != node.getInit().getType().getType()) {
            throw new SemanticError(node.getLocation(), "Invalid type init");
        }
    }

    @Override
    public void visit(TypeNode node) {

    }

    @Override
    public void visit(ArrayTypeNode node) {

    }

    @Override
    public void visit(Statement node) {

    }

    @Override
    public void visit(IfStatement node) {
        node.getCondition().accept(this);
        if(node.getCondition().getType().getType() != Type.types.BOOL) {
            throw new SemanticError(node.getLocation(), "Invalid condition");
        }
        node.getThenStatement().accept(this);
        if(node.getElseStatement() != null) {
            node.getElseStatement().accept(this);
        }
    }

    @Override
    public void visit(WhileStatement node) {
        node.getCondition().accept(this);
        if(node.getCondition().getType().getType() != Type.types.BOOL) {
            throw new SemanticError(node.getLocation(), "Invalid condition");
        }
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
            if(node.getRet().getType().getType() != curFunctionEntity.getReturnType().getType()) {
                throw new SemanticError(node.getLocation(), "Invalid return type");
            }
        } else {
            if(curFunctionEntity.getReturnType().getType() != Type.types.VOID) {
                throw new SemanticError(node.getLocation(), "Invalid return type");
            }
        }
    }

    @Override
    public void visit(ExprStatement node) {
        node.getExpr().accept(this);
    }

    @Override
    public void visit(VarDeclStatement node) {
        visit(node.getDeclaration());
    }

    @Override
    public void visit(BlockStatement node) {
        for(Statement statement : node.getStatements()) {
            statement.accept(this);
        }
    }

    @Override
    public void visit(Expression node) {

    }

    @Override
    public void visit(ThisExpression node) {

    }

    @Override
    public void visit(NullLiteral node) {

    }

    @Override
    public void visit(BoolLiteral node) {

    }

    @Override
    public void visit(IntLiteral node) {

    }

    @Override
    public void visit(StringLiteral node) {

    }

    @Override
    public void visit(Identifier node) {

    }

    @Override
    public void visit(MemberExpression node) {
        node.getExpr().accept(this);
        if(node.getMember() != null) {
            node.getMember().accept(this);
        } else {
            node.getFuncCall().accept(this);
        }
    }

    @Override
    public void visit(ArrayExpression node) {
        node.getArr().accept(this);
        node.getIdx().accept(this);
    }

    @Override
    public void visit(FuncCallExpression node) {
        for(int i = 0; i < node.getArguments().size(); i++) {
            if(node.getArguments().get(i).getType().getType() != node.getFunctionEntity().getParameters().get(i).getType().getType()) {
                throw new SemanticError(node.getLocation(), "Invalid paramenter type");
            }
        }
    }

    @Override
    public void visit(NewExpression node) {
        for(Expression expression : node.getDimensions()) {
            expression.accept(this);
        }
    }

    @Override
    public void visit(SuffixExpression node) {
        node.getExpr().accept(this);
        switch (node.getOp()) {
            case "++":
                if(node.getExpr().getType().getType() != Type.types.INT) {
                    throw new SemanticError(node.getLocation(), "Invalid SuffixExpression");
                }
                break;
            case "--":
                if(node.getExpr().getType().getType() != Type.types.INT) {
                    throw new SemanticError(node.getLocation(), "Invalid SuffixExpression");
                }
                break;
            default:
                throw new SemanticError(node.getLocation(), "Invalid SuffixExpression");
        }
    }

    @Override
    public void visit(PrefixExpression node) {
        switch (node.getOp()) {
            case "++":
            case "--":
            case "+":
            case "-":
            case "~":
                if(node.getExpr().getType().getType() != Type.types.INT) {
                    throw new SemanticError(node.getLocation(), "Invalid PrefixExpression");
                }
                break;
            case "!":
                if(node.getExpr().getType().getType() != Type.types.BOOL) {
                    throw new SemanticError(node.getLocation(), "Invalid PrefixExpression");
                }
                break;
            default:
                throw new SemanticError(node.getLocation(), "Invalid PrefixExpression");
        }
    }

    @Override
    public void visit(BinaryExpression node) {
        node.getLhs().accept(this);
        node.getRhs().accept(this);
        if(node.getLhs().getType().getType() != node.getRhs().getType().getType()) {
            throw new SemanticError(node.getLocation(), "LHS type isnot equal to RHS type");
        }
        switch (node.getOp()) {
            case "*":
            case "/":
            case "%":
            case "-":
            case ">>":
            case "<<":
            case "&":
            case "|":
            case "^":
                if(node.getLhs().getType().getType() != Type.types.INT) {
                    throw new SemanticError(node.getLocation(), "Operator wrong type");
                }
                break;
            case "+":
            case "<":
            case ">":
            case "<=":
            case ">=":
                if(node.getLhs().getType().getType() != Type.types.INT && node.getLhs().getType().getType() != Type.types.STRING) {
                    throw new SemanticError(node.getLocation(), "Operator wrong type");
                }
                break;
            case "&&":
            case "||":
                if(node.getLhs().getType().getType() != Type.types.BOOL) {
                    throw new SemanticError(node.getLocation(), "Operator wrong type");
                }
                break;
            case "==":
            case "!=":
                break;
            default:
                throw new SemanticError(node.getLocation(), "Invalid BinaryExpression");
        }
    }

    @Override
    public void visit(AssignExpression node) {
        node.getLhs().accept(this);
        node.getRhs().accept(this);
        if(node.getLhs().getType().getType() != node.getRhs().getType().getType()) {
            throw new SemanticError(node.getLocation(), "LHS type isnot equal to RHS type");
        }
    }

}
