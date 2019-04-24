package BackEnd;

import IR.BasicBlock;
import IR.Function;
import IR.IRProgram;
import IR.Instruction.Call;
import IR.Instruction.Instruction;
import IR.Instruction.Move;
import IR.Operand.*;

import static IR.RegisterSet.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

public class SimpleAlocator {
    private IRProgram program;
    private LinkedList<PhysicalRegister> physicalRegisters;

    public SimpleAlocator(IRProgram program) {
        this.program = program;
        this.physicalRegisters = new LinkedList<>();
        this.physicalRegisters.add(rbx);
        this.physicalRegisters.add(r10);
        this.physicalRegisters.add(r11);
        this.physicalRegisters.add(r12);
        this.physicalRegisters.add(r13);
        this.physicalRegisters.add(r14);
        this.physicalRegisters.add(r15);
    }

    public void allocateRegisters() {
        for(Function function : program.getFunctions().values()) {
            allocateRegisters(function);
        }
    }
    private PhysicalRegister getPhysicalRegister(Operand vr) {
        if(vr instanceof VirtualRegister) {
            return ((VirtualRegister) vr).getAllocatedPhysicalRegister();
        } else return null;
    }

    private void allocateRegisters(Function function) {
        for(BasicBlock bb : function.getBasicBlocks()) {
            for(Instruction inst = bb.getHead(); inst != null; inst = inst.getNext()) {
                if(inst instanceof Call) continue;
                HashMap<Register, Register> renameMap = new HashMap<>();
                HashSet<Register> allRegs = new HashSet<>();
                HashSet<Register> usedRegs = new HashSet<>(inst.getUsedRegisters());
                HashSet<Register> definedRegs = new HashSet<>(inst.getDefinedRegisters());
                allRegs.addAll(usedRegs);
                allRegs.addAll(definedRegs);

                for(Register avr : allRegs) {
                    assert avr instanceof VirtualRegister;
                    VirtualRegister vr = (VirtualRegister) avr;
                    if(vr.getAllocatedPhysicalRegister() != null) continue;
                    if(vr.getSpillSpace() == null)
                        vr.setSpillSpace(new StackSlot());
                }

                if(inst instanceof Move) {
                    Move move = (Move) inst;
                    Address dest = move.getDst();
                    Operand src = move.getSrc();
                    PhysicalRegister pdest = getPhysicalRegister(dest);
                    PhysicalRegister psrc = getPhysicalRegister(src);
                    if(pdest != null && psrc != null) {
                        move.setDst(pdest);
                        move.setSrc(psrc);
                        continue;
                    } else if(pdest != null) {
                        move.setDst(pdest);
                        if(move.getSrc() instanceof VirtualRegister) {
                            move.setSrc(((VirtualRegister) move.getSrc()).getSpillSpace());
                        } else if(move.getSrc() instanceof Constant) {

                        } else {
                            assert false;
                        }
                        continue;
                    } else if(psrc != null) {
                        move.setSrc(psrc);
                        if(move.getSrc() instanceof VirtualRegister) {
                            move.setSrc(((VirtualRegister) move.getDst()).getSpillSpace());
                        } else {
                            assert false;
                        }
                        continue;
                    }
                }

                int cnt = 0;
                for (Register reg : allRegs) {
                    if (!renameMap.containsKey(reg)) {
                        PhysicalRegister pr = ((VirtualRegister)reg).getAllocatedPhysicalRegister();
                        if(pr == null)
                            renameMap.put(reg, physicalRegisters.get(cnt++));
                        else {
                            renameMap.put(reg, pr);
                        }
                    }
                }
                inst.renameUsedRegisters(renameMap);
                inst.renameDefinedRegisters(renameMap);

                for (Register reg : usedRegs) {
                    if(((VirtualRegister)reg).getAllocatedPhysicalRegister() == null)
                        inst.prepend(new Move(bb, renameMap.get(reg),((VirtualRegister) reg).getSpillSpace()));
                }
                for (Register reg : definedRegs) {
                    if(((VirtualRegister)reg).getAllocatedPhysicalRegister() == null) {
                        inst.append(new Move(bb, ((VirtualRegister) reg).getSpillSpace(), renameMap.get(reg)));
                        inst = inst.getNext();
                    }
                }

            }
        }
    }
}
