package FrontEnd;

import AST.*;

import java.util.*;

public class CommonExpressionElimination implements ASTVistor {
    private Program program;
    private HashMap<Integer, Identifier> variableMap;

    public CommonExpressionElimination(Program program) {
        this.program = program;
        this.variableMap = new HashMap<>();
    }

    public void run() {
        visit(program);
    }

    private Integer getExpressionHashCode(Expression expression) {
        if(expression instanceof Identifier) {
            System.err.println(((Identifier) expression).getName() + " identifier.hashcode() = " + expression.hashCode());
            return ((Identifier) expression).getName().hashCode();
        } else if(expression instanceof BinaryExpression) {
            int opValue = ((BinaryExpression) expression).getOp().hashCode();
            Integer lvalue = getExpressionHashCode(((BinaryExpression) expression).getLhs());
            Integer rvalue = getExpressionHashCode(((BinaryExpression) expression).getRhs());
            System.err.println("binaryExpression.hashcode() = " + (lvalue ^ rvalue));
            return lvalue ^ opValue ^ rvalue;
        } else if(expression instanceof IntLiteral) {
            return ((IntLiteral) expression).getValue();
        }
        return -1;
    }

    @Override
    public void visit(Program node) {
        for(FunctionDeclaration functionDeclaration : node.getFunctions()) {
            visit(functionDeclaration);
        }
    }

    @Override
    public void visit(FunctionDeclaration node) {
        variableMap.clear();
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
        if(node.getInit() != null) {
            Integer init = getExpressionHashCode(node.getInit());
            System.err.println(node.getName() + " var.hashcode() = " + init);
            if(init > 0) {
                if(!variableMap.containsKey(init)) {
                    Identifier var = new Identifier(node.getName());
                    var.setVariableEntity(node.getVariableEntity());
                    var.setType(node.getVariableEntity().getType());
                    variableMap.put(init, var);
                } else if (!node.getName().equals(variableMap.get(init).getName())){
                    System.err.println(node.getName() + " eliminate to " + variableMap.get(init).getName());
                    node.setInit(variableMap.get(init));
                }
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

    }

    @Override
    public void visit(WhileStatement node) {

    }

    @Override
    public void visit(ForStatement node) {
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

    }

    @Override
    public void visit(ExprStatement node) {

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
        node.getLhs().accept(this);
        node.getRhs().accept(this);
    }

    @Override
    public void visit(AssignExpression node) {

    }
}
