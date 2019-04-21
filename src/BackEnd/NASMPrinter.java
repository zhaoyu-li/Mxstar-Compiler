package BackEnd;

import IR.*;
import IR.Instruction.*;
import IR.Operand.*;
import Scope.VariableEntity;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Formatter;
import java.util.HashMap;

public class NASMPrinter implements IRVistor {
    private StringBuilder program;
    private HashMap<BasicBlock, String> bbNames;
    private HashMap<StaticData, String> sdNames;

    private int bbIndex;
    private int sdIndex;

    public NASMPrinter() {
        program = new StringBuilder();
        bbNames = new HashMap<>();
        sdNames = new HashMap<>();
        bbIndex = 0;
        sdIndex = 0;
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
            function.accept(this);
        }
        addLine("\tsection .data");
        for(StaticVariable var : node.getStaticVariables()) {
            add("\tdb ");
            for (int i = 0; i < var.getLength(); i++) {
                if (i != 0) add(", ");
                add("00H");
            }
            add("\n");
        }
        for(StaticString str : node.getStaticStrings()) {
            addLine("\tdq " + String.valueOf(str.getValue().length()));
            add("\tdb");
            for(int i = 0; i < str.getValue().length(); i++) {
                Formatter formatter = new Formatter();
                formatter.format("%02XH ", (int) str.getValue().charAt(i));
                add(formatter.toString());
            }
            addLine("00H");
        }
    }

    @Override
    public void visit(Function node) {
        add(getNASMFunctionName(node) + ":");
        for(BasicBlock bb : node.getBasicBlocks()) {
            bb.accept(this);
        }
    }

    @Override
    public void visit(BasicBlock node) {
        addLine("\t" + getBasicBlockName(node));
        for(Instruction inst = node.getHead(); inst != null; inst = inst.getNext()) {
            inst.accept(this);
        }
    }

    @Override
    public void visit(Jump node) {
        addLine("\tjump" + getBasicBlockName(node.getTargetBB()));
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
        add("\tcmp");
        node.getLhs().accept(this);
        add(", ");
        node.getRhs().accept(this);
        addLine("");
        addLine("\t" + op + " " + getBasicBlockName(node.getElseBB()));
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
            add("\n");
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
        node.getdst().accept(this);
        add("\n");
    }

    @Override
    public void visit(Move node) {
        add("\tmov ");
        node.getdst().accept(this);
        add(", ");
        node.getSrc().accept(this);
        add("\n");
    }

    @Override
    public void visit(Lea node) {
        add("\tlea ");
        node.getDst().accept(this);
        add(", ");
        node.getSrc().accept(this);
        add("\n");
    }

    private PhysicalRegister getPhysical(Operand v) {
        if(v instanceof VirtualRegister)
            return ((VirtualRegister) v).getAllocatedPhysicalRegister();
        else
            return null;
    }

    @Override
    public void visit(Load node) {

    }

    @Override
    public void visit(Store node) {

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
    public void visit(Memory node) {

    }

    @Override
    public void visit(StackSlot node) {

    }

    @Override
    public void visit(VirtualRegister node) {

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
