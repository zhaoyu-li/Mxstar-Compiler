package FrontEnd;

import AST.*;
import IR.Instruction.*;
import IR.Function.FuncType;
import IR.Operand.*;
import Scope.*;
import IR.*;
import Type.Type;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Stack;

import static IR.RegisterSet.*;

public class IRBuilder implements ASTVistor {
    private IRProgram program;
    private GlobalScope globalScope;
    private Scope curScope;
    private Function curFunction;
    private BasicBlock curBB;
    private String curClassName;
    private VirtualRegister curThis;
    private Stack<BasicBlock> loopConditionBB;
    private Stack<BasicBlock> loopAfterBB;

    private HashMap<Expression, Operand> exprResultMap;
    private HashMap<Expression, Address> assignMap;

    private Boolean isInParameters;

    public IRBuilder(GlobalScope globalScope) {
        this.globalScope = globalScope;
        curScope = globalScope;
        curFunction = null;
        curBB = null;
        curClassName = null;
        curThis = null;
        program = new IRProgram();
        loopConditionBB = new Stack<>();
        loopAfterBB = new Stack<>();
        isInParameters = false;
    }

    public IRProgram getProgram() {
        return program;
    }

    private void buildInitFunction(Program node) {
        curFunction = program.getFunction("variable_init");
        curFunction.setHeadBB(new BasicBlock("head", curFunction));
        curBB = curFunction.getHeadBB();
        for(VariableDeclaration variableDeclaration : node.getVariables()) {
            Expression init = variableDeclaration.getInit();
            if(init != null) {

            }
        }
        curBB.addNextInst(new Call(curBB, vrax, program.getFunction("main")));
        curBB.addNextInst(new Return(curBB));
        curFunction.setTailBB(curBB);
    }

    @Override
    public void visit(Program node) {
        for(VariableDeclaration variableDeclaration : node.getVariables()) {
            StaticVariable var = new StaticVariable(variableDeclaration.getName(), 4);
            VirtualRegister vr = new VirtualRegister(variableDeclaration.getName());
            program.addStaticData(var);
            variableDeclaration.getVariableEntity().setVirtualRegister(vr);
        }
        for(FunctionDeclaration functionDeclaration : node.getFunctions()) {
            Function function = new Function(FuncType.UserDefined, functionDeclaration.getName());
            program.addFunction(function);
        }
        for(ClassDeclaration classDeclaration : node.getClasses()) {
            FunctionDeclaration constructor = classDeclaration.getConstructor();
            if(constructor != null) {
                Function function = new Function(FuncType.UserDefined, constructor.getName());
                program.addFunction(function);
            }
            for(FunctionDeclaration functionDeclaration : classDeclaration.getMethods()) {
                Function function = new Function(FuncType.UserDefined, functionDeclaration.getName());
                program.addFunction(function);
            }
        }
        for(FunctionDeclaration functionDeclaration : node.getFunctions()) {
            functionDeclaration.accept(this);
        }
        for(ClassDeclaration classDeclaration : node.getClasses()) {
            classDeclaration.accept(this);
        }
    }

    @Override
    public void visit(Declaration node) {

    }

    @Override
    public void visit(FunctionDeclaration node) {
        curFunction = program.getFunction(node.getFunctionEntity().getName());
        curFunction.setHeadBB(new BasicBlock("head", curFunction));
        curBB = curFunction.getHeadBB();
        if(curClassName != null) {
            VirtualRegister virthis = new VirtualRegister("this");
            curFunction.addParameter(virthis);
            curThis = virthis;
        }
        isInParameters = true;
        for(VariableDeclaration variableDeclaration : node.getParameters()) {
            variableDeclaration.accept(this);
        }
        isInParameters = false;
        for(int i = 0; i < curFunction.getParameters().size(); i++) {
            if(i < 6) {
                curBB.addNextInst(new Move(curBB, vargs.get(i), curFunction.getParameters().get(i)));
            } else {
                curBB.addNextInst(new Move(curBB, curFunction.getParameters().get(i).getSpillPlace(), curFunction.getParameters().get(i)));
            }
        }
        for(VariableEntity var : node.getFunctionEntity().getGlobalVariables()) {
            curBB.addNextInst(new Move(curBB, var.getVirtualRegister().getSpillPlace(), var.getVirtualRegister()));
        }
        for(Statement statement : node.getBody()) {
            statement.accept(this);
        }
        if(!(curBB.getTail() instanceof Return)) {
            if(node.getFunctionEntity().getReturnType().getType() == Type.types.VOID) {
                curBB.addNextInst(new Return(curBB));
            } else {
                curBB.addNextInst(new Move(curBB, vrax, new IntImmediate(0)));
                curBB.addNextInst(new Return(curBB));
            }
        }
        BasicBlock tailBB= new BasicBlock("tail", curFunction);
        for(Return ret : curFunction.getReturnList()) {
            ret.prepend(new Jump(ret.getBB(), tailBB));
            ret.remove();
        }
        tailBB.addNextInst(new Return(tailBB));
        curFunction.setTailBB(tailBB);

        Instruction retInst = curFunction.getTailBB().getTail();
        for(VariableEntity variableEntity : node.getFunctionEntity().getGlobalVariables()) {
            retInst.prepend(new Move(retInst.getBB(), variableEntity.getVirtualRegister().getSpillPlace(), variableEntity.getVirtualRegister()));
        }

    }

    @Override
    public void visit(ClassDeclaration node) {
        curClassName = node.getName();
        if(node.getConstructor() != null) {
            node.getConstructor().accept(this);
        }
        for(FunctionDeclaration functionDeclaration : node.getMethods()) {
            functionDeclaration.accept(this);
        }
        curClassName = null;
    }

    private void assign(Expression expr, Address vr) {
        expr.accept(this);
        Operand result = exprResultMap.get(expr);
        if(result != vr) {
            curBB.addNextInst(new Move(curBB, vr, result));
        }
    }

    @Override
    public void visit(VariableDeclaration node) {
        VirtualRegister vr = new VirtualRegister(node.getName());
        if(isInParameters) {
            curFunction.addParameter(vr);
        }
        node.getVariableEntity().setVirtualRegister(vr);
        if(node.getInit() != null) {
            assign(node.getInit(), vr);
        }
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
    public void visit(EmptyStatement node) {

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
