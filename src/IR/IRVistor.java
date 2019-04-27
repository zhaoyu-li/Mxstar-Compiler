package IR;

import IR.Instruction.*;
import IR.Operand.*;

public interface IRVistor {
    void visit(IRProgram node);
    void visit(Function node);
    void visit(BasicBlock node);

    void visit(Jump node);
    void visit(CJump node);
    void visit(Return node);

    void visit(BinaryOperation node);
    void visit(UnaryOperation node);
    void visit(Move node);
    void visit(Lea node);
    void visit(Call node);
    void visit(Push node);
    void visit(Pop node);
    void visit(Leave node);

    void visit(Memory node);
    void visit(StackSlot node);
    void visit(VirtualRegister node);
    void visit(PhysicalRegister node);
    void visit(IntImmediate node);
    void visit(StaticVariable node);
    void visit(StaticString node);

}
