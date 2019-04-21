package AST;

import IR.BasicBlock;
import IR.Operand.Address;
import IR.Operand.Operand;
import Type.Type;

public abstract class Expression extends Node {
    protected Type type;
    private boolean isMutable;
    private Operand result;
    private Address addr;
    private BasicBlock trueBB;
    private BasicBlock falseBB;

    public Expression() {
        type = null;
        isMutable = true;
        result = null;
        addr= null;
        trueBB = null;
        falseBB = null;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    public void setMutable(boolean mutable) {
        isMutable = mutable;
    }

    public boolean isMutable() {
        return isMutable;
    }

    public void setResult(Operand result) {
        this.result = result;
    }

    public Operand getResult() {
        return result;
    }

    public void setAddr(Address addr) {
        this.addr = addr;
    }

    public Address getAddr() {
        return addr;
    }

    public void setTrueBB(BasicBlock trueBB) {
        this.trueBB = trueBB;
    }

    public BasicBlock getTrueBB() {
        return trueBB;
    }

    public void setFalseBB(BasicBlock falseBB) {
        this.falseBB = falseBB;
    }

    public BasicBlock getFalseBB() {
        return falseBB;
    }
}
