package FrontEnd;

import AST.*;
import Scope.VariableEntity;

import java.util.HashMap;
import java.util.HashSet;

public class LoopOptimizer implements ASTVistor {
    private HashMap<Statement, HashSet<VariableEntity>> loopToVariables;
    private HashSet<Statement> curLoop;


    public LoopOptimizer() {

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

    @Override
    public void visit(FunctionDeclaration node) {

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
