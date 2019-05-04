package BackEnd;

import IR.BasicBlock;
import IR.Function;
import IR.Instruction.Call;
import IR.Instruction.Instruction;
import IR.Operand.Register;
import IR.Operand.VirtualRegister;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

public class LivenessAnalyzer {
    private HashMap<BasicBlock, HashSet<VirtualRegister>> uses;
    private HashMap<BasicBlock, HashSet<VirtualRegister>> defs;
    private HashMap<BasicBlock, HashSet<VirtualRegister>> INs;
    private HashMap<BasicBlock, HashSet<VirtualRegister>> OUTs;

    public LivenessAnalyzer() {
        uses = new HashMap<>();
        defs = new HashMap<>();
        INs = new HashMap<>();
        OUTs = new HashMap<>();
    }

    private void process(Function function) {
        uses.clear();
        defs.clear();
        INs.clear();
        OUTs.clear();
        for(BasicBlock bb : function.getBasicBlocks()) {
            uses.put(bb, new HashSet<>());
            defs.put(bb, new HashSet<>());
            INs.put(bb, new HashSet<>());
            OUTs.put(bb, new HashSet<>());
        }
        for(BasicBlock bb : function.getBasicBlocks()) {
            HashSet<VirtualRegister> use = new HashSet<>();
            HashSet<VirtualRegister> def = new HashSet<>();
            for(Instruction inst = bb.getHead(); inst != null; inst = inst.getNext()) {
                LinkedList<Register> allUse = inst instanceof Call ? ((Call) inst).getAllUsedRegister() : inst.getUsedRegisters();
                for(Register reg : allUse){
                    VirtualRegister vr = (VirtualRegister) reg;
                    if(!def.contains(vr)) {
                        use.add(vr);
                    }
                }
                for(Register reg : inst.getDefinedRegisters()) {
                    VirtualRegister vr = (VirtualRegister) reg;
                    def.add(vr);
                }
            }
            defs.put(bb, def);
            uses.put(bb, use);
        }
        boolean changed = true;
        while(changed) {
            changed = false;
            for(BasicBlock bb : function.getReversePrevOrder()) {
                int oldSize = INs.get(bb).size();
                OUTs.get(bb).clear();
                INs.get(bb).clear();
                for(BasicBlock succ : bb.getNextBBs()) {
                    OUTs.get(bb).addAll(INs.get(succ));
                }
                INs.get(bb).addAll(OUTs.get(bb));
                INs.get(bb).removeAll(defs.get(bb));
                INs.get(bb).addAll(uses.get(bb));
                if(INs.get(bb).size() != oldSize) {
                    changed = true;
                }
            }
        }
    }

    public HashMap<BasicBlock,HashSet<VirtualRegister>> getOut(Function function) {
        process(function);
        return OUTs;
    }

}
