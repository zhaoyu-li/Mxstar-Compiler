package FrontEnd;

import AST.*;
import Parser.MxstarParser;
import Parser.MxstarBaseVisitor;
import FrontEnd.PaserErrorListener;

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
    public Object visitProgram(ProgramContext ctx) {
        for(GlobalDeclarationContext c : ctx.globalDeclaration()) {
            if(c.functionDeclaration() != null) {
                program.add(visitFunctionDeclaration(c.functionDeclaration()));
            } else if(c.classDeclaration() != null) {
                program.add(visitClassDeclaration(c.classDeclaration()));
            } else if(c.variableDeclaration() != null) {
                program.addAll(visitVariableDeclaration(c.variableDeclaration()));
            }
        }
        return null;
    }

    @Override
    public FunctionDeclaration visitFunctionDeclaration(FunctionDeclarationContext ctx) {
        FunctionDeclaration functionDeclaration = new FunctionDeclaration();
        return functionDeclaration;
    }

    @Override
    public ClassDeclaration visitClassDeclaration(ClassDeclarationContext ctx) {
        ClassDeclaration classDeclaration = new ClassDeclaration();
        return classDeclaration;
    }

    @Override
    public FunctionDeclaration visitConstructorDeclaration(ConstructorDeclarationContext ctx) {
        FunctionDeclaration constructor = new FunctionDeclaration();
        return constructor;
    }

    @Override
    public List<VariableDeclaration> visitFieldDeclaration(FieldDeclarationContext ctx) {
        List<VariableDeclaration> fields = new LinkedList<>();
        return fields;
    }

    @Override
    public List<VariableDeclaration> visitVariableDeclaration(VariableDeclarationContext ctx) {
        List<VariableDeclaration> declarations = new LinkedList<>();
        return declarations;
    }

    @Override
    public VariableDeclaration visitVariableDeclarator(VariableDeclaratorContext ctx) {
        VariableDeclaration declaration = new VariableDeclaration();
        return declaration;
    }
}
