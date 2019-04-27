package BackEnd;

import IR.*;
import IR.Instruction.*;
import IR.Operand.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.HashMap;

public class CodeGenerator implements IRVistor {
    private StringBuilder program;
    private HashMap<BasicBlock, String> bbNames;
    private HashMap<StaticData, String> sdNames;
    private HashMap<StackSlot,String> ssNames;
    private HashMap<VirtualRegister,String> varNames;

    private BasicBlock nextBB;

    private boolean inLeaInst;
    private int bbIndex;
    private int sdIndex;
    private int varIndex;
    private int ssIndex;

    public CodeGenerator() {
        program = new StringBuilder();
        bbNames = new HashMap<>();
        sdNames = new HashMap<>();
        ssNames = new HashMap<>();
        varNames = new HashMap<>();
        bbIndex = 0;
        sdIndex = 0;
        varIndex = 0;
        ssIndex = 0;
        inLeaInst = false;
    }

    public void print(PrintStream printStream) {
        printStream.print(program.toString());
    }

    private void addLine(String line) {
        program.append(line + "\n");
    }

    private void add(String str) {
        program.append(str);
    }

    private String getNASMFunctionName(Function function) {
        switch(function.getType()) {
            case Library:
                return "__" + function.getName();
            case External:
                return function.getName();
            case UserDefined:
                return "_" + function.getName();
            default:
                return null;
        }
    }

    private String getBasicBlockName(BasicBlock bb) {
        if(!bbNames.containsKey(bb)) {
            bbNames.put(bb, "_block_" + String.valueOf(bbIndex++));
        }
        return bbNames.get(bb);
    }

    private String getStaticDataName(StaticData sd) {
        if(!sdNames.containsKey(sd)) {
            sdNames.put(sd, "_global_" + String.valueOf(sdIndex++));
        }
        return sdNames.get(sd);
    }

    private String getVirtualRegsiterName(VirtualRegister virtualRegister) {
        if(!varNames.containsKey(virtualRegister))
            varNames.put(virtualRegister, "v" + String.valueOf(varIndex++));
        return varNames.get(virtualRegister);
    }
    private String getStackSlotName(StackSlot ss) {
        if(!ssNames.containsKey(ss))
            ssNames.put(ss, "stack[" + String.valueOf(ssIndex++) + "]");
        return ssNames.get(ss);
    }

    @Override
    public void visit(IRProgram node) {
        try {
            BufferedReader br = new BufferedReader(new FileReader("lib/c2nasm/lib.asm"));
            String line;
            while((line = br.readLine()) != null) add(line + "\n");
            addLine("\tsection .text");
        } catch (IOException e) {
            e.printStackTrace();
        }
        for(Function function : node.getFunctions().values()) {
            if(function.getType() == Function.FuncType.UserDefined)
                function.accept(this);
        }
        addLine("\tsection .data");
        for(StaticVariable var : node.getStaticVariables()) {
            addLine(getStaticDataName(var) + ":");
            add("\tdb ");
            for (int i = 0; i < var.getLength(); i++) {
                if (i != 0) add(", ");
                add("00H");
            }
            add("\n");
        }
        for(StaticString str : node.getStaticStrings()) {
            addLine(getStaticDataName(str) + ":");
            addLine("\tdq " + String.valueOf(str.getValue().length()));
            add("\tdb ");
            for(int i = 0; i < str.getValue().length(); i++) {
                Formatter formatter = new Formatter();
                formatter.format("%02XH, ", (int) str.getValue().charAt(i));
                add(formatter.toString());
            }
            addLine("00H");
        }
    }

    @Override
    public void visit(Function node) {
        addLine(getNASMFunctionName(node) + ":");
        ArrayList<BasicBlock> reversePostOrder = new ArrayList<>(node.getReversePostOrder());
        for(int i = 0; i < reversePostOrder.size(); i++) {
            BasicBlock bb = reversePostOrder.get(i);
            nextBB = (i + 1 == reversePostOrder.size()) ? null : reversePostOrder.get(i + 1);
            bb.accept(this);
        }
    }

    @Override
    public void visit(BasicBlock node) {
        addLine("\t" + getBasicBlockName(node) + ":");
        for(Instruction inst = node.getHead(); inst != null; inst = inst.getNext()) {
            inst.accept(this);
        }
    }

    @Override
    public void visit(Jump node) {
        if(node.getTargetBB() != nextBB)
            addLine("\tjmp " + getBasicBlockName(node.getTargetBB()));
    }

    @Override
    public void visit(CJump node) {
        String op = null;
        switch(node.getOp()) {
            case EQ:
                op = "je";
                break;
            case LT:
                op = "jl";
                break;
            case GT:
                op = "jg";
                break;
            case LE:
                op = "jle";
                break;
            case GE:
                op = "jge";
                break;
            case NE:
                op = "jne";
                break;
        }
        add("\tcmp ");
        node.getLhs().accept(this);
        add(", ");
        node.getRhs().accept(this);
        addLine("");
        addLine("\t" + op + " " + getBasicBlockName(node.getThenBB()));
        if(node.getElseBB() != nextBB)
            addLine("\tjmp " + getBasicBlockName(node.getElseBB()));
    }

    @Override
    public void visit(Return node) {
        addLine("\tret");
    }

    @Override
    public void visit(BinaryOperation node) {
        if(node.getOp() == BinaryOperation.BinaryOp.MUL) {
            add("\timul ");
            node.getSrc().accept(this);
            add("\n");
            return;
        }
        if(node.getOp() == BinaryOperation.BinaryOp.DIV || node.getOp() == BinaryOperation.BinaryOp.MOD) {
            add("\tidiv ");
            node.getSrc().accept(this);
            add("\n");
            return;
        }
        String op = null;
        switch(node.getOp()) {
            case OR:
                op = "or";
                break;
            case ADD:
                op = "add";
                break;
            case AND:
                op = "and";
                break;
            case SAL:
                op = "sal";
                break;
            case SAR:
                op = "sar";
                break;
            case SUB:
                op = "sub";
                break;
            case XOR:
                op = "xor";
                break;
        }
        if(node.getOp() == BinaryOperation.BinaryOp.SAL || node.getOp() == BinaryOperation.BinaryOp.SAR) {
            add("\t" + op + " ");
            node.getDst().accept(this);
            add(", cl\n");
            return;
        }
        add("\t" + op + " ");
        node.getDst().accept(this);
        add(", ");
        node.getSrc().accept(this);
        add("\n");
    }

    @Override
    public void visit(UnaryOperation node) {
        String op = null;
        switch(node.getOp()) {
            case DEC:
                op = "dec";
                break;
            case INC:
                op = "inc";
                break;
            case NEG:
                op = "neg";
                break;
            case NOT:
                op = "not";
                break;
        }
        add("\t" + op + " ");
        node.getDst().accept(this);
        add("\n");
    }

    @Override
    public void visit(Move node) {
        if(node.getSrc() == node.getDst())
            return;
        add("\tmov ");
        node.getDst().accept(this);
        add(", ");
        node.getSrc().accept(this);
        add("\n");
    }

    @Override
    public void visit(Lea node) {
        inLeaInst = true;
        add("\tlea ");
        node.getDst().accept(this);
        add(", ");
        node.getSrc().accept(this);
        add("\n");
        inLeaInst = false;
    }

    @Override
    public void visit(Call node) {
        add("\tcall " + getNASMFunctionName(node.getFunc()));
        add("\n");
    }

    @Override
    public void visit(Push node) {
        add("\tpush ");
        node.getSrc().accept(this);
        add("\n");
    }

    @Override
    public void visit(Pop node) {
        add("\tpop ");
        node.getDst().accept(this);
        add("\n");
    }

    @Override
    public void visit(Leave node) {
        addLine("\tleave");
    }

    @Override
    public void visit(Cdq node) {
        addLine("\tcdq");
    }

    @Override
    public void visit(Memory node) {
        boolean occur = false;
        if(!inLeaInst)
            add("qword ");
        add("[");
        if(node.getBase() != null) {
            node.getBase().accept(this);
            occur = true;
        }
        if(node.getIndex() != null) {
            if(occur)
                add(" + ");
            node.getIndex().accept(this);
            if(node.getScale() != 1)
                add(" * " + String.valueOf(node.getScale()));
            occur = true;
        }
        if(node.getOffset() != null) {
            Constant constant = node.getOffset();
            if(constant instanceof StaticData) {
                if(occur)
                    add(" + ");
                constant.accept(this);
            } else if(constant instanceof IntImmediate) {
                int value = ((IntImmediate) constant).getValue();
                if(occur) {
                    if(value > 0)
                        add(" + " + String.valueOf(value));
                    else if(value < 0)
                        add(" - " + String.valueOf(-value));
                } else {
                    add(String.valueOf(value));
                }
            }
        }
        add("]");
    }

    @Override
    public void visit(StackSlot node) {
        if(node.getBase() != null || node.getIndex() != null || node.getOffset() != null) {
            visit((Memory) node);
        } else {
            add(getStackSlotName(node));
        }
    }

    @Override
    public void visit(VirtualRegister node) {
        if(node.getAllocatedPhysicalRegister() != null) {
            visit(node.getAllocatedPhysicalRegister());
            varNames.put(node, node.getAllocatedPhysicalRegister().getName());
        } else {

            add(getVirtualRegsiterName(node));
        }
    }

    @Override
    public void visit(PhysicalRegister node) {
        add(node.getName());
    }

    @Override
    public void visit(IntImmediate node) {
        add(String.valueOf(node.getValue()));
    }

    @Override
    public void visit(StaticVariable node) {
        add(getStaticDataName(node));
    }

    @Override
    public void visit(StaticString node) {
        add(getStaticDataName(node));
    }
}
