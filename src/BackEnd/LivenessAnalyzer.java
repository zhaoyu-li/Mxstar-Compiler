package BackEnd;

import IR.BasicBlock;
import IR.Function;
import IR.IRProgram;
import IR.Instruction.Call;
import IR.Instruction.Instruction;
import IR.Operand.Register;
import IR.Operand.VirtualRegister;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

public class LivenessAnalyzer {
    private IRProgram program;
    private HashMap<BasicBlock, HashSet<VirtualRegister>> liveOut;
    private HashMap<BasicBlock, HashSet<VirtualRegister>> usedRegisters;
    private HashMap<BasicBlock, HashSet<VirtualRegister>> definedRegisters;

    public LivenessAnalyzer(IRProgram program) {
        this.program = program;
        this.liveOut = new HashMap<>();
        this.usedRegisters = new HashMap<>();
        this.definedRegisters = new HashMap<>();
    }

    public void run() {
        for(Function function : program.getFunctions().values()) {
            process(function);
        }
    }

    private LinkedList<VirtualRegister> trans(Collection<Register> registers) {
        LinkedList<VirtualRegister> virtualRegisters = new LinkedList<>();
        for(Register reg : registers) {
            virtualRegisters.add((VirtualRegister) reg);
        }
        return virtualRegisters;
    }

    private void process(Function function) {
        liveOut = new HashMap<>();
        usedRegisters = new HashMap<>();
        definedRegisters = new HashMap<>();
        for(BasicBlock bb : function.getBasicBlocks()) {
            liveOut.put(bb, new HashSet<>());
            usedRegisters.put(bb, new HashSet<>());
            definedRegisters.put(bb, new HashSet<>());
        }
        for(BasicBlock bb : function.getBasicBlocks()) {
            HashSet<VirtualRegister> bbUsedRegisters = new HashSet<>();
            HashSet<VirtualRegister> bbDefinedRegisters = new HashSet<>();
            for(Instruction inst = bb.getHead(); inst != null; inst = inst.getNext()) {
                LinkedList<Register> usedRegs;
                if(inst instanceof Call)
                    usedRegs = ((Call) inst).getCallUsed();
                else
                    usedRegs = inst.getUsedRegisters();
                for(VirtualRegister reg : trans(usedRegs))
                    if(!bbDefinedRegisters.contains(reg))
                        bbUsedRegisters.add(reg);
                bbDefinedRegisters.addAll(trans(inst.getDefinedRegisters()));
            }

            definedRegisters.put(bb, bbDefinedRegisters);
            usedRegisters.put(bb, bbUsedRegisters);
        }
        boolean changed = true;
        while(changed) {
            changed = false;
            LinkedList<BasicBlock> basicBlocks = function.getReversePrevOrder();
            for(BasicBlock bb : basicBlocks) {
                int oldSize = liveOut.get(bb).size();
                for(BasicBlock succ : bb.getNextBBs()) {
                    HashSet<VirtualRegister> regs = new HashSet<>(liveOut.get(succ));
                    regs.removeAll(definedRegisters.get(succ));
                    regs.addAll(usedRegisters.get(succ));
                    liveOut.get(bb).addAll(regs);
                }
                changed = changed || liveOut.get(bb).size() != oldSize;
            }
        }
    }
    public HashMap<BasicBlock,HashSet<VirtualRegister>> getLiveOut(Function function) {
        process(function);
        return liveOut;
    }

}
