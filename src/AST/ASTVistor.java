package AST;

public interface ASTVistor {
    void visit(Program node);

    void visit(FunctionDeclaration node);
    void visit(ClassDeclaration node);
    void visit(VariableDeclaration node);

    void visit(TypeNode node);
    void visit(ArrayTypeNode node);

    void visit(IfStatement node);
    void visit(WhileStatement node);
    void visit(ForStatement node);
    void visit(BreakStatement node);
    void visit(ContinueStatement node);
    void visit(ReturnStatement node);
    void visit(ExprStatement node);
    void visit(VarDeclStatement node);
    void visit(BlockStatement node);
    void visit(EmptyStatement node);

    void visit(ThisExpression node);
    void visit(NullLiteral node);
    void visit(BoolLiteral node);
    void visit(IntLiteral node);
    void visit(StringLiteral node);
    void visit(Identifier node);
    void visit(MemberExpression node);
    void visit(ArrayExpression node);
    void visit(FuncCallExpression node);
    void visit(NewExpression node);
    void visit(SuffixExpression node);
    void visit(PrefixExpression node);
    void visit(BinaryExpression node);
    void visit(AssignExpression node);
}
