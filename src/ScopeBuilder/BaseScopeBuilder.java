package ScopeBuilder;

import AST.*;
import Type.Type;
import Utility.SemanticError;

public abstract class BaseScopeBuilder implements ASTVistor {

    public void checkInitType(VariableDeclaration node) {
        if (node.getInit() != null) {
            node.getInit().accept(this);
        }
        if(!node.getType().getType().equals(node.getInit().getType())) {
            throw new SemanticError(node.getLocation(), "Invalid type init");
        }
    }

    public Type resolveType(TypeNode typeNode) {
        if(typeNode.isPrimitiveType() || typeNode.isClassType()) {
            return typeNode.getType();
        } else if(typeNode.isArrayType()) {
            return resolveType(((ArrayTypeNode) typeNode).getBaseType());
        } else throw new SemanticError(typeNode.getLocation(), "Invalid type");
    }

    @Override
    public void visit(Program node) {

    }

    @Override
    public void visit(Declaration node) {

    }

    @Override
    public void visit(FunctionDeclaration node) {

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
    public void visit(Statement node) {

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
