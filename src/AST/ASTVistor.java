package AST;

public interface ASTVistor {
    void visit(Program node);

    void visit(Declaration node);
    void visit(FunctionDeclaration node);
    void visit(ClassDeclaration node);
    void visit(VariableDeclaration node);

    void visit(TypeNode node);

    void visit(Statement node);
    void visit(ForStatement node);
    void visit(WhileStatement node);
    void visit(IfStatement node);
    void visit(ContinueStatement node);
    void visit(BreakStatement node);
    void visit(ReturnStatement node);
    void visit(BlockStatement node);
    void visit(VarDeclStatement node);
    void visit(ExprStatement node);

    void visit(Expression node);
    void visit(ArrayExpression node);
    void visit(FuncCallExpression node);
    void visit(NewExpression node);
    void visit(MemberExpression node);
    void visit(BinaryExpression node);
    void visit(AssignExpression node);

    void visit(Identifier node);

}
