package FrontEnd;

import AST.*;
import Scope.VariableEntity;

import java.util.*;

public class UselessLoopEliminator implements ASTVistor {
    private Program program;
    private HashMap<Statement, HashSet<VariableEntity>> loopVariables;
    private Stack<Statement> curLoops;
    private HashSet<Statement> irreplaceableLoops;
    

    public UselessLoopEliminator(Program program) {
        this.program = program;
        this.loopVariables = new HashMap<>();
        this.irreplaceableLoops = new HashSet<>();
        this.curLoops = new Stack<>();
    }

    public void run() {
        visit(program);
    }

    private boolean isLocal(VariableEntity var) {
        return !var.isGlobal() && !var.isInClass();
    }

    @Override
    public void visit(Program node) {
        for(FunctionDeclaration functionDeclaration : node.getFunctions()) {
            visit(functionDeclaration);
        }
        for(ClassDeclaration classDeclaration : node.getClasses()) {
            visit(classDeclaration);
        }
    }

    private void process(FunctionDeclaration node) {
        deleteLoop(node.getBody());
    }

    private boolean isLoop(Statement statement) {
        return statement instanceof ForStatement || statement instanceof WhileStatement;
    }

    private void deleteLoop(List<Statement> body) {
        List<Statement> deleted = new LinkedList<>();
        for(Statement statement : body) {
            if(isLoop(statement) && !irreplaceableLoops.contains(statement)) {
                System.err.println("delete loop " + statement.getLocation());
                deleted.add(statement);
            }
        }
        body.removeAll(deleted);
    }

    @Override
    public void visit(FunctionDeclaration node) {
        loopVariables.clear();
        irreplaceableLoops.clear();
        curLoops.clear();
        if(node.getParameters() != null) {
            for(VariableDeclaration variableDeclaration : node.getParameters()) {
                visit(variableDeclaration);
            }
        }
        if(node.getBody() != null) {
            for(Statement statement : node.getBody()) {
                statement.accept(this);
            }
        }
        process(node);
    }

    @Override
    public void visit(ClassDeclaration node) {
        if(node.getConstructor() != null) {
            visit(node.getConstructor());
        }
        if(node.getMethods() != null) {
            for(FunctionDeclaration functionDeclaration : node.getMethods()) {
                visit(functionDeclaration);
            }
        }
    }

    @Override
    public void visit(VariableDeclaration node) {
        VariableEntity var = node.getVariableEntity();
        if(isLocal(var)) {
            for(Statement loop : curLoops) {
                loopVariables.get(loop).add(var);
            }
        } else {
            irreplaceableLoops.addAll(curLoops);
        }
        if(node.getInit() != null) {
            node.getInit().accept(this);
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
        node.getThenStatement().accept(this);
        if(node.getElseStatement() != null) {
            node.getElseStatement().accept(this);
        }
    }

    @Override
    public void visit(WhileStatement node) {
        loopVariables.put(node, new HashSet<>());
        curLoops.push(node);
        node.getCondition().accept(this);
        node.getBody().accept(this);
        curLoops.pop();
    }

    @Override
    public void visit(ForStatement node) {
        loopVariables.put(node, new HashSet<>());
        curLoops.push(node);
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
        curLoops.pop();
    }

    @Override
    public void visit(BreakStatement node) {

    }

    @Override
    public void visit(ContinueStatement node) {

    }

    @Override
    public void visit(ReturnStatement node) {
        irreplaceableLoops.addAll(curLoops);
        if(node.getRet() != null){
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
        for(Statement statement : node.getStatements()) {
            statement.accept(this);
        }
    }

    @Override
    public void visit(EmptyStatement node) {

    }

    @Override
    public void visit(ThisExpression node) {
        irreplaceableLoops.addAll(curLoops);
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
        VariableEntity var = node.getVariableEntity();
        if(var == null) { // function
            irreplaceableLoops.addAll(curLoops);
        } else {
            if(isLocal(var)) {
                for(Statement loop : loopVariables.keySet()) {
                    if(curLoops.contains(loop)) {
                        loopVariables.get(loop).add(var);
                    } else {
                        if(loopVariables.get(loop).contains(var)) {
                            irreplaceableLoops.add(loop);
                        }
                    }
                }
            } else {
                irreplaceableLoops.addAll(curLoops);
            }
        }

    }

    @Override
    public void visit(MemberExpression node) {
        node.getExpr().accept(this);
        if(node.getMember() != null){
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
        visit(node.getName());
        for(Expression expression : node.getArguments()) {
            expression.accept(this);
        }
    }

    @Override
    public void visit(NewExpression node) {
        irreplaceableLoops.addAll(curLoops);
        for(Expression expression : node.getDimensions()) {
            expression.accept(this);
        }
    }

    @Override
    public void visit(SuffixExpression node) {
        node.getExpr().accept(this);
    }

    @Override
    public void visit(PrefixExpression node) {
        node.getExpr().accept(this);
    }

    @Override
    public void visit(BinaryExpression node) {
        node.getLhs().accept(this);
        node.getRhs().accept(this);
    }

    @Override
    public void visit(AssignExpression node) {
        node.getLhs().accept(this);
        node.getRhs().accept(this);
    }
}
