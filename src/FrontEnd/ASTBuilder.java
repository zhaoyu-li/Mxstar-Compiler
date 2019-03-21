package FrontEnd;

import AST.*;
import Parser.MxstarParser;
import Parser.MxstarBaseVisitor;
import FrontEnd.PaserErrorListener;
import Type.Type;
import Utility.SemanticError;

import static Parser.MxstarParser.*;

import java.util.LinkedList;
import java.util.List;

public class ASTBuilder extends MxstarBaseVisitor<Object> {
    private Program program;

    public ASTBuilder() {
        this.program = new Program();
    }

    public Program getProgram() {
        return program;
    }

    @Override
    public Program visitProgram(ProgramContext context) {
        for(GlobalDeclarationContext ctx : context.globalDeclaration()) {
            if(ctx.functionDeclaration() != null) {
                program.add(visitFunctionDeclaration(ctx.functionDeclaration()));
            } else if(ctx.classDeclaration() != null) {
                program.add(visitClassDeclaration(ctx.classDeclaration()));
            } else if(ctx.variableDeclaration() != null) {
                program.addAll(visitVariableDeclaration(ctx.variableDeclaration()));
            } else {
                throw new SemanticError(new Location(ctx), "Invalid global-declaration");
            }
        }
        return program;
    }

    @Override
    public FunctionDeclaration visitFunctionDeclaration(FunctionDeclarationContext ctx) {
        FunctionDeclaration functionDeclaration = new FunctionDeclaration();
        functionDeclaration.setReturnType(visitType(ctx.type()));
        functionDeclaration.setName(ctx.IDENTIFIER().getSymbol().getText());
        functionDeclaration.setParameters(visitParameterList(ctx.parameterList()));
        functionDeclaration.setBody(visitStatementList(ctx.block().statementList()));
        functionDeclaration.setLocation(ctx);
        return functionDeclaration;
    }

    @Override
    public ClassDeclaration visitClassDeclaration(ClassDeclarationContext ctx) {
        ClassDeclaration classDeclaration = new ClassDeclaration();
        classDeclaration.setName(ctx.IDENTIFIER().getSymbol().getText());
        List<VariableDeclaration> fields = new LinkedList<>();
        List<FunctionDeclaration> methods = new LinkedList<>();
        classDeclaration.setLocation(ctx);
        if(!ctx.constructorDeclaration().isEmpty()){
            classDeclaration.setConstructor(visitConstructorDeclaration(ctx.constructorDeclaration(0)));
            for(int i = 1; i < ctx.constructorDeclaration().size(); i++) {
                throw new SemanticError(new Location(ctx.constructorDeclaration(i)), "Invalid number of constuctor");
            }
        }
        for(FieldDeclarationContext c : ctx.fieldDeclaration()) {
            fields.addAll(visitFieldDeclaration(c));
        }
        for(FunctionDeclarationContext c : ctx.functionDeclaration()) {
            methods.add(visitFunctionDeclaration(c));
        }
        return classDeclaration;
    }

    @Override
    public FunctionDeclaration visitConstructorDeclaration(ConstructorDeclarationContext ctx) {
        FunctionDeclaration constructor = new FunctionDeclaration();
        constructor.setName(ctx.IDENTIFIER().getSymbol().getText());
        constructor.setParameters(visitParameterList(ctx.parameterList()));
        constructor.setBody(visitStatementList(ctx.block().statementList()));
        return constructor;
    }

    @Override
    public List<Statement> visitStatementList(StatementListContext ctx) {
        List<Statement> statementList = new LinkedList<>();
        for(StatementContext c : ctx.statement()) {
            if(c instanceof VarDeclStatementContext) {
                statementList.addAll(visitVarDeclStatement(((VarDeclStatementContext) c)));
            } else {
                statementList.add((Statement) c.accept(this));
            }
        }
        return statementList;
    }

    @Override
    public List<Statement> visitVarDeclStatement(VarDeclStatementContext ctx) {
        List<VariableDeclaration> declarations = visitVariableDeclaration(ctx.variableDeclaration());
        List<Statement> statements = new LinkedList<>();
        for(VariableDeclaration declaration : declarations) {
            VarDeclStatement varDeclStatement = new VarDeclStatement();
            varDeclStatement.setDeclaration(declaration);
            statements.add(varDeclStatement);
        }
        return statements;
    }

    @Override
    public List<VariableDeclaration> visitFieldDeclaration(FieldDeclarationContext ctx) {
        List<VariableDeclaration> fields = visitVariableDeclarators(ctx.variableDeclarators());
        TypeNode typeNode = visitType(ctx.type());
        for(VariableDeclaration declaration : fields) {
            declaration.setType(typeNode);
        }
        return fields;
    }

    @Override
    public List<VariableDeclaration> visitVariableDeclarators(VariableDeclaratorsContext ctx) {
        List<VariableDeclaration> declarations = new LinkedList<>();
        for(VariableDeclaratorContext c : ctx.variableDeclarator()) {
            declarations.add(visitVariableDeclarator(c));
        }
        return  declarations;
    }

    @Override
    public List<VariableDeclaration> visitVariableDeclaration(VariableDeclarationContext ctx) {
        List<VariableDeclaration> declarations = visitVariableDeclarators(ctx.variableDeclarators());
        TypeNode typeNode = visitType(ctx.type());
        for(VariableDeclaration declaration : declarations) {
            declaration.setType(typeNode);
        }
        return declarations;
    }

    @Override
    public VariableDeclaration visitVariableDeclarator(VariableDeclaratorContext ctx) {
        VariableDeclaration declaration = new VariableDeclaration();
        declaration.setName(ctx.IDENTIFIER().getSymbol().getText());
        declaration.setLocation(ctx);
        if(ctx.expression() != null) {
            declaration.setInit((Expression) ctx.expression().accept(this));
        }
        return declaration;
    }

    @Override
    public List<VariableDeclaration> visitParameterList(ParameterListContext ctx) {
        List<VariableDeclaration> parameterList = new LinkedList<>();
        if(ctx != null) {
            for(ParameterContext c : ctx.parameter()) {
                parameterList.add((VariableDeclaration) c.accept(this));
            }
        }
        return parameterList;
    }

    @Override
    public TypeNode visitType(TypeContext ctx) {
        if(ctx.empty().isEmpty()) {
            if(ctx.primitiveType() != null) {
                return visitPrimitiveType(ctx.primitiveType());
            } else {
                return visitClassType(ctx.classType());
            }
        } else {
            if(ctx.primitiveType() != null) {
                return new ArrayTypeNode(visitPrimitiveType(ctx.primitiveType()), ctx.empty().size());
            } else {
                return new ArrayTypeNode(visitClassType(ctx.classType()), ctx.empty().size());
            }
        }
    }

    @Override
    public TypeNode visitPrimitiveType(PrimitiveTypeContext ctx) {
        return new TypeNode(new Type(ctx.token.getText()), new Location(ctx));
    }

    @Override
    public TypeNode visitClassType(ClassTypeContext ctx) {
        return new TypeNode(new Type(ctx.token.getText()), new Location(ctx));
    }

    @Override
    public IfStatement visitIfStatement(IfStatementContext ctx) {
        IfStatement ifStatement = new IfStatement();
        ifStatement.setCondition((Expression) ctx.expression().accept(this));
        ifStatement.setElseStatement((Statement) ctx.statement(0).accept(this));
        ifStatement.setLocation(ctx);
        return ifStatement;
    }

    @Override
    public WhileStatement visitWhileStatement(WhileStatementContext ctx) {
        WhileStatement whileStatement = new WhileStatement();
        whileStatement.setCondition((Expression) ctx.expression().accept(this));
        whileStatement.setBody((Statement) ctx.statement().accept(this));
        whileStatement.setLocation(ctx);
        return whileStatement;
    }

    @Override
    public ForStatement visitForStatement(ForStatementContext ctx) {
        ForStatement forStatement = new ForStatement();
        forStatement.setInit((Expression) ctx.forInit.accept(this));
        forStatement.setCondition((Expression) ctx.forCondition.accept(this));
        forStatement.setUpdate((Expression) ctx.forUpdate.accept(this));
        forStatement.setBody((Statement) ctx.statement().accept(this));
        forStatement.setLocation(ctx);
        return forStatement;
    }

    @Override
    public BreakStatement visitBreakStatement(BreakStatementContext ctx) {
        BreakStatement breakStatement = new BreakStatement();
        breakStatement.setLocation(ctx);
        return breakStatement;
    }

    @Override
    public ContinueStatement visitContinueStatement(ContinueStatementContext ctx) {
        ContinueStatement continueStatement = new ContinueStatement();
        continueStatement.setLocation(ctx);
        return continueStatement;
    }

    @Override
    public ReturnStatement visitReturnStatement(ReturnStatementContext ctx) {
        ReturnStatement returnStatement = new ReturnStatement();
        returnStatement.setRet((Expression) ctx.expression().accept(this));
        returnStatement.setLocation(ctx);
        return returnStatement;
    }

    @Override
    public ExprStatement visitExprStatement(ExprStatementContext ctx) {
        ExprStatement exprStatement = new ExprStatement();
        exprStatement.setExpr((Expression) ctx.expression().accept(this));
        exprStatement.setLocation(ctx);
        return exprStatement;
    }

    @Override
    public BlockStatement visitBlockStatement(BlockStatementContext ctx) {
        BlockStatement blockStatement = new BlockStatement();
        blockStatement.setStatements(visitStatementList(ctx.block().statementList()));
        blockStatement.setLocation(ctx);
        return blockStatement;
    }

    @Override
    public Expression visitPrimaryExpression(PrimaryExpressionContext ctx) {
        if(ctx.token == null) {
            return (Expression) ctx.expression().accept(this);
        } else {
            switch (ctx.token.getType()) {
                case THIS:
                    ThisExpression thisExpression = new ThisExpression();
                    thisExpression.setLocation(ctx);
                    return thisExpression;
                case NULL_LITERAL:
                    NullLiteral nullLiteral = new NullLiteral();
                    nullLiteral.setLocation(ctx);
                    return nullLiteral;
                case BOOL_LITERAL:
                    BoolLiteral boolLiteral = new BoolLiteral();
                    boolLiteral.setValue(ctx.token.getText());
                    boolLiteral.setLocation(ctx);
                    return boolLiteral;
                case INT_LITERAL:
                    IntLiteral intLiteral = new IntLiteral();
                    intLiteral.setValue(ctx.token.getText());
                    intLiteral.setLocation(ctx);
                    return intLiteral;
                case STRING_LITERAL:
                    StringLiteral stringLiteral = new StringLiteral();
                    stringLiteral.setValue(ctx.token.getText());
                    stringLiteral.setLocation(ctx);
                    return stringLiteral;
                case IDENTIFIER:
                    Identifier identifier = new Identifier();
                    identifier.setName(ctx.IDENTIFIER().getSymbol().getText());
                    identifier.setLocation(ctx);
                    return identifier;
                default:
                    throw new SemanticError(new Location(ctx), "Invalid primary expression");
            }
        }
    }


    @Override
    public MemberExpression visitMemberExpression(MemberExpressionContext ctx) {
        MemberExpression memberExpression = new MemberExpression();
        memberExpression.setExpr((Expression) ctx.expression().accept(this));
        memberExpression.serMember(ctx.IDENTIFIER().getSymbol().getText());
        memberExpression.setLocation(ctx);
        return memberExpression;
    }

    @Override
    public FuncCallExpression visitFuncCallExpression(FuncCallExpressionContext ctx) {
        FuncCallExpression funcCallExpression = new FuncCallExpression();
        funcCallExpression.setName(ctx.IDENTIFIER().getSymbol().getText());
        List<Expression> arguments = new LinkedList<>();
        if(ctx.expressionList().expression() != null) {
            for(ExpressionContext c : ctx.expressionList().expression()) {
                arguments.add((Expression) c.accept(this));
            }
        }
        funcCallExpression.setArguments(arguments);
        funcCallExpression.setLocation(ctx);
        return funcCallExpression;
    }

    @Override
    public ArrayExpression visitArrayExpression(ArrayExpressionContext ctx) {
        ArrayExpression arrayExpression = new ArrayExpression();
        arrayExpression.setArr((Expression) ctx.arr.accept(this));
        arrayExpression.setIdx((Expression) ctx.idx.accept(this));
        arrayExpression.setLocation(ctx);
        return arrayExpression;
    }

    @Override
    public NewExpression visitNewExpression(NewExpressionContext ctx) {
        return visitCreator(ctx.creator());
    }

    @Override
    public SuffixExpression visitSuffixExpression(SuffixExpressionContext ctx) {
        SuffixExpression suffixExpression = new SuffixExpression();
        suffixExpression.setOp(ctx.op.getText());
        suffixExpression.setExpr((Expression) ctx.expression().accept(this));
        suffixExpression.setLocation(ctx);
        return suffixExpression;
    }

    @Override
    public PrefixExpression visitPrefixExpression(PrefixExpressionContext ctx) {
        PrefixExpression prefixExpression = new PrefixExpression();
        prefixExpression.setOp(ctx.op.getText());
        prefixExpression.setExpr((Expression) ctx.expression().accept(this));
        prefixExpression.setLocation(ctx);
        return prefixExpression;
    }

    @Override
    public BinaryExpression visitBinaryExpression(BinaryExpressionContext ctx) {
        BinaryExpression binaryExpression = new BinaryExpression();
        binaryExpression.setOp(ctx.op.getText());
        binaryExpression.setLhs((Expression) ctx.lhs.accept(this));
        binaryExpression.setRhs((Expression) ctx.rhs.accept(this));
        binaryExpression.setLocation(ctx);
        return binaryExpression;
    }

    @Override
    public AssignExpression visitAssignExpression(AssignExpressionContext ctx) {
        AssignExpression assignExpression = new AssignExpression();
        assignExpression.setLhs((Expression) ctx.lhs.accept(this));
        assignExpression.setRhs((Expression) ctx.rhs.accept(this));
        assignExpression.setLocation(ctx);
        return assignExpression;
    }

    @Override
    public NewExpression visitCreator(CreatorContext ctx) {
        NewExpression newExpression = new NewExpression();
        if(ctx.primitiveType() != null) {
            newExpression.setTypeNode(visitPrimitiveType(ctx.primitiveType()));
        } else {
            newExpression.setTypeNode(visitClassType(ctx.classType()));
        }
        List<Expression> dimensions = new LinkedList<>();
        if(ctx.expression() != null) {
            for(ExpressionContext c : ctx.expression()) {
                dimensions.add((Expression) c.accept(this));
            }
        }
        if(ctx.empty() != null) {
            newExpression.setNumDimension(ctx.empty().size());
        } else {
            newExpression.setNumDimension(0);
        }
        newExpression.setLocation(ctx);
        return newExpression;
    }
}
