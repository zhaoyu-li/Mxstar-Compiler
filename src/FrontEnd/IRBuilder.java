package FrontEnd;

import AST.*;
import IR.Instruction.*;
import IR.Function.FuncType;
import IR.Operand.*;
import Scope.*;
import IR.*;
import Type.*;
import Utility.Config;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Stack;

import static IR.RegisterSet.*;

public class IRBuilder implements ASTVistor {
    private IRProgram program;
    private GlobalScope globalScope;
    private Function curFunction;
    private BasicBlock curBB;
    private String curClassName;
    private VirtualRegister curThis;
    private Stack<BasicBlock> loopConditionBB;
    private Stack<BasicBlock> loopAfterBB;

    private HashMap<Expression, BasicBlock> trueBBMap;
    private HashMap<Expression, BasicBlock> falseBBMap;

    private HashMap<Expression, Operand> exprResultMap;
    private HashMap<Expression, Address> assignMap;

    private Boolean isInParameters;

    public IRBuilder(GlobalScope globalScope) {
        this.globalScope = globalScope;
        curFunction = null;
        curBB = null;
        curClassName = null;
        curThis = null;
        program = new IRProgram();
        loopConditionBB = new Stack<>();
        loopAfterBB = new Stack<>();
        trueBBMap = new HashMap<>();
        falseBBMap = new HashMap<>();
        exprResultMap = new HashMap<>();
        assignMap = new HashMap<>();
        isInParameters = false;
    }

    public IRProgram getProgram() {
        return program;
    }

    private void registerVariable(VariableDeclaration variableDeclaration) {
        StaticVariable var = new StaticVariable(variableDeclaration.getName(), Config.getRegSize());
        VirtualRegister vr = new VirtualRegister(variableDeclaration.getName());
        vr.setSpillPlace(new Memory(var));
        program.addStaticData(var);
        variableDeclaration.getVariableEntity().setVirtualRegister(vr);
    }

    private void registerFunction(FunctionDeclaration functionDeclaration) {
        Function function = new Function(FuncType.UserDefined, functionDeclaration.getFunctionEntity().getName());
        program.addFunction(function);
    }

    private void buildInitFunction(Program node) {
        curFunction = program.getFunction("globalInit");
        curFunction.setHeadBB(new BasicBlock("head", curFunction));
        curBB = curFunction.getHeadBB();
        for(VariableDeclaration variableDeclaration : node.getVariables()) {
            Expression init = variableDeclaration.getInit();
            if(init != null) {
                assign(variableDeclaration.getInit(), variableDeclaration.getVariableEntity().getVirtualRegister());
            }
        }
        curBB.addNextInst(new Call(curBB, vrax, program.getFunction("main")));
        curBB.addNextInst(new Return(curBB));
        curFunction.setTailBB(curBB);
    }

    @Override
    public void visit(Program node) {
        for(VariableDeclaration variableDeclaration : node.getVariables()) {
            registerVariable(variableDeclaration);
        }
        for(FunctionDeclaration functionDeclaration : node.getFunctions()) {
            registerFunction(functionDeclaration);
        }
        for(ClassDeclaration classDeclaration : node.getClasses()) {
            FunctionDeclaration constructor = classDeclaration.getConstructor();
            if(constructor != null) {
                registerFunction(constructor);
            }
            for(FunctionDeclaration functionDeclaration : classDeclaration.getMethods()) {
                registerFunction(functionDeclaration);
            }
        }
        for(FunctionDeclaration functionDeclaration : node.getFunctions()) {
            functionDeclaration.accept(this);
        }
        for(ClassDeclaration classDeclaration : node.getClasses()) {
            classDeclaration.accept(this);
        }
        buildInitFunction(node);
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
            VirtualRegister thisPointer = new VirtualRegister("this");
            curFunction.addParameter(thisPointer);
            curThis = thisPointer;
        }
        isInParameters = true;
        for(VariableDeclaration variableDeclaration : node.getParameters()) {
            visit(variableDeclaration);
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
            curBB.addNextInst(new Move(curBB, var.getVirtualRegister(), var.getVirtualRegister().getSpillPlace()));
        }
        for(Statement statement : node.getBody()) {
            statement.accept(this);
        }
        if(!(curBB.getTail() instanceof Return)) {
            if(node.getFunctionEntity().getReturnType().getType() == Type.types.VOID) {
                curBB.addNextInst(new Return(curBB));
            }
        }
        BasicBlock tailBB = new BasicBlock("tail", curFunction);
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
        if(expr.getType().getType() == Type.types.BOOL) {
            boolAssign(expr, vr);
        } else {
            assignMap.put(expr, vr);
            expr.accept(this);
            Operand result = exprResultMap.get(expr);
            if(result != vr) {
                curBB.addNextInst(new Move(curBB, vr, result));
            }
        }
    }

    private void boolAssign(Expression expr, Address vr) {
        BasicBlock trueBB = new BasicBlock("true", curFunction);
        BasicBlock falseBB = new BasicBlock("false", curFunction);
        BasicBlock mergeBB = new BasicBlock("merge", curFunction);
        trueBBMap.put(expr, trueBB);
        falseBBMap.put(expr, falseBB);
        expr.accept(this);
        trueBB.addNextInst(new Move(trueBB, vr, new IntImmediate(1)));
        falseBB.addNextInst(new Move(falseBB, vr, new IntImmediate(0)));
        trueBB.addNextInst(new Jump(trueBB, mergeBB));
        falseBB.addNextInst(new Jump(falseBB, mergeBB));
        curBB = mergeBB;
    }

    @Override
    public void visit(VariableDeclaration node) {
        VirtualRegister vr = new VirtualRegister(node.getName());
        if(isInParameters) {
            if(curFunction.getParameters().size() > 6) {
                vr.setSpillPlace(new StackSlot(vr.getName()));
            }
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
        BasicBlock thenBB = new BasicBlock("ifThen", curFunction);
        BasicBlock afterBB = new BasicBlock("ifAfter", curFunction);
        BasicBlock elseBB = node.getElseStatement() == null ? afterBB : new BasicBlock("ifElse", curFunction);
        trueBBMap.put(node.getCondition(), thenBB);
        falseBBMap.put(node.getCondition(), elseBB);
        node.getCondition().accept(this);
        curBB = thenBB;
        node.getThenStatement().accept(this);
        curBB.addNextInst(new Jump(curBB, afterBB));
        if(node.getElseStatement() != null) {
            curBB = elseBB;
            node.getElseStatement().accept(this);
            curBB.addNextInst(new Jump(curBB, afterBB));
        }
        curBB = afterBB;
    }

    @Override
    public void visit(WhileStatement node) {
        BasicBlock condBB = new BasicBlock("whileCond", curFunction);
        BasicBlock bodyBB = new BasicBlock("whileBody", curFunction);
        BasicBlock afterBB = new BasicBlock("whileAfter", curFunction);
        curBB.addNextInst(new Jump(curBB, condBB));
        loopConditionBB.push(condBB);
        loopAfterBB.push(afterBB);
        curBB = condBB;
        trueBBMap.put(node.getCondition(), bodyBB);
        falseBBMap.put(node.getCondition(), afterBB);
        node.getCondition().accept(this);
        curBB = bodyBB;
        node.getBody().accept(this);
        curBB.addNextInst(new Jump(curBB, condBB));
        curBB = afterBB;
        loopConditionBB.pop();
        loopAfterBB.pop();
    }

    @Override
    public void visit(ForStatement node) {
        if(node.getInit() != null) {
            node.getInit().accept(this);
        }
        BasicBlock bodyBB = new BasicBlock("forBody", curFunction);
        BasicBlock afterBB = new BasicBlock("forAfter", curFunction);
        BasicBlock condBB = node.getCondition() == null ? bodyBB : new BasicBlock("forCond", curFunction);
        BasicBlock updateBB = node.getUpdate() == null ? condBB : new BasicBlock("forUpdate", curFunction);
        curBB.addNextInst(new Jump(curBB, condBB));
        loopConditionBB.push(condBB);
        loopAfterBB.push(afterBB);
        if(node.getCondition() != null) {
            trueBBMap.put(node.getCondition(), bodyBB);
            falseBBMap.put(node.getCondition(), afterBB);
            curBB = condBB;
            node.getCondition().accept(this);
        }
        curBB = bodyBB;
        node.getBody().accept(this);
        curBB.addNextInst(new Jump(curBB, updateBB));
        if(node.getUpdate() != null) {
            curBB = updateBB;
            node.getUpdate().accept(this);
            curBB.addNextInst(new Jump(curBB, condBB));
        }
        curBB = afterBB;
        loopConditionBB.pop();
        loopAfterBB.pop();
    }

    @Override
    public void visit(BreakStatement node) {
        curBB.addNextInst(new Jump(curBB, loopAfterBB.peek()));
    }

    @Override
    public void visit(ContinueStatement node) {
        curBB.addNextInst(new Jump(curBB, loopConditionBB.peek()));
    }

    @Override
    public void visit(ReturnStatement node) {
        if(node.getRet() != null) {
            node.getRet().accept(this);
            curBB.addNextInst(new Move(curBB, vrax, exprResultMap.get(node.getRet())));
        }
        curBB.addNextInst(new Return(curBB));
    }

    @Override
    public void visit(ExprStatement node) {
        node.getExpr().accept(this);
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
    public void visit(Expression node) {

    }

    @Override
    public void visit(ThisExpression node) {
    }

    @Override
    public void visit(NullLiteral node) {
        Operand operand = new IntImmediate(0);
        exprResultMap.put(node, operand);
    }

    @Override
    public void visit(BoolLiteral node) {
        curBB.addNextInst(new Jump(curBB, node.getValue() ? trueBBMap.get(node) : falseBBMap.get(node)));
    }

    @Override
    public void visit(IntLiteral node) {
        Operand operand = new IntImmediate(node.getValue());
        exprResultMap.put(node, operand);
    }

    @Override
    public void visit(StringLiteral node) {
        StaticString str = new StaticString("static_string", node.getValue());
        program.addStaticString(str);
        exprResultMap.put(node, str);
    }

    @Override
    public void visit(Identifier node) {
        Operand operand;
        if(node.getName().equals("this")) {
            operand = curThis;
        } else if(node.getVariableEntity().isInClass()) {
            int offset = globalScope.getClassEntity(curClassName).getScope().getVariableOffset(node.getName());
            operand = new Memory(curThis, new IntImmediate(offset));
        } else {
            operand = node.getVariableEntity().getVirtualRegister();
            if(node.getVariableEntity().isGlobal()) {
                curFunction.addGlobalVariable(node.getVariableEntity());
            }
        }
        if(trueBBMap.containsKey(node)) {
            curBB.addNextInst(new CJump(curBB, operand, CJump.CompareOp.NEQ, new IntImmediate(0), trueBBMap.get(node), falseBBMap.get(node)));
        } else {
            exprResultMap.put(node, operand);
        }
    }

    @Override
    public void visit(MemberExpression node) {
        VirtualRegister baseAddr = new VirtualRegister("");
        node.getExpr().accept(this);
        curBB.addNextInst(new Move(curBB, baseAddr, exprResultMap.get(node.getExpr())));
        if(node.getExpr().getType() instanceof ArrayType) {
            exprResultMap.put(node, new Memory(baseAddr));
        } else if(node.getExpr().getType() instanceof ClassType) {
            ClassType classType = (ClassType) node.getExpr().getType();
            Operand operand;
            if(node.getMember() != null) {
                String name = node.getMember().getName();
                int offset = classType.getClassEntity().getScope().getVariableOffset(name);
                operand = new Memory(baseAddr, new IntImmediate(offset));
            } else {
                Function function = program.getFunction(node.getFuncCall().getFunctionEntity().getName());
                LinkedList<Operand> arguments = new LinkedList<>();
                arguments.add(baseAddr);
                for (Expression expression : node.getFuncCall().getArguments()) {
                    expression.accept(this);
                    Operand argument = exprResultMap.get(expression);
                    arguments.add(argument);
                }
                curBB.addNextInst(new Call(curBB, vrax, function, arguments));
                if (node.getFuncCall().getFunctionEntity().getReturnType().getType() != Type.types.VOID) {
                    VirtualRegister ret = new VirtualRegister("");
                    curBB.addNextInst(new Move(curBB, ret, vrax));
                    operand = ret;
                } else {
                    operand = null;
                }
            }
            if(trueBBMap.containsKey(node)) {
                curBB.addNextInst(new CJump(curBB, operand, CJump.CompareOp.NEQ, new IntImmediate(0), trueBBMap.get(node), falseBBMap.get(node)));
            } else {
                exprResultMap.put(node, operand);

            }
        }
    }

    @Override
    public void visit(ArrayExpression node) {
        node.getArr().accept(this);
        Operand baseAddr = exprResultMap.get(node.getArr());
        node.getIdx().accept(this);
        Operand index = exprResultMap.get(node.getIdx());
        VirtualRegister base;
        if(baseAddr instanceof Register) {
            base = (VirtualRegister) baseAddr;
        } else {
            base = new VirtualRegister("");
            curBB.addNextInst(new Move(curBB, base, baseAddr));
        }
        Memory memory = new Memory();
        if(index instanceof IntImmediate) {
            memory = new Memory(base, new IntImmediate(((IntImmediate) index).getValue() * 4 + 4));
        } else if(index instanceof Register) {
            memory = new Memory(base, (Register) index, 4, new IntImmediate(4));
        } else if(index instanceof Memory) {
            VirtualRegister vr = new VirtualRegister("");
            curBB.addNextInst(new Move(curBB, vr, index));
            memory = new Memory(base, vr, 4, new IntImmediate(4));
        }
        if(trueBBMap.containsKey(node)) {
            curBB.addNextInst(new CJump(curBB, memory, CJump.CompareOp.NEQ, new IntImmediate(0), trueBBMap.get(node), falseBBMap.get(node)));
        } else {
            exprResultMap.put(node, memory);
        }
    }

    @Override
    public void visit(FuncCallExpression node) {
        LinkedList<Operand> arguments = new LinkedList<>();
        if(node.getFunctionEntity().getScope() != globalScope) {
            arguments.add(curThis);
        }
        for(Expression expression : node.getArguments()) {
            expression.accept(this);
            arguments.add(exprResultMap.get(expression));
        }
        curBB.addNextInst(new Call(curBB, vrax, program.getFunction(node.getFunctionEntity().getName()), arguments));
        if(trueBBMap.containsKey(node)) {
            curBB.addNextInst(new CJump(curBB, vrax, CJump.CompareOp.NEQ, new IntImmediate(0), trueBBMap.get(node), falseBBMap.get(node)));
        } else if(node.getFunctionEntity().getReturnType().getType() != Type.types.VOID) {
            VirtualRegister vr = new VirtualRegister("");
            curBB.addNextInst(new Move(curBB, vr, vrax));
            exprResultMap.put(node, vr);
        }
    }

    private Operand allocateArray(LinkedList<Operand> dims, int baseBytes, Function constructor) {
        if(dims.size() == 0) {
            if(baseBytes == 0) {
                return new IntImmediate(0);
            } else {
                VirtualRegister retAddr = new VirtualRegister("");
                curBB.addNextInst(new Call(curBB, vrax, program.getFunction("malloc"), new IntImmediate(baseBytes)));
                curBB.addNextInst(new Move(curBB, retAddr, vrax));
                if(constructor != null) {
                    curBB.addNextInst(new Call(curBB, vrax, constructor, retAddr));
                } else {
                    if(baseBytes == 4) {
                        curBB.addNextInst(new Move(curBB, new Memory(retAddr), new IntImmediate(0)));
                    } else if(baseBytes == 8) {
                        curBB.addNextInst(new BinaryOperation(curBB, retAddr, BinaryOperation.BinaryOp.ADD, new IntImmediate(4)));
                        curBB.addNextInst(new Move(curBB, new Memory(retAddr), new IntImmediate(0)));
                        curBB.addNextInst(new BinaryOperation(curBB, retAddr, BinaryOperation.BinaryOp.SUB, new IntImmediate(4)));
                    }
                }
                return retAddr;
            }
        } else {
            VirtualRegister addr = new VirtualRegister("");
            VirtualRegister size = new VirtualRegister("");
            VirtualRegister bytes = new VirtualRegister("");
            curBB.addNextInst(new Move(curBB, size, dims.get(0)));
            curBB.addNextInst(new Lea(curBB, bytes, new Memory(size, Config.getRegSize(), new IntImmediate(Config.getRegSize()))));
            curBB.addNextInst(new Call(curBB, vrax, program.getFunction("malloc"), bytes));
            curBB.addNextInst(new Move(curBB, addr, vrax));
            curBB.addNextInst(new Move(curBB, new Memory(addr), size));
            BasicBlock condBB = new BasicBlock("allocateCond", curFunction);
            BasicBlock bodyBB = new BasicBlock("allocateBody", curFunction);
            BasicBlock afterBB = new BasicBlock("allocateAfter", curFunction);
            curBB.addNextInst(new Jump(curBB, condBB));
            condBB.addNextInst(new CJump(condBB, size, CJump.CompareOp.GT, new IntImmediate(0), bodyBB, afterBB));
            curBB = bodyBB;
            if(dims.size() == 1) {
                Operand pointer = allocateArray(new LinkedList<>(), baseBytes, constructor);
                curBB.addNextInst(new Move(curBB, new Memory(addr, size, Config.getRegSize()), pointer));
            } else {
                LinkedList<Operand> remainDims = new LinkedList<>();
                for(int i = 1; i < dims.size(); i++) {
                    remainDims.add(dims.get(i));
                }
                Operand pointer = allocateArray(remainDims, baseBytes, constructor);
                curBB.addNextInst(new Move(curBB, new Memory(addr, size, Config.getRegSize()), pointer));
            }
            curBB.addNextInst(new UnaryOperation(curBB, UnaryOperation.UnaryOp.DEC, size));
            condBB.addNextInst(new Jump(curBB, condBB));
            curBB = afterBB;
            return addr;
        }
    }

    @Override
    public void visit(NewExpression node) {
        Function constructor = null;
        if(node.getNumDimension() == 0) {
            if(node.getType() instanceof ClassType) {
                ClassType classType = (ClassType) node.getType();
                if(classType.getClassEntity().getScope().getFunction(classType.getClassEntity().getName()) != null) {
                    constructor = program.getFunction(classType.getClassEntity().getName());
                }
            }
        }
        LinkedList<Operand> dims = new LinkedList<>();
        for(Expression expression : node.getDimensions()) {
            expression.accept(this);
            dims.add(exprResultMap.get(expression));
        }
        if(node.getNumDimension() > 0) {
            Operand pointer = allocateArray(dims, 0, null);
            exprResultMap.put(node, pointer);
        } else {
            int bytes;
            if(node.getType().getType() == Type.types.STRING) {
                bytes = Config.getRegSize() * 2;
            } else {
                bytes = Config.getRegSize();
            }
            Operand pointer = allocateArray(dims, bytes, constructor);
            exprResultMap.put(node, pointer);
        }

    }

    @Override
    public void visit(SuffixExpression node) {
        node.getExpr().accept(this);
        Operand operand = exprResultMap.get(node.getExpr());
        switch(node.getOp()) {
            case "++": {
                VirtualRegister oldValue = new VirtualRegister("");
                curBB.addNextInst(new Move(curBB, oldValue, operand));
                curBB.addNextInst(new UnaryOperation(curBB, UnaryOperation.UnaryOp.INC, (Address) operand));
                exprResultMap.put(node, oldValue);
                break;
            }
            case "--": {
                VirtualRegister oldValue = new VirtualRegister("");
                curBB.addNextInst(new Move(curBB, oldValue, operand));
                curBB.addNextInst(new UnaryOperation(curBB, UnaryOperation.UnaryOp.DEC, (Address) operand));
                exprResultMap.put(node, oldValue);
                break;
            }
        }
    }

    @Override
    public void visit(PrefixExpression node) {
        if(node.getOp().equals("!")) {
            trueBBMap.put(node.getExpr(), falseBBMap.get(node));
            falseBBMap.put(node.getExpr(), trueBBMap.get(node));
            node.getExpr().accept(this);
        } else {
            node.getExpr().accept(this);
            Operand operand = exprResultMap.get(node.getExpr());
            switch(node.getOp()) {
                case "++": {
                    curBB.addNextInst(new UnaryOperation(curBB, UnaryOperation.UnaryOp.INC, (Address) operand));
                    exprResultMap.put(node, operand);
                    break;
                }
                case "--": {
                    curBB.addNextInst(new UnaryOperation(curBB, UnaryOperation.UnaryOp.DEC, (Address) operand));
                    exprResultMap.put(node, operand);
                    break;
                }
                case "+": {
                    exprResultMap.put(node, operand);
                    break;
                }
                case "-": {
                    VirtualRegister vr = new VirtualRegister("");
                    curBB.addNextInst(new Move(curBB, vr, operand));
                    curBB.addNextInst(new UnaryOperation(curBB, UnaryOperation.UnaryOp.NEG, vr));
                    exprResultMap.put(node, vr);
                    break;
                }
                case "~": {
                    VirtualRegister vr = new VirtualRegister("");
                    curBB.addNextInst(new Move(curBB, vr, operand));
                    curBB.addNextInst(new UnaryOperation(curBB, UnaryOperation.UnaryOp.NEG, vr));
                    exprResultMap.put(node, vr);
                    break;
                }
            }
        }
    }

    private Operand StringOperation(Expression lhs, Expression rhs) {
        Address result = new VirtualRegister("");
        lhs.accept(this);
        Operand oldLhs = exprResultMap.get(lhs);
        rhs.accept(this);
        Operand oldRhs = exprResultMap.get(rhs);
        VirtualRegister vr;
        if(oldLhs instanceof Memory && !(oldLhs instanceof StackSlot)) {
            vr = new VirtualRegister("");
            curBB.addNextInst(new Move(curBB, vr, oldLhs));
        }
        if(oldRhs instanceof Memory && !(oldRhs instanceof StackSlot)) {
            vr = new VirtualRegister("");
            curBB.addNextInst(new Move(curBB, vr, oldRhs));
        }
        curBB.addNextInst(new Call(curBB, vrax, program.getFunction("stringAdd"), oldLhs, oldRhs));
        curBB.addNextInst(new Move(curBB, result, vrax));
        return result;
    }

    private Operand doArithmetic(Address dst, String op, Expression lhs, Expression rhs) {
        BinaryOperation.BinaryOp bop;
        lhs.accept(this);
        Operand oldLhs = exprResultMap.get(lhs);
        rhs.accept(this);
        Operand oldRhs = exprResultMap.get(rhs);
        Address result = new VirtualRegister("");
        switch(op) {
            case "*":
                bop = BinaryOperation.BinaryOp.MUL;
                curBB.addNextInst(new Move(curBB, vrax, oldLhs));
                curBB.addNextInst(new BinaryOperation(curBB, null, bop, oldRhs));
                curBB.addNextInst(new Move(curBB, result, vrax));
                break;
            case "/":
                bop = BinaryOperation.BinaryOp.DIV;
                curBB.addNextInst(new Move(curBB, vrax, oldLhs));
                curBB.addNextInst(new BinaryOperation(curBB, null, bop, oldRhs));
                curBB.addNextInst(new Move(curBB, result, vrax));
                break;
            case "%":
                bop = BinaryOperation.BinaryOp.MOD;
                curBB.addNextInst(new Move(curBB, vrax, oldLhs));
                curBB.addNextInst(new BinaryOperation(curBB, null, bop, oldRhs));
                curBB.addNextInst(new Move(curBB, result, vrdx));
                break;
            case "+":
                bop = BinaryOperation.BinaryOp.ADD;
                if(oldLhs == dst) {
                    result = dst;
                    curBB.addNextInst(new BinaryOperation(curBB, result, bop, oldRhs));
                } else {
                    curBB.addNextInst(new Move(curBB, result, oldLhs));
                    curBB.addNextInst(new BinaryOperation(curBB, result, bop, oldRhs));
                }
                break;
            case "-":
                bop = BinaryOperation.BinaryOp.SUB;
                if(oldLhs == dst) {
                    result = dst;
                    curBB.addNextInst(new BinaryOperation(curBB, result, bop, oldRhs));
                } else {
                    curBB.addNextInst(new Move(curBB, result, oldLhs));
                    curBB.addNextInst(new BinaryOperation(curBB, result, bop, oldRhs));
                }
                break;
            case ">>":
                bop = BinaryOperation.BinaryOp.SAR;
                if(oldLhs == dst) {
                    result = dst;
                    curBB.addNextInst(new Move(curBB, vrcx, oldRhs));
                    curBB.addNextInst(new BinaryOperation(curBB, result, bop, vrcx));
                } else {
                    curBB.addNextInst(new Move(curBB, result, oldLhs));
                    curBB.addNextInst(new Move(curBB, vrcx, oldRhs));
                    curBB.addNextInst(new BinaryOperation(curBB, result, bop, oldRhs));
                }
                break;
            case "<<":
                bop = BinaryOperation.BinaryOp.SAL;
                if(oldLhs == dst) {
                    result = dst;
                    curBB.addNextInst(new Move(curBB, vrcx, oldRhs));
                    curBB.addNextInst(new BinaryOperation(curBB, result, bop, vrcx));
                } else {
                    curBB.addNextInst(new Move(curBB, result, oldLhs));
                    curBB.addNextInst(new Move(curBB, vrcx, oldRhs));
                    curBB.addNextInst(new BinaryOperation(curBB, result, bop, oldRhs));
                }
                break;
            case "&":
                bop = BinaryOperation.BinaryOp.AND;
                if(oldRhs == dst) {
                    result = dst;
                    curBB.addNextInst(new BinaryOperation(curBB, result, bop, oldLhs));
                } else {
                    curBB.addNextInst(new Move(curBB, result, oldLhs));
                    curBB.addNextInst(new BinaryOperation(curBB, result, bop, oldRhs));
                }
                break;
            case "|":
                bop = BinaryOperation.BinaryOp.OR;
                if(oldRhs == dst) {
                    result = dst;
                    curBB.addNextInst(new BinaryOperation(curBB, result, bop, oldLhs));
                } else {
                    curBB.addNextInst(new Move(curBB, result, oldLhs));
                    curBB.addNextInst(new BinaryOperation(curBB, result, bop, oldRhs));
                }
                break;
            case "^":
                bop = BinaryOperation.BinaryOp.XOR;
                if(oldRhs == dst) {
                    result = dst;
                    curBB.addNextInst(new BinaryOperation(curBB, result, bop, oldLhs));
                } else {
                    curBB.addNextInst(new Move(curBB, result, oldLhs));
                    curBB.addNextInst(new BinaryOperation(curBB, result, bop, oldRhs));
                }
                break;
        }
        return result;
    }

    private void doLogical(String op, Expression lhs, Expression rhs, BasicBlock trueBB, BasicBlock falseBB) {
        BasicBlock checkSecondBB = new BasicBlock("secondConditionBB", curFunction);
        if(op.equals("&&")) {
            falseBBMap.put(lhs, falseBB);
            trueBBMap.put(lhs, checkSecondBB);
        } else {
            trueBBMap.put(lhs, trueBB);
            falseBBMap.put(lhs, checkSecondBB);
        }
        lhs.accept(this);
        curBB = checkSecondBB;
        trueBBMap.put(rhs, trueBB);
        falseBBMap.put(rhs, falseBB);
        rhs.accept(this);
    }

    private void doRelation(String op, Expression lhs, Expression rhs, BasicBlock trueBB, BasicBlock falseBB) {
        lhs.accept(this);
        Operand oldLhs = exprResultMap.get(lhs);
        rhs.accept(this);
        Operand oldRhs = exprResultMap.get(rhs);

        CJump.CompareOp cop = null;
        switch(op) {
            case ">": cop = CJump.CompareOp.GT; break;
            case "<": cop = CJump.CompareOp.LT; break;
            case ">=": cop = CJump.CompareOp.GE; break;
            case "<=": cop = CJump.CompareOp.LE; break;
            case "==": cop = CJump.CompareOp.EQ; break;
            case "!=": cop = CJump.CompareOp.NEQ; break;
        }
        if(lhs.getType().getType() == Type.types.STRING) { //  str (<|<=|>|>=|==|!=) str
            VirtualRegister scr = new VirtualRegister("");
            curBB.addNextInst(new Call(curBB, vrax, program.getFunction("library_stringCompare"), oldLhs, oldRhs));
            curBB.addNextInst(new Move(curBB, scr, vrax));
            curBB.addNextInst(new CJump(curBB, scr, cop, new IntImmediate(0), trueBB, falseBB));
        } else {
            if(oldLhs instanceof Memory && oldRhs instanceof Memory) {
                VirtualRegister vr = new VirtualRegister("");
                curBB.addNextInst(new Move(curBB, vr, oldLhs));
                oldLhs = vr;
            }
            curBB.addNextInst(new CJump(curBB, oldLhs, cop, oldRhs, trueBB, falseBB));
        }
    }

    @Override
    public void visit(BinaryExpression node) {
        switch(node.getOp()) {
            case "*": case "/": case "%": case "+": case "-":
            case ">>": case "<<": case "&": case "|": case "^":
                if(node.getOp().equals("+") && node.getType().getType() == Type.types.STRING) {
                    exprResultMap.put(node, StringOperation(node.getLhs(), node.getRhs()));
                } else {
                    exprResultMap.put(node, doArithmetic(assignMap.get(node), node.getOp(), node.getLhs(), node.getRhs()));
                }
                break;
            case "<": case ">": case "==": case ">=": case "<=": case "!=":
                doRelation(node.getOp(), node.getLhs(), node.getRhs(), trueBBMap.get(node), falseBBMap.get(node));
                break;
            case "&&": case "||":
                doLogical(node.getOp(), node.getLhs(), node.getRhs(), trueBBMap.get(node), falseBBMap.get(node));
                break;
        }
    }

    @Override
    public void visit(AssignExpression node) {
        node.getLhs().accept(this);
        Operand lvalue = exprResultMap.get(node.getLhs());
        assign(node.getRhs(), (Address) lvalue);
    }
}
