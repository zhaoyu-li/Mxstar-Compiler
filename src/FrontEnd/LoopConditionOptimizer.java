package FrontEnd;

import AST.*;
import Scope.Scope;
import Scope.VariableEntity;

import java.util.*;

public class LoopConditionOptimizer implements ASTVistor {
    private Program program;
    private Stack<Statement> curLoops;
    private HashMap<Statement, IfStatement> loopToIf;
    private HashMap<Statement, HashSet<VariableEntity>> loopConditionVariables;
    private HashMap<Statement, HashSet<VariableEntity>> ifConditionVariables;
    private HashSet<Statement> immobileLoop;
    private Statement curLoop;
    private Statement curIf;
    private boolean nowInLoopCondition;
    private boolean nowInIfCondition;
    private HashMap<Statement, Statement> changed;

    public LoopConditionOptimizer(Program program) {
        this.program = program;
        this.curLoops = new Stack<>();
        this.loopToIf = new HashMap<>();
        this.loopConditionVariables = new HashMap<>();
        this.ifConditionVariables = new HashMap<>();
        this.nowInLoopCondition = false;
        this.nowInIfCondition = false;
        this.curLoop = null;
        this.curIf = null;
        this.changed = new HashMap<>();
        this.immobileLoop = new HashSet<>();
    }

    public void run() {
        visit(program);
    }

    private void init() {
        curLoops.clear();
        loopToIf.clear();
        loopConditionVariables.clear();
        ifConditionVariables.clear();
        curLoop = null;
        curIf = null;
        nowInLoopCondition = false;
        nowInIfCondition = false;
        immobileLoop.clear();
    }

    private class LoopConditionSwitcher implements ASTVistor { // find block statement and do exchange
        private LoopConditionSwitcher() {

        }

        private void run() {
            visit(program);
        }

        @Override
        public void visit(Program node) {
            for(FunctionDeclaration functionDeclaration : node.getFunctions()) {
                visit(functionDeclaration);
            }
        }

        @Override
        public void visit(FunctionDeclaration node) {
            if(node.getBody() != null) {
                for(Statement statement : node.getBody()) {
                    statement.accept(this);
                }
            }
        }

        @Override
        public void visit(ClassDeclaration node) {

        }

        @Override
        public void visit(VariableDeclaration node) {

        }

        @Override
        public void visit(TypeNode node) {

        }

        @Override
        public void visit(ArrayTypeNode node) {

        }


        @Override
        public void visit(IfStatement node) {
            node.getThenStatement().accept(this);
            if(node.getElseStatement() != null) {
                node.getElseStatement().accept(this);
            }
        }

        @Override
        public void visit(WhileStatement node) {
            if(node.getBody() != null) {
                node.getBody().accept(this);
            }
        }

        @Override
        public void visit(ForStatement node) {
            if(node.getBody() != null) {
                node.getBody().accept(this);
            }
        }

        @Override
        public void visit(BreakStatement node) {

        }

        @Override
        public void visit(ContinueStatement node) {

        }

        @Override
        public void visit(ReturnStatement node) {

        }

        @Override
        public void visit(ExprStatement node) {
        }

        @Override
        public void visit(VarDeclStatement node) {

        }

        @Override
        public void visit(BlockStatement node) {
            for(int i = 0; i < node.getStatements().size(); i++) {
                Statement statement = node.getStatements().get(i);
                if(changed.containsKey(statement)) {
                    System.err.println("============================= switch =========================");
                    node.getStatements().set(i, changed.get(statement));
                    statement = changed.get(statement);
                }
                statement.accept(this);
            }
        }

        @Override
        public void visit(EmptyStatement node) {

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

        }

        @Override
        public void visit(ArrayExpression node) {

        }

        @Override
        public void visit(FuncCallExpression node) {

        }

        @Override
        public void visit(NewExpression node) {

        }

        @Override
        public void visit(SuffixExpression node) {

        }

        @Override
        public void visit(PrefixExpression node) {

        }

        @Override
        public void visit(BinaryExpression node) {

        }

        @Override
        public void visit(AssignExpression node) {

        }
    }

    @Override
    public void visit(Program node) {
        for(FunctionDeclaration functionDeclaration : node.getFunctions()) {
            visit(functionDeclaration);
        }
        LoopConditionSwitcher loopConditionSwitcher = new LoopConditionSwitcher();
        loopConditionSwitcher.run();
    }

    private boolean checkLoop(Statement loop) {
        if(loop instanceof ForStatement) {
            return !immobileLoop.contains(loop)
                    && (((ForStatement) loop).getBody()) instanceof BlockStatement
                    && ((BlockStatement) ((ForStatement) loop).getBody()).getStatements().size() == 1
                    && ((BlockStatement) ((ForStatement) loop).getBody()).getStatements().get(0) instanceof IfStatement
                    && ((IfStatement) ((BlockStatement) ((ForStatement) loop).getBody()).getStatements().get(0)).getElseStatement() == null;
        } else {
            return !immobileLoop.contains(loop)
                    && (((WhileStatement) loop).getBody()) instanceof BlockStatement
                    && ((BlockStatement) ((WhileStatement) loop).getBody()).getStatements().size() == 1
                    && ((BlockStatement) ((WhileStatement) loop).getBody()).getStatements().get(0) instanceof IfStatement
                    && ((IfStatement) ((BlockStatement) ((WhileStatement) loop).getBody()).getStatements().get(0)).getElseStatement() == null;
        }
    }

    @Override
    public void visit(FunctionDeclaration node) {
        init();
        if(node.getBody() != null) {
            for(Statement statement : node.getBody()) {
                statement.accept(this);
            }
        }
    }

    @Override
    public void visit(ClassDeclaration node) {

    }

    @Override
    public void visit(VariableDeclaration node) {

    }

    @Override
    public void visit(TypeNode node) {

    }

    @Override
    public void visit(ArrayTypeNode node) {

    }


    @Override
    public void visit(IfStatement node) {
        if(curLoop != null && !loopToIf.containsKey(curLoop)) {
            loopToIf.put(curLoop, node);
        }
        curIf = node;
        ifConditionVariables.put(node, new HashSet<>());
        nowInIfCondition = true;
        node.getCondition().accept(this);
        nowInIfCondition = false;
        node.getThenStatement().accept(this);
        if(node.getElseStatement() != null) {
            node.getElseStatement().accept(this);
        }
        curIf = null;
    }

    @Override
    public void visit(WhileStatement node) {
        curLoop = node;
        curLoops.push(node);
        loopConditionVariables.put(node, new HashSet<>());
        nowInLoopCondition = true;
        node.getCondition().accept(this);
        nowInLoopCondition = false;
        node.getBody().accept(this);
        curLoops.pop();
        curLoop = null;
        if(checkLoop(node)) {
            HashSet<VariableEntity> check = new HashSet<>(loopConditionVariables.get(node));
            check.retainAll(ifConditionVariables.get(loopToIf.get(node)));
            if(check.isEmpty()) {
                System.err.println("=========================== change loop and if ================================");
                WhileStatement newWhileStatement = node;
                newWhileStatement.setBody(loopToIf.get(node).getThenStatement());
                IfStatement newIfStatement = loopToIf.get(node);
                newIfStatement.setThenStatement(newWhileStatement);
                changed.put(node, newIfStatement);
            }
        }
    }

    @Override
    public void visit(ForStatement node) {
        curLoop = node;
        curLoops.push(node);
        loopConditionVariables.put(node, new HashSet<>());
        nowInLoopCondition = true;
        if(node.getInit() != null) {
            node.getInit().accept(this);
        }
        if(node.getCondition() != null) {
            node.getCondition().accept(this);
        }
        if(node.getUpdate() != null) {
            node.getUpdate().accept(this);
        }
        nowInLoopCondition = false;
        node.getBody().accept(this);
        curLoops.pop();
        curLoop = null;
        if(checkLoop(node)) {
            HashSet<VariableEntity> check = new HashSet<>(loopConditionVariables.get(node));
            check.retainAll(ifConditionVariables.get(loopToIf.get(node)));
            if(check.isEmpty()) {
                System.err.println("============================== change loop and if ================================");
                ForStatement newForStatement = node;
                newForStatement.setBody(loopToIf.get(node).getThenStatement());
                IfStatement newIfStatement = loopToIf.get(node);
                newIfStatement.setThenStatement(newForStatement);
                changed.put(node, newIfStatement);
            }
        }
    }

    @Override
    public void visit(BreakStatement node) {

    }

    @Override
    public void visit(ContinueStatement node) {

    }

    @Override
    public void visit(ReturnStatement node) {
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
        if(nowInLoopCondition) {
            loopConditionVariables.get(curLoop).add(var);
        } else if(nowInIfCondition) {
            ifConditionVariables.get(curIf).add(var);
        }
        if(node.getVariableEntity().isGlobal() || node.getVariableEntity().isInClass()) {
            immobileLoop.addAll(curLoops);
        }
    }

    @Override
    public void visit(MemberExpression node) {
        immobileLoop.addAll(curLoops);
    }

    @Override
    public void visit(ArrayExpression node) {
        node.getArr().accept(this);
        node.getIdx().accept(this);
    }

    @Override
    public void visit(FuncCallExpression node) {
        immobileLoop.addAll(curLoops);
    }

    @Override
    public void visit(NewExpression node) {
        immobileLoop.addAll(curLoops);
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
