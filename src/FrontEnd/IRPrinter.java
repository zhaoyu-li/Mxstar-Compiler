package FrontEnd;

import IR.*;
import IR.Instruction.*;
import IR.Operand.*;

import java.util.Formatter;
import java.util.HashMap;

public class IRPrinter implements IRVistor {
    private StringBuilder program;

    public IRPrinter() {
        program = new StringBuilder();
    }

    public void print() {
        System.out.print(program.toString());
    }

    private void addLine(String line) {
        program.append(line + "\n");
    }

    private void add(String str) {
        program.append(str);
    }



    @Override
    public void visit(IRProgram node) {
        for(Function function : node.getFunctions().values()) {
            if(function.getType() == Function.FuncType.UserDefined)
                function.accept(this);
        }
//        for(StaticVariable var : node.getStaticVariables()) {
//
//        }
//        for(StaticString str : node.getStaticStrings()) {
//
//        }
    }

    @Override
    public void visit(Function node) {
        for(BasicBlock bb : node.getBasicBlocks()) {
            bb.accept(this);
        }
    }

    @Override
    public void visit(BasicBlock node) {
        for(Instruction inst = node.getHead(); inst != null; inst = inst.getNext()) {
            inst.accept(this);
        }
    }

    @Override
    public void visit(Jump node) {
        addLine("jump");
    }

    @Override
    public void visit(CJump node) {
        addLine("cjump");
    }

    @Override
    public void visit(Return node) {
        addLine("ret");
    }

    @Override
    public void visit(BinaryOperation node) {
        addLine("bop");
    }

    @Override
    public void visit(UnaryOperation node) {
        addLine("uop");
    }

    @Override
    public void visit(Move node) {
        addLine("Move");
    }

    @Override
    public void visit(Lea node) {
        addLine("lea");
    }

    @Override
    public void visit(Call node) {
        addLine("call");
    }

    @Override
    public void visit(Push node) {
        addLine("push");
    }

    @Override
    public void visit(Pop node) {
        addLine("pop");
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

    }

    @Override
    public void visit(IntImmediate node) {

    }

    @Override
    public void visit(StaticVariable node) {

    }

    @Override
    public void visit(StaticString node) {

    }
}
