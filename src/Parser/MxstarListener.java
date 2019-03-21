// Generated from C:/Users/apoll/Desktop/Mxstar-Complier/src/Parser\Mxstar.g4 by ANTLR 4.7.2
package Parser;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link MxstarParser}.
 */
public interface MxstarListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link MxstarParser#program}.
	 * @param ctx the parse tree
	 */
	void enterProgram(MxstarParser.ProgramContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxstarParser#program}.
	 * @param ctx the parse tree
	 */
	void exitProgram(MxstarParser.ProgramContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxstarParser#globalDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterGlobalDeclaration(MxstarParser.GlobalDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxstarParser#globalDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitGlobalDeclaration(MxstarParser.GlobalDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxstarParser#functionDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterFunctionDeclaration(MxstarParser.FunctionDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxstarParser#functionDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitFunctionDeclaration(MxstarParser.FunctionDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxstarParser#classDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterClassDeclaration(MxstarParser.ClassDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxstarParser#classDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitClassDeclaration(MxstarParser.ClassDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxstarParser#variableDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterVariableDeclaration(MxstarParser.VariableDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxstarParser#variableDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitVariableDeclaration(MxstarParser.VariableDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxstarParser#constructorDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterConstructorDeclaration(MxstarParser.ConstructorDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxstarParser#constructorDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitConstructorDeclaration(MxstarParser.ConstructorDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxstarParser#fieldDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterFieldDeclaration(MxstarParser.FieldDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxstarParser#fieldDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitFieldDeclaration(MxstarParser.FieldDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxstarParser#variableDeclarators}.
	 * @param ctx the parse tree
	 */
	void enterVariableDeclarators(MxstarParser.VariableDeclaratorsContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxstarParser#variableDeclarators}.
	 * @param ctx the parse tree
	 */
	void exitVariableDeclarators(MxstarParser.VariableDeclaratorsContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxstarParser#variableDeclarator}.
	 * @param ctx the parse tree
	 */
	void enterVariableDeclarator(MxstarParser.VariableDeclaratorContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxstarParser#variableDeclarator}.
	 * @param ctx the parse tree
	 */
	void exitVariableDeclarator(MxstarParser.VariableDeclaratorContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxstarParser#parameterList}.
	 * @param ctx the parse tree
	 */
	void enterParameterList(MxstarParser.ParameterListContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxstarParser#parameterList}.
	 * @param ctx the parse tree
	 */
	void exitParameterList(MxstarParser.ParameterListContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxstarParser#parameter}.
	 * @param ctx the parse tree
	 */
	void enterParameter(MxstarParser.ParameterContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxstarParser#parameter}.
	 * @param ctx the parse tree
	 */
	void exitParameter(MxstarParser.ParameterContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxstarParser#block}.
	 * @param ctx the parse tree
	 */
	void enterBlock(MxstarParser.BlockContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxstarParser#block}.
	 * @param ctx the parse tree
	 */
	void exitBlock(MxstarParser.BlockContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxstarParser#type}.
	 * @param ctx the parse tree
	 */
	void enterType(MxstarParser.TypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxstarParser#type}.
	 * @param ctx the parse tree
	 */
	void exitType(MxstarParser.TypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxstarParser#primitiveType}.
	 * @param ctx the parse tree
	 */
	void enterPrimitiveType(MxstarParser.PrimitiveTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxstarParser#primitiveType}.
	 * @param ctx the parse tree
	 */
	void exitPrimitiveType(MxstarParser.PrimitiveTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxstarParser#classType}.
	 * @param ctx the parse tree
	 */
	void enterClassType(MxstarParser.ClassTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxstarParser#classType}.
	 * @param ctx the parse tree
	 */
	void exitClassType(MxstarParser.ClassTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxstarParser#empty}.
	 * @param ctx the parse tree
	 */
	void enterEmpty(MxstarParser.EmptyContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxstarParser#empty}.
	 * @param ctx the parse tree
	 */
	void exitEmpty(MxstarParser.EmptyContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxstarParser#statementList}.
	 * @param ctx the parse tree
	 */
	void enterStatementList(MxstarParser.StatementListContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxstarParser#statementList}.
	 * @param ctx the parse tree
	 */
	void exitStatementList(MxstarParser.StatementListContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ifStatement}
	 * labeled alternative in {@link MxstarParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterIfStatement(MxstarParser.IfStatementContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ifStatement}
	 * labeled alternative in {@link MxstarParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitIfStatement(MxstarParser.IfStatementContext ctx);
	/**
	 * Enter a parse tree produced by the {@code whileStatement}
	 * labeled alternative in {@link MxstarParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterWhileStatement(MxstarParser.WhileStatementContext ctx);
	/**
	 * Exit a parse tree produced by the {@code whileStatement}
	 * labeled alternative in {@link MxstarParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitWhileStatement(MxstarParser.WhileStatementContext ctx);
	/**
	 * Enter a parse tree produced by the {@code forStatement}
	 * labeled alternative in {@link MxstarParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterForStatement(MxstarParser.ForStatementContext ctx);
	/**
	 * Exit a parse tree produced by the {@code forStatement}
	 * labeled alternative in {@link MxstarParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitForStatement(MxstarParser.ForStatementContext ctx);
	/**
	 * Enter a parse tree produced by the {@code breakStatement}
	 * labeled alternative in {@link MxstarParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterBreakStatement(MxstarParser.BreakStatementContext ctx);
	/**
	 * Exit a parse tree produced by the {@code breakStatement}
	 * labeled alternative in {@link MxstarParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitBreakStatement(MxstarParser.BreakStatementContext ctx);
	/**
	 * Enter a parse tree produced by the {@code continueStatement}
	 * labeled alternative in {@link MxstarParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterContinueStatement(MxstarParser.ContinueStatementContext ctx);
	/**
	 * Exit a parse tree produced by the {@code continueStatement}
	 * labeled alternative in {@link MxstarParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitContinueStatement(MxstarParser.ContinueStatementContext ctx);
	/**
	 * Enter a parse tree produced by the {@code returnStatement}
	 * labeled alternative in {@link MxstarParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterReturnStatement(MxstarParser.ReturnStatementContext ctx);
	/**
	 * Exit a parse tree produced by the {@code returnStatement}
	 * labeled alternative in {@link MxstarParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitReturnStatement(MxstarParser.ReturnStatementContext ctx);
	/**
	 * Enter a parse tree produced by the {@code exprStatement}
	 * labeled alternative in {@link MxstarParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterExprStatement(MxstarParser.ExprStatementContext ctx);
	/**
	 * Exit a parse tree produced by the {@code exprStatement}
	 * labeled alternative in {@link MxstarParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitExprStatement(MxstarParser.ExprStatementContext ctx);
	/**
	 * Enter a parse tree produced by the {@code varDeclStatement}
	 * labeled alternative in {@link MxstarParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterVarDeclStatement(MxstarParser.VarDeclStatementContext ctx);
	/**
	 * Exit a parse tree produced by the {@code varDeclStatement}
	 * labeled alternative in {@link MxstarParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitVarDeclStatement(MxstarParser.VarDeclStatementContext ctx);
	/**
	 * Enter a parse tree produced by the {@code blockStatement}
	 * labeled alternative in {@link MxstarParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterBlockStatement(MxstarParser.BlockStatementContext ctx);
	/**
	 * Exit a parse tree produced by the {@code blockStatement}
	 * labeled alternative in {@link MxstarParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitBlockStatement(MxstarParser.BlockStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxstarParser#expressionList}.
	 * @param ctx the parse tree
	 */
	void enterExpressionList(MxstarParser.ExpressionListContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxstarParser#expressionList}.
	 * @param ctx the parse tree
	 */
	void exitExpressionList(MxstarParser.ExpressionListContext ctx);
	/**
	 * Enter a parse tree produced by the {@code primaryExpression}
	 * labeled alternative in {@link MxstarParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterPrimaryExpression(MxstarParser.PrimaryExpressionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code primaryExpression}
	 * labeled alternative in {@link MxstarParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitPrimaryExpression(MxstarParser.PrimaryExpressionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code binaryExpression}
	 * labeled alternative in {@link MxstarParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterBinaryExpression(MxstarParser.BinaryExpressionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code binaryExpression}
	 * labeled alternative in {@link MxstarParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitBinaryExpression(MxstarParser.BinaryExpressionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code prefixExpression}
	 * labeled alternative in {@link MxstarParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterPrefixExpression(MxstarParser.PrefixExpressionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code prefixExpression}
	 * labeled alternative in {@link MxstarParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitPrefixExpression(MxstarParser.PrefixExpressionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code arrayExpression}
	 * labeled alternative in {@link MxstarParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterArrayExpression(MxstarParser.ArrayExpressionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code arrayExpression}
	 * labeled alternative in {@link MxstarParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitArrayExpression(MxstarParser.ArrayExpressionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code suffixExpression}
	 * labeled alternative in {@link MxstarParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterSuffixExpression(MxstarParser.SuffixExpressionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code suffixExpression}
	 * labeled alternative in {@link MxstarParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitSuffixExpression(MxstarParser.SuffixExpressionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code newExpression}
	 * labeled alternative in {@link MxstarParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterNewExpression(MxstarParser.NewExpressionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code newExpression}
	 * labeled alternative in {@link MxstarParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitNewExpression(MxstarParser.NewExpressionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code assignExpression}
	 * labeled alternative in {@link MxstarParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterAssignExpression(MxstarParser.AssignExpressionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code assignExpression}
	 * labeled alternative in {@link MxstarParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitAssignExpression(MxstarParser.AssignExpressionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code memberExpression}
	 * labeled alternative in {@link MxstarParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterMemberExpression(MxstarParser.MemberExpressionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code memberExpression}
	 * labeled alternative in {@link MxstarParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitMemberExpression(MxstarParser.MemberExpressionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code funcCallExpression}
	 * labeled alternative in {@link MxstarParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterFuncCallExpression(MxstarParser.FuncCallExpressionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code funcCallExpression}
	 * labeled alternative in {@link MxstarParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitFuncCallExpression(MxstarParser.FuncCallExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxstarParser#creator}.
	 * @param ctx the parse tree
	 */
	void enterCreator(MxstarParser.CreatorContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxstarParser#creator}.
	 * @param ctx the parse tree
	 */
	void exitCreator(MxstarParser.CreatorContext ctx);
}