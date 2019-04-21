package FrontEnd;

import AST.*;

public class ASTPrinter implements ASTVistor {
    private StringBuilder stringBuilder;
    private String curIndent;
    private final String indent;

    public ASTPrinter() {
        stringBuilder = new StringBuilder();
        curIndent = "";
        indent = "    ";
    }

    public void print() {
        System.out.print(stringBuilder.toString());
    }

    private void indent() {
        curIndent += indent;
    }

    private void unindent() {
        curIndent = curIndent.substring(0, curIndent.length() - indent.length());
    }

    private void appendNewLine(String line) {
        if(stringBuilder.length() != 0) {
            stringBuilder.append('\n');
        }
        stringBuilder.append(curIndent);
        stringBuilder.append(line);
    }

    @Override
    public void visit(Program node) {
        appendNewLine("Program, Location: " + node.getLocation().toString());
        indent();
        for(FunctionDeclaration functionDeclaration : node.getFunctions()) {
            visit(functionDeclaration);
        }

        for(ClassDeclaration classDeclaration : node.getClasses()) {
            visit(classDeclaration);
        }

        for(VariableDeclaration variableDeclaration : node.getVariables()) {
            visit(variableDeclaration);
        }
        unindent();
    }

    @Override
    public void visit(FunctionDeclaration node) {
        appendNewLine("FunctionDeclaration, Location: " + node.getLocation().toString());
        if(node.getReturnType() != null) {
            indent();
            appendNewLine("ReturnType : ");
            visit(node.getReturnType());
            unindent();
        }
        if(node.getParameters() != null) {
            indent();
            for(VariableDeclaration variableDeclaration : node.getParameters()) {
                visit(variableDeclaration);
            }
            unindent();
        }
        if(node.getBody() != null) {
            indent();
            for(Statement statement : node.getBody()) {
                statement.accept(this);
            }
            unindent();
        }
    }

    @Override
    public void visit(ClassDeclaration node) {
        appendNewLine("ClassDeclaration, Location: " + node.getLocation().toString());
        if(node.getConstructor() != null) {
            indent();
            appendNewLine("Constructor : ");
            visit(node.getConstructor());
            unindent();
        }
        if(node.getFields() != null) {
            indent();
            for(VariableDeclaration variableDeclaration : node.getFields()) {
                visit(variableDeclaration);
            }
            unindent();
        }
        if(node.getMethods() != null) {
            indent();
            for(FunctionDeclaration functionDeclaration : node.getMethods()) {
                visit(functionDeclaration);
            }
            unindent();
        }
    }

    @Override
    public void visit(VariableDeclaration node) {
        appendNewLine("VariableDeclaration, Locatoin: " + node.getLocation().toString());
        if(node.getType() != null) {
            indent();
            visit(node.getType());
            unindent();
        }
        if(node.getInit() != null) {
            indent();
            node.getInit().accept(this);
            unindent();
        }
    }

    @Override
    public void visit(TypeNode node) {
        appendNewLine("Type: " + node.getType().toString());
    }

    @Override
    public void visit(ArrayTypeNode node) {
        appendNewLine("ArrayType: " + node.getType().toString() + ", Dimemsion: " + String.valueOf(node.getDimension()));
    }


    @Override
    public void visit(IfStatement node) {
        appendNewLine("IfStatement: " + node.getLocation().toString());
        indent();
        node.getCondition().accept(this);
        indent();
        node.getThenStatement().accept(this);
        if(node.getElseStatement() != null) {
            node.getElseStatement().accept(this);
        }
        unindent();
        unindent();
    }

    @Override
    public void visit(WhileStatement node) {
        appendNewLine("WhileStatement: " + node.getLocation().toString());
        indent();
        node.getCondition().accept(this);
        indent();
        node.getBody().accept(this);
        unindent();
        unindent();
    }

    @Override
    public void visit(ForStatement node) {
        appendNewLine("ForStatement: " + node.getLocation().toString());
        indent();
        node.getInit().accept(this);
        node.getCondition().accept(this);
        node.getUpdate().accept(this);
        indent();
        node.getBody().accept(this);
        unindent();
        unindent();
    }

    @Override
    public void visit(BreakStatement node) {
        appendNewLine("BreakStatement: " + node.getLocation().toString());
    }

    @Override
    public void visit(ContinueStatement node) {
        appendNewLine("ContinueStatement: " + node.getLocation().toString());
    }

    @Override
    public void visit(ReturnStatement node) {
        appendNewLine("ReturnStatement: " + node.getLocation().toString());
        indent();
        if(node.getRet() != null){
            node.getRet().accept(this);
        }
        unindent();
    }

    @Override
    public void visit(ExprStatement node) {
        appendNewLine("ExprStatement: " + node.getLocation().toString());
        indent();
        node.getExpr().accept(this);
        unindent();
    }

    @Override
    public void visit(VarDeclStatement node) {
        appendNewLine("VarDeclStatement: " + node.getLocation().toString());
        indent();
        node.getDeclaration().accept(this);
        unindent();
    }

    @Override
    public void visit(BlockStatement node) {
        appendNewLine("BlockStatement: " + node.getLocation().toString());
        if(node.getStatements() != null) {
            for(Statement statement : node.getStatements()) {
                indent();
                statement.accept(this);
                unindent();
            }
        }
    }

    @Override
    public void visit(EmptyStatement node) {

    }

    @Override
    public void visit(ThisExpression node) {
        appendNewLine("ThisExpression: " + node.getLocation().toString());
    }

    @Override
    public void visit(NullLiteral node) {
        appendNewLine("NullLiteral: " + node.getLocation().toString());
    }

    @Override
    public void visit(BoolLiteral node) {
        appendNewLine("BoolLiteral: " + String.valueOf(node.getValue()) + " "+ node.getLocation().toString());
    }

    @Override
    public void visit(IntLiteral node) {
        appendNewLine("IntLiteral: " + String.valueOf(node.getValue()) + " " + node.getLocation().toString());
    }

    @Override
    public void visit(StringLiteral node) {
        appendNewLine("StaticString: " + node.getValue() + " " + node.getLocation().toString());
    }

    @Override
    public void visit(Identifier node) {
        appendNewLine("Identifier: " + node.getName() + " " + node.getLocation().toString());
    }

    @Override
    public void visit(MemberExpression node) {
        appendNewLine("MemberExpression: " + node.getLocation().toString());
        indent();
        node.getExpr().accept(this);
        indent();
        if(node.getMember() != null){
            node.getMember().accept(this);
        } else {
            node.getFuncCall().accept(this);
        }
        unindent();
        unindent();
    }

    @Override
    public void visit(ArrayExpression node) {
        appendNewLine("ArrayExpression: " + node.getLocation().toString());
        indent();
        node.getArr().accept(this);
        indent();
        node.getIdx().accept(this);
        unindent();
        unindent();
    }

    @Override
    public void visit(FuncCallExpression node) {
        appendNewLine("FuncCallExpression: " + node.getLocation().toString());
        indent();
        visit(node.getName());
        indent();
        for(Expression expression : node.getArguments()) {
            expression.accept(this);
        }
        unindent();
        unindent();
    }

    @Override
    public void visit(NewExpression node) {
        appendNewLine("NewExpression: " + node.getLocation().toString());
        indent();
        visit(node.getTypeNode());
        indent();
        for(Expression expression : node.getDimensions()) {
            expression.accept(this);
        }
        appendNewLine("RestDimension: " + String.valueOf(node.getNumDimension()));
        unindent();
        unindent();
    }

    @Override
    public void visit(SuffixExpression node) {
        appendNewLine("SuffixExpression: " + node.getLocation().toString());
        indent();
        appendNewLine("Op: " + node.getOp());
        node.getExpr().accept(this);
        unindent();
    }

    @Override
    public void visit(PrefixExpression node) {
        appendNewLine("PrefixExpression: " + node.getLocation().toString());
        indent();
        appendNewLine("Op: " + node.getOp());
        node.getExpr().accept(this);
        unindent();
    }

    @Override
    public void visit(BinaryExpression node) {
        appendNewLine("BinaryExpression: " + node.getLocation().toString());
        indent();
        node.getLhs().accept(this);
        appendNewLine("Op: " + node.getOp());
        node.getRhs().accept(this);
        unindent();
    }

    @Override
    public void visit(AssignExpression node) {
        appendNewLine("AssignExpression: " + node.getLocation().toString());
        indent();
        node.getLhs().accept(this);
        node.getRhs().accept(this);
        unindent();
    }
}
