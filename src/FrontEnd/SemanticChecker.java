package FrontEnd;

import AST.*;
import Scope.*;
import Type.*;
import Utility.SemanticError;

public class SemanticChecker implements ASTVistor {
    private GlobalScope globalScope;
    private FunctionEntity curFunctionEntity;
    private int loop;

    public SemanticChecker(GlobalScope globalScope) {
        this.globalScope = globalScope;
        curFunctionEntity = null;
        loop = 0;
    }

    private void checkMainFunction() {
        FunctionEntity mainFunction = globalScope.getFunction("main");
        if(mainFunction == null) {
            throw new SemanticError(new Location(0,0), "Cannot find main function");
        } else {
            if(!mainFunction.getReturnType().getType().equals(Type.types.INT)) {
                throw new SemanticError(new Location(0,0), "main function's return type should be int");
            }
            if(!mainFunction.getParameters().isEmpty()) {
                throw new SemanticError(new Location(0,0), "main function should have no parameters");
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
            if(!node.getType().getType().match(node.getInit().getType())) {
                throw new SemanticError(node.getLocation(), "Invalid type init");
            }
        }
    }

    @Override
    public void visit(TypeNode node) {

    }

    @Override
    public void visit(ArrayTypeNode node) {

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
        loop++;
        node.getBody().accept(this);
        loop--;
    }

    @Override
    public void visit(ForStatement node) {
        if(node.getInit() != null) {
            node.getInit().accept(this);
        }
        if(node.getCondition() != null) {
            if(node.getCondition().getType().getType() != Type.types.BOOL) {
                throw new SemanticError(node.getLocation(), "Invalid condition");
            }
            node.getCondition().accept(this);
        }
        if(node.getUpdate() != null) {
            node.getUpdate().accept(this);
        }
        loop++;
        node.getBody().accept(this);
        loop--;
    }

    @Override
    public void visit(BreakStatement node) {
        if(loop == 0) {
            throw new SemanticError(node.getLocation(), "Invalid use of break");
        }
    }

    @Override
    public void visit(ContinueStatement node) {
        if(loop == 0) {
            throw new SemanticError(node.getLocation(), "Invalid use of continue");
        }
    }

    @Override
    public void visit(ReturnStatement node) {
        if(node.getRet() != null) {
            if(!curFunctionEntity.getReturnType().match(node.getRet().getType())) {
                throw new SemanticError(node.getLocation(), "Invalid return type");
            }
        } else {
            if(curFunctionEntity.getReturnType().getType() != Type.types.VOID && curFunctionEntity.getReturnType().getType() != Type.types.NULL) {
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
    public void visit(EmptyStatement node) {

    }

    @Override
    public void visit(ThisExpression node) {
        node.setMutable(false);
    }

    @Override
    public void visit(NullLiteral node) {
        node.setMutable(false);
    }

    @Override
    public void visit(BoolLiteral node) {
        node.setMutable(false);
    }

    @Override
    public void visit(IntLiteral node) {
        node.setMutable(false);
    }

    @Override
    public void visit(StringLiteral node) {
        node.setMutable(false);
    }

    @Override
    public void visit(Identifier node) {

    }

    @Override
    public void visit(MemberExpression node) {
        node.getExpr().accept(this);
        if(node.getExpr().getType() instanceof ArrayType) {
            node.setMutable(false);
        }
        if(node.getMember() != null) {
            node.getMember().accept(this);
        } else {
            node.getFuncCall().accept(this);
            node.setMutable(false);
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
            if(!node.getArguments().get(i).getType().match(node.getFunctionEntity().getParameters().get(i).getType())) {
                throw new SemanticError(node.getLocation(), "Invalid parameter type");
            }
        }
        node.setMutable(false);
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
                if(!node.getExpr().isMutable()) {
                    throw new SemanticError(node.getLocation(), "Invalid immutable operator");
                }
                break;
            case "--":
                if(node.getExpr().getType().getType() != Type.types.INT) {
                    throw new SemanticError(node.getLocation(), "Invalid SuffixExpression");
                }
                if(!node.getExpr().isMutable()) {
                    throw new SemanticError(node.getLocation(), "Invalid immutable operator");
                }
                break;
            default:
                throw new SemanticError(node.getLocation(), "Invalid SuffixExpression");
        }
        node.setMutable(false);
    }

    @Override
    public void visit(PrefixExpression node) {
        node.getExpr().accept(this);
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
        if(!node.getOp().equals("++") && !node.getOp().equals("--")) {
            node.setMutable(false);
        }
    }

    @Override
    public void visit(BinaryExpression node) {
        node.getLhs().accept(this);
        node.getRhs().accept(this);
        if(!node.getLhs().getType().match(node.getRhs().getType())) {
            throw new SemanticError(node.getLocation(), "LHS type is not equal to RHS type");
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
        node.setMutable(false);
    }

    @Override
    public void visit(AssignExpression node) {
        node.getLhs().accept(this);
        node.getRhs().accept(this);
        if(!node.getLhs().getType().match(node.getRhs().getType())) {
            throw new SemanticError(node.getLocation(), "LHS type is not equal to RHS type");
        }
        if(node.getLhs().getType().getType() == Type.types.VOID) {
            throw new SemanticError(node.getLocation(), "Cannot assign a void type");
        }
        if(!node.getLhs().isMutable()) {
            throw new SemanticError(node.getLocation(), "Cannot assign a immutable type");
        }
        node.setMutable(false);
    }
}
