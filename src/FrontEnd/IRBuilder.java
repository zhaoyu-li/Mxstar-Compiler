package FrontEnd;

import AST.*;
import IR.Instruction.*;
import IR.Function.FuncType;
import IR.Operand.*;
import Scope.*;
import IR.*;
import Type.*;
import Utility.Config;

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
        isInParameters = false;
    }

    public IRProgram getProgram() {
        return program;
    }

    private void registerVariable(VariableDeclaration variableDeclaration) {
        StaticVariable var = new StaticVariable(variableDeclaration.getName(), Config.getRegSize());
        VirtualRegister vr = new VirtualRegister(variableDeclaration.getName());
        vr.setSpillPlace(new Memory(var));
        program.addStaticVariable(var);
        variableDeclaration.getVariableEntity().setVirtualRegister(vr);
    }

    private void registerFunction(FunctionDeclaration functionDeclaration) {
        Function function = new Function(FuncType.UserDefined, functionDeclaration.getFunctionEntity().getName());
        program.addFunction(function);
    }

    private void buildInitFunction(Program node) {
        curFunction = program.getFunction("globalInit");
        curFunction.setHeadBB(new BasicBlock("headBB", curFunction));
        curBB = curFunction.getHeadBB();
        for(VariableDeclaration variableDeclaration : node.getVariables()) {
            if(variableDeclaration.getInit() != null) {
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
    public void visit(FunctionDeclaration node) {
        curFunction = program.getFunction(node.getFunctionEntity().getName());
        curFunction.setHeadBB(new BasicBlock("headBB", curFunction));
        curBB = curFunction.getHeadBB();
        if(curClassName != null) { // in the class
            //VirtualRegister thisPointer = new VirtualRegister("this");
            //curFunction.addParameter(thisPointer);
            //curThis = thisPointer;
            curFunction.addParameter(curThis);
        }
        isInParameters = true;
        for(VariableDeclaration variableDeclaration : node.getParameters()) {
            visit(variableDeclaration);
        }
        isInParameters = false;
        for(int i = 0; i < curFunction.getParameters().size(); i++) {
            if(i < 6) {
                curBB.addNextInst(new Store(curBB, vargs.get(i), curFunction.getParameters().get(i)));
            } else {
                curBB.addNextInst(new Store(curBB, curFunction.getParameters().get(i).getSpillPlace(), curFunction.getParameters().get(i)));
            }
        }
        for(VariableEntity var : node.getFunctionEntity().getGlobalVariables()) {
            curBB.addNextInst(new Load(curBB, var.getVirtualRegister(), var.getVirtualRegister().getSpillPlace()));
        }
        for(Statement statement : node.getBody()) {
            statement.accept(this);
        }
        if(!(curBB.getTail() instanceof Return)) {
            if(node.getFunctionEntity().getReturnType().isVoidType()) {
                Return ret = new Return(curBB);
                curBB.addNextInst(ret);
                curFunction.addReturn(ret);
            } else {
                curBB.addNextInst(new Move(curBB, vrax, new IntImmediate(0)));
                Return ret = new Return(curBB);
                curBB.addNextInst(ret);
                curFunction.addReturn(ret);
            }
        }
        BasicBlock tailBB = new BasicBlock("tailBB", curFunction);
        for(Return ret : curFunction.getReturnList()) {
            ret.prepend(new Jump(ret.getBB(), tailBB));
            ret.remove();
        }
        tailBB.addNextInst(new Return(tailBB));
        curFunction.setTailBB(tailBB);

        Instruction retInst = curFunction.getTailBB().getTail();
        for(VariableEntity variableEntity : node.getFunctionEntity().getGlobalVariables()) {
            retInst.prepend(new Store(tailBB, variableEntity.getVirtualRegister().getSpillPlace(), variableEntity.getVirtualRegister()));
        }
        curFunction.addUsedRecursiveVariables(curFunction);
    }

    @Override
    public void visit(ClassDeclaration node) {
        curClassName = node.getName();
        curThis = new VirtualRegister("this");
        if(node.getConstructor() != null) {
            visit(node.getConstructor());
        }
        for(FunctionDeclaration functionDeclaration : node.getMethods()) {
            visit(functionDeclaration);
        }
        curClassName = null;
        curThis = null;
    }

    private void assign(Expression expr, Address vr) {
        if(expr.getType().isBoolType()) {
            boolAssign(expr, vr);
        } else {
            expr.setAddr(vr);
            expr.accept(this);
            Operand result = expr.getResult();
            if(result != vr) {
                curBB.addNextInst(new Store(curBB, vr, result));
            }
        }
    }

    private void boolAssign(Expression expr, Address vr) {
        BasicBlock trueBB = new BasicBlock("trueBB", curFunction);
        BasicBlock falseBB = new BasicBlock("falseBB", curFunction);
        BasicBlock mergeBB = new BasicBlock("mergeBB", curFunction);
        expr.setTrueBB(trueBB);
        expr.setFalseBB(falseBB);
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
                vr.setSpillPlace(new StackSlot(vr.getName(), curFunction));
            }
            curFunction.addParameter(vr);
        } else {
            vr.setSpillPlace(new StackSlot(vr.getName(), curFunction));
            curFunction.addTemporary((StackSlot) vr.getSpillPlace());
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
    public void visit(IfStatement node) {
        BasicBlock thenBB = new BasicBlock("ifThenBB", curFunction);
        BasicBlock afterBB = new BasicBlock("ifAfterBB", curFunction);
        BasicBlock elseBB = node.getElseStatement() == null ? afterBB : new BasicBlock("ifElseBB", curFunction);
        node.getCondition().setTrueBB(thenBB);
        node.getCondition().setFalseBB(elseBB);
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
        BasicBlock condBB = new BasicBlock("whileConditionBB", curFunction);
        BasicBlock bodyBB = new BasicBlock("whileBodyBB", curFunction);
        BasicBlock afterBB = new BasicBlock("whileAfterBB", curFunction);
        curBB.addNextInst(new Jump(curBB, condBB));
        loopConditionBB.push(condBB);
        loopAfterBB.push(afterBB);
        curBB = condBB;
        node.getCondition().setTrueBB(bodyBB);
        node.getCondition().setFalseBB(afterBB);
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
        BasicBlock bodyBB = new BasicBlock("forBodyBB", curFunction);
        BasicBlock afterBB = new BasicBlock("forAfterBB", curFunction);
        BasicBlock condBB = node.getCondition() == null ? bodyBB : new BasicBlock("forConditionBB", curFunction);
        BasicBlock updateBB = node.getUpdate() == null ? condBB : new BasicBlock("forUpdateBB", curFunction);
        curBB.addNextInst(new Jump(curBB, condBB));
        loopConditionBB.push(condBB);
        loopAfterBB.push(afterBB);
        if(node.getCondition() != null) {
            node.getCondition().setTrueBB(bodyBB);
            node.getCondition().setFalseBB(afterBB);
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
            curBB.addNextInst(new Store(curBB, vrax, node.getRet().getResult()));
        }
        Return ret = new Return(curBB);
        curBB.addNextInst(ret);
        curFunction.addReturn(ret);
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
    public void visit(ThisExpression node) {
        node.setResult(curThis);
    }

    @Override
    public void visit(NullLiteral node) {
        Operand operand = new IntImmediate(0);
        node.setResult(operand);
    }

    @Override
    public void visit(BoolLiteral node) {
        curBB.addNextInst(new Jump(curBB, node.getValue() ? node.getTrueBB() : node.getFalseBB()));
        node.setResult(node.getValue() ? new IntImmediate(1) : new IntImmediate(0));
    }

    @Override
    public void visit(IntLiteral node) {
        Operand operand = new IntImmediate(node.getValue());
        node.setResult(operand);
    }

    @Override
    public void visit(StringLiteral node) {
        StaticString str = new StaticString(node.getValue());
        program.addStaticString(str);
        node.setResult(str);
    }

    @Override
    public void visit(Identifier node) {
        Operand operand;
        if(node.getVariableEntity().isInClass()) {
            int offset = globalScope.getClassEntity(curClassName).getScope().getVariableOffset(node.getName());
            operand = new Memory(curThis, new IntImmediate(offset));
        } else {
            operand = node.getVariableEntity().getVirtualRegister();
            if(node.getVariableEntity().isGlobal()) {
                curFunction.addGlobalVariable(node.getVariableEntity());
            }
        }
        if(node.getTrueBB() != null) {
            curBB.addNextInst(new CJump(curBB, operand, CJump.CompareOp.EQ, new IntImmediate(1), node.getTrueBB(), node.getFalseBB()));
        } else {
            node.setResult(operand);
        }
    }

    @Override
    public void visit(MemberExpression node) {
        VirtualRegister baseAddr = new VirtualRegister("");
        node.getExpr().accept(this);
        curBB.addNextInst(new Store(curBB, baseAddr, node.getExpr().getResult()));
        if(node.getExpr().getType() instanceof ArrayType) {
            node.setResult(new Memory(baseAddr));
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
                    Operand argument = expression.getResult();
                    arguments.add(argument);
                }
                curBB.addNextInst(new Call(curBB, vrax, function, arguments));
                if (node.getFuncCall().getFunctionEntity().getReturnType().getType() != Type.types.VOID) {
                    VirtualRegister ret = new VirtualRegister("");
                    curBB.addNextInst(new Store(curBB, ret, vrax));
                    operand = ret;
                } else {
                    operand = null;
                }
            }
            if(node.getTrueBB() != null) {
                curBB.addNextInst(new CJump(curBB, operand, CJump.CompareOp.EQ, new IntImmediate(1), node.getTrueBB(), node.getFalseBB()));
            } else {
                node.setResult(operand);
            }
        }
    }

    @Override
    public void visit(ArrayExpression node) {
        node.getArr().accept(this);
        Operand baseAddr = node.getArr().getResult();
        node.getIdx().accept(this);
        Operand index = node.getIdx().getResult();
        VirtualRegister base;
        if(baseAddr instanceof Register) {
            base = (VirtualRegister) baseAddr;
        } else {
            base = new VirtualRegister("");
            curBB.addNextInst(new Store(curBB, base, baseAddr));
        }
        Memory memory = new Memory();
        if(index instanceof IntImmediate) {
            memory = new Memory(base, new IntImmediate(((IntImmediate) index).getValue() * 4 + 4));
        } else if(index instanceof Register) {
            memory = new Memory(base, (Register) index, 4, new IntImmediate(4));
        } else if(index instanceof Memory) {
            VirtualRegister vr = new VirtualRegister("");
            curBB.addNextInst(new Store(curBB, vr, index));
            memory = new Memory(base, vr, 4, new IntImmediate(4));
        }
        if(node.getTrueBB() != null) {
            curBB.addNextInst(new CJump(curBB, memory, CJump.CompareOp.NE, new IntImmediate(0), node.getTrueBB(), node.getFalseBB()));
        } else {
            node.setResult(memory);
        }
    }

    @Override
    public void visit(FuncCallExpression node) {
        LinkedList<Operand> arguments = new LinkedList<>();
//        if(node.getFunctionEntity().getScope() != globalScope) {
//            arguments.add(curThis);
//        }
        if(curClassName != null) {
            arguments.add(curThis);
        }
        for(Expression expression : node.getArguments()) {
            expression.accept(this);
            arguments.add(expression.getResult());
        }
        curBB.addNextInst(new Call(curBB, vrax, program.getFunction(node.getFunctionEntity().getName()), arguments));
        if(node.getTrueBB() != null) {
            curBB.addNextInst(new CJump(curBB, vrax, CJump.CompareOp.EQ, new IntImmediate(1), node.getTrueBB(), node.getFalseBB()));
        } else if(!node.getFunctionEntity().getReturnType().isVoidType()) {
            VirtualRegister ret = new VirtualRegister("");
            curBB.addNextInst(new Store(curBB, ret, vrax));
            node.setResult(ret);
        }
    }

    private Operand allocateArray(LinkedList<Operand> dims, int baseBytes, Function constructor) {
        if(dims.size() == 0) {
            if(baseBytes == 0) {
                return new IntImmediate(0);
            } else {
                VirtualRegister retAddr = new VirtualRegister("");
                curBB.addNextInst(new Call(curBB, vrax, program.getFunction("malloc"), new IntImmediate(baseBytes)));
                curBB.addNextInst(new Store(curBB, retAddr, vrax));
                if(constructor != null) {
                    curBB.addNextInst(new Call(curBB, vrax, constructor, retAddr));
                } else {
                    if(baseBytes == 4) {
                        curBB.addNextInst(new Store(curBB, new Memory(retAddr), new IntImmediate(0)));
                    } else if(baseBytes == 8) {
                        curBB.addNextInst(new BinaryOperation(curBB, retAddr, BinaryOperation.BinaryOp.ADD, new IntImmediate(4)));
                        curBB.addNextInst(new Store(curBB, new Memory(retAddr), new IntImmediate(0)));
                        curBB.addNextInst(new BinaryOperation(curBB, retAddr, BinaryOperation.BinaryOp.SUB, new IntImmediate(4)));
                    }
                }
                return retAddr;
            }
        } else {
            VirtualRegister addr = new VirtualRegister("");
            VirtualRegister size = new VirtualRegister("");
            VirtualRegister bytes = new VirtualRegister("");
            curBB.addNextInst(new Store(curBB, size, dims.get(0)));
            curBB.addNextInst(new Lea(curBB, bytes, new Memory(size, Config.getRegSize(), new IntImmediate(Config.getRegSize()))));
            curBB.addNextInst(new Call(curBB, vrax, program.getFunction("malloc"), bytes));
            curBB.addNextInst(new Store(curBB, addr, vrax));
            curBB.addNextInst(new Store(curBB, new Memory(addr), size));
            BasicBlock condBB = new BasicBlock("allocateConditionBB", curFunction);
            BasicBlock bodyBB = new BasicBlock("allocateBodyBB", curFunction);
            BasicBlock afterBB = new BasicBlock("allocateAfterBB", curFunction);
            curBB.addNextInst(new Jump(curBB, condBB));
            condBB.addNextInst(new CJump(condBB, size, CJump.CompareOp.GT, new IntImmediate(0), bodyBB, afterBB));
            curBB = bodyBB;
            if(dims.size() == 1) {
                Operand pointer = allocateArray(new LinkedList<>(), baseBytes, constructor);
                curBB.addNextInst(new Store(curBB, new Memory(addr, size, Config.getRegSize()), pointer));
            } else {
                LinkedList<Operand> remainDims = new LinkedList<>();
                for(int i = 1; i < dims.size(); i++) {
                    remainDims.add(dims.get(i));
                }
                Operand pointer = allocateArray(remainDims, baseBytes, constructor);
                curBB.addNextInst(new Store(curBB, new Memory(addr, size, Config.getRegSize()), pointer));
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
            dims.add(expression.getResult());
        }
        if(node.getNumDimension() > 0) {
            Operand pointer = allocateArray(dims, 0, null);
            node.setResult(pointer);
        } else {
            int bytes;
            if(node.getType().getType() == Type.types.STRING) {
                bytes = Config.getRegSize() * 2;
            } else {
                bytes = Config.getRegSize();
            }
            Operand pointer = allocateArray(dims, bytes, constructor);
            node.setResult(pointer);
        }

    }

    @Override
    public void visit(SuffixExpression node) {
        node.getExpr().accept(this);
        Operand value = node.getExpr().getResult();
        switch(node.getOp()) {
            case "++": {
                VirtualRegister oldValue = new VirtualRegister("");
                curBB.addNextInst(new Store(curBB, oldValue, value));
                curBB.addNextInst(new UnaryOperation(curBB, UnaryOperation.UnaryOp.INC, (Address) value));
                node.setResult(oldValue);
                break;
            }
            case "--": {
                VirtualRegister oldValue = new VirtualRegister("");
                curBB.addNextInst(new Store(curBB, oldValue, value));
                curBB.addNextInst(new UnaryOperation(curBB, UnaryOperation.UnaryOp.DEC, (Address) value));
                node.setResult(oldValue);
                break;
            }
        }
    }

    @Override
    public void visit(PrefixExpression node) {
        if(node.getOp().equals("!")) {
            node.getExpr().setTrueBB(node.getFalseBB());
            node.getExpr().setFalseBB(node.getTrueBB());
            node.getExpr().accept(this);
        } else {
            node.getExpr().accept(this);
            Operand value = node.getExpr().getResult();
            switch(node.getOp()) {
                case "++": {
                    curBB.addNextInst(new UnaryOperation(curBB, UnaryOperation.UnaryOp.INC, (Address) value));
                    node.setResult(value);
                    break;
                }
                case "--": {
                    curBB.addNextInst(new UnaryOperation(curBB, UnaryOperation.UnaryOp.DEC, (Address) value));
                    node.setResult(value);
                    break;
                }
                case "+": {
                    node.setResult(value);
                    break;
                }
                case "-": {
                    VirtualRegister oldValue = new VirtualRegister("");
                    curBB.addNextInst(new Store(curBB, oldValue, value));
                    curBB.addNextInst(new UnaryOperation(curBB, UnaryOperation.UnaryOp.NEG, oldValue));
                    node.setResult(oldValue);
                    break;
                }
                case "~": {
                    VirtualRegister oldValue = new VirtualRegister("");
                    curBB.addNextInst(new Store(curBB, oldValue, value));
                    curBB.addNextInst(new UnaryOperation(curBB, UnaryOperation.UnaryOp.NOT, oldValue));
                    node.setResult(oldValue);
                    break;
                }
            }
        }
    }

    private Operand StringConcat(Expression lhs, Expression rhs) {
        Address result = new VirtualRegister("");
        lhs.accept(this);
        Operand str1 = lhs.getResult();
        rhs.accept(this);
        Operand str2 = rhs.getResult();
        /*VirtualRegister vr;
        if(src1 instanceof Memory && !(src1 instanceof StackSlot)) {
            vr = new VirtualRegister("");
            curBB.addNextInst(new Store(curBB, vr, src1));
        }
        if(src2 instanceof Memory && !(src2 instanceof StackSlot)) {
            vr = new VirtualRegister("");
            curBB.addNextInst(new Store(curBB, vr, src2));
        }*/
        curBB.addNextInst(new Call(curBB, vrax, program.getFunction("string.concat"), str1, str2));
        curBB.addNextInst(new Store(curBB, result, vrax));
        return result;
    }

    private Operand ArithmeticOperation(Address dst, String op, Expression lhs, Expression rhs) {
        lhs.accept(this);
        Operand src1 = lhs.getResult();
        rhs.accept(this);
        Operand src2 = rhs.getResult();
        Address result = new VirtualRegister("");
        BinaryOperation.BinaryOp bop;
        switch(op) {
            case "*":
                bop = BinaryOperation.BinaryOp.MUL;
                curBB.addNextInst(new Store(curBB, vrax, src1));
                if(src2 instanceof Constant) {
                    VirtualRegister vr = new VirtualRegister("");
                    curBB.addNextInst(new Move(curBB, vr, src2));
                    curBB.addNextInst(new BinaryOperation(curBB, null, bop, vr));
                } else {
                    curBB.addNextInst(new BinaryOperation(curBB, null, bop, src2));
                }
                curBB.addNextInst(new Store(curBB, result, vrax));
                break;
            case "/":
                bop = BinaryOperation.BinaryOp.DIV;
                curBB.addNextInst(new Store(curBB, vrax, src1));
                if(src2 instanceof Constant) {
                    VirtualRegister vr = new VirtualRegister("");
                    curBB.addNextInst(new Move(curBB, vr, src2));
                    curBB.addNextInst(new BinaryOperation(curBB, null, bop, vr));
                } else {
                    curBB.addNextInst(new BinaryOperation(curBB, null, bop, src2));
                }
                curBB.addNextInst(new Store(curBB, result, vrax));
                break;
            case "%":
                bop = BinaryOperation.BinaryOp.MOD;
                curBB.addNextInst(new Store(curBB, vrax, src1));
                if(src2 instanceof Constant) {
                    VirtualRegister vr = new VirtualRegister("");
                    curBB.addNextInst(new Move(curBB, vr, src2));
                    curBB.addNextInst(new BinaryOperation(curBB, null, bop, vr));
                } else {
                    curBB.addNextInst(new BinaryOperation(curBB, null, bop, src2));
                }
                curBB.addNextInst(new Store(curBB, result, vrax));
                break;
            case "+":
                bop = BinaryOperation.BinaryOp.ADD;
                if(src1 == dst) {
                    result = dst;
                    curBB.addNextInst(new BinaryOperation(curBB, result, bop, src2));
                } else {
                    curBB.addNextInst(new Store(curBB, result, src1));
                    curBB.addNextInst(new BinaryOperation(curBB, result, bop, src2));
                }
                break;
            case "-":
                bop = BinaryOperation.BinaryOp.SUB;
                if(src1 == dst) {
                    result = dst;
                    curBB.addNextInst(new BinaryOperation(curBB, result, bop, src2));
                } else {
                    curBB.addNextInst(new Store(curBB, result, src1));
                    curBB.addNextInst(new BinaryOperation(curBB, result, bop, src2));
                }
                break;
            case ">>":
                bop = BinaryOperation.BinaryOp.SAR;
                if(src1 == dst) {
                    result = dst;
                    curBB.addNextInst(new Store(curBB, vrcx, src2));
                    curBB.addNextInst(new BinaryOperation(curBB, result, bop, vrcx));
                } else {
                    curBB.addNextInst(new Store(curBB, result, src1));
                    curBB.addNextInst(new Store(curBB, vrcx, src2));
                    curBB.addNextInst(new BinaryOperation(curBB, result, bop, src2));
                }
                break;
            case "<<":
                bop = BinaryOperation.BinaryOp.SAL;
                if(src1 == dst) {
                    result = dst;
                    curBB.addNextInst(new Store(curBB, vrcx, src2));
                    curBB.addNextInst(new BinaryOperation(curBB, result, bop, vrcx));
                } else {
                    curBB.addNextInst(new Store(curBB, result, src1));
                    curBB.addNextInst(new Store(curBB, vrcx, src2));
                    curBB.addNextInst(new BinaryOperation(curBB, result, bop, src2));
                }
                break;
            case "&":
                bop = BinaryOperation.BinaryOp.AND;
                if(src2 == dst) {
                    result = dst;
                    curBB.addNextInst(new BinaryOperation(curBB, result, bop, src1));
                } else {
                    curBB.addNextInst(new Store(curBB, result, src1));
                    curBB.addNextInst(new BinaryOperation(curBB, result, bop, src2));
                }
                break;
            case "|":
                bop = BinaryOperation.BinaryOp.OR;
                if(src2 == dst) {
                    result = dst;
                    curBB.addNextInst(new BinaryOperation(curBB, result, bop, src1));
                } else {
                    curBB.addNextInst(new Store(curBB, result, src1));
                    curBB.addNextInst(new BinaryOperation(curBB, result, bop, src2));
                }
                break;
            case "^":
                bop = BinaryOperation.BinaryOp.XOR;
                if(src2 == dst) {
                    result = dst;
                    curBB.addNextInst(new BinaryOperation(curBB, result, bop, src1));
                } else {
                    curBB.addNextInst(new Store(curBB, result, src1));
                    curBB.addNextInst(new BinaryOperation(curBB, result, bop, src2));
                }
                break;
            default:
                break;
        }
        return result;
    }

    private void LogicalOperation(String op, Expression lhs, Expression rhs, BasicBlock trueBB, BasicBlock falseBB) {
        BasicBlock checkSecondBB = new BasicBlock("secondConditionBB", curFunction);
        switch(op) {
            case "&&":
                lhs.setTrueBB(checkSecondBB);
                lhs.setFalseBB(falseBB);
                break;
            case "||":
                lhs.setTrueBB(trueBB);
                lhs.setFalseBB(checkSecondBB);
                break;
        }
        lhs.accept(this);
        curBB = checkSecondBB;
        rhs.setTrueBB(trueBB);
        rhs.setFalseBB(falseBB);
        rhs.accept(this);
    }

    private BasicBlock doCompare(String op, Operand lhs, Operand rhs, BasicBlock TrueBB, BasicBlock FalseBB) {
        int lvalue = ((IntImmediate) lhs).getValue();
        int rvalue = ((IntImmediate) rhs).getValue();
        switch(op) {
            case "==":
                if(lvalue == rvalue) return TrueBB;
                else return FalseBB;
            case "!=":
                if(lvalue != rvalue) return TrueBB;
                else return FalseBB;
            case "<":
                if(lvalue < rvalue) return TrueBB;
                else return FalseBB;
            case ">":
                if(lvalue > rvalue) return TrueBB;
                else return FalseBB;
            case "<=":
                if(lvalue <= rvalue) return TrueBB;
                else return FalseBB;
            case ">=":
                if(lvalue >= rvalue) return TrueBB;
                else return FalseBB;
        }
        return null;
    }

    private void RelationOperation(String op, Expression lhs, Expression rhs, BasicBlock trueBB, BasicBlock falseBB) {
        lhs.accept(this);
        Operand src1 = lhs.getResult();
        rhs.accept(this);
        Operand src2 = rhs.getResult();
        CJump.CompareOp cop;
        switch(op) {
            case ">":
                cop = CJump.CompareOp.GT;
                break;
            case "<":
                cop = CJump.CompareOp.LT;
                break;
            case ">=":
                cop = CJump.CompareOp.GE;
                break;
            case "<=":
                cop = CJump.CompareOp.LE;
                break;
            case "==":
                cop = CJump.CompareOp.EQ;
                break;
            case "!=":
                cop = CJump.CompareOp.NE;
                break;
            default:
                cop = null;
                break;
        }
        if(lhs.getType().isStringType()) {
            VirtualRegister src = new VirtualRegister("");
            curBB.addNextInst(new Call(curBB, vrax, program.getFunction("string.compare"), src1, src2));
            curBB.addNextInst(new Store(curBB, src, vrax));
            curBB.addNextInst(new CJump(curBB, src, cop, new IntImmediate(0), trueBB, falseBB));
        } else {
            if(src1 instanceof Memory && src2 instanceof Memory) {
                VirtualRegister vr = new VirtualRegister("");
                curBB.addNextInst(new Store(curBB, vr, src1));
                curBB.addNextInst(new CJump(curBB, vr, cop, src2, trueBB, falseBB));
            } else if(src1 instanceof Memory && src2 instanceof Constant){
                VirtualRegister vr = new VirtualRegister("");
                curBB.addNextInst(new Store(curBB, vr, src1));
                curBB.addNextInst(new CJump(curBB, vr, cop, src2, trueBB, falseBB));
            } else if(src1 instanceof Constant && src2 instanceof Memory) {
                VirtualRegister vr = new VirtualRegister("");
                curBB.addNextInst(new Store(curBB, vr, src2));
                curBB.addNextInst(new CJump(curBB, src1, cop, vr, trueBB, falseBB));
            } else {
                curBB.addNextInst(new Jump(curBB, doCompare(op, src1, src2, trueBB, falseBB)));
            }
        }
    }

    @Override
    public void visit(BinaryExpression node) {
        switch(node.getOp()) {
            case "*":
            case "/":
            case "%":
            case "+":
            case "-":
            case "<<":
            case ">>":
            case "&":
            case "^":
            case "|":
                if(node.getOp().equals("+") && node.getType().isStringType()) {
                    node.setResult(StringConcat(node.getLhs(), node.getRhs()));
                } else {
                    node.setResult(ArithmeticOperation(node.getAddr(), node.getOp(), node.getLhs(), node.getRhs()));
                }
                break;
            case "<":
            case ">":
            case ">=":
            case "<=":
            case "==":
            case "!=":
                RelationOperation(node.getOp(), node.getLhs(), node.getRhs(), node.getTrueBB(), node.getFalseBB());
                break;
            case "&&":
            case "||":
                LogicalOperation(node.getOp(), node.getLhs(), node.getRhs(), node.getTrueBB(), node.getFalseBB());
                break;
        }
    }

    @Override
    public void visit(AssignExpression node) {
        node.getLhs().accept(this);
        Operand lhs = node.getLhs().getResult();
        assign(node.getRhs(), (Address) lhs);
    }
}
