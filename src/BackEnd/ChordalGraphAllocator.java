package BackEnd;

import IR.BasicBlock;
import IR.Function;
import IR.IRProgram;
import IR.Instruction.Instruction;
import IR.Operand.PhysicalRegister;
import IR.Operand.Register;
import IR.Operand.VirtualRegister;
import IR.RegisterSet;

import java.util.*;

public class ChordalGraphAllocator {
    private IRProgram program;
    private LivenessAnalyzer livenessAnalyzer;
    private HashMap<VirtualRegister, HashSet<VirtualRegister>> interferenceGraph;
    private LinkedList<PhysicalRegister> physicalRegisters;


    public ChordalGraphAllocator(IRProgram program) {
        this.program = program;
        this.livenessAnalyzer = new LivenessAnalyzer();
        this.interferenceGraph = new HashMap<>();
        this.physicalRegisters = new LinkedList<>();
        for(PhysicalRegister pr : RegisterSet.allRegs) {
            if(!pr.getName().equals("rsp") && !pr.getName().equals("rbp")) {
                physicalRegisters.add(pr);
            }
        }
    }

    private void addRegsiter(VirtualRegister vr) {
        if(!interferenceGraph.containsKey(vr)) {
            interferenceGraph.put(vr, new HashSet<>());
        }
    }

    private void delRegister(VirtualRegister vr) {
        if(interferenceGraph.containsKey(vr)) {
            for(VirtualRegister adj : interferenceGraph.get(vr)) {
                delEdge(vr, adj);
            }
            interferenceGraph.remove(vr);
        }
    }

    private void addEdge(VirtualRegister a, VirtualRegister b) {
        if(!a.equals(b)) {
            interferenceGraph.get(a).add(b);
            interferenceGraph.get(b).add(a);
        }
    }

    private void delEdge(VirtualRegister a, VirtualRegister b) {
        if(interferenceGraph.containsKey(a) && interferenceGraph.get(a).contains(b)) {
            interferenceGraph.get(a).remove(b);
            interferenceGraph.get(b).remove(a);
        }
    }

    private Collection<VirtualRegister> getAllVertix() {
        return interferenceGraph.keySet();
    }

    private void getInterferenceGraph(Function function) {
        HashMap<BasicBlock, HashSet<VirtualRegister>> OUTs = livenessAnalyzer.getOUTs(function);
        interferenceGraph.clear();
        for(BasicBlock bb : function.getBasicBlocks()) {
            for(Instruction inst = bb.getHead(); inst != null; inst = inst.getNext()) {
                for(Register reg : inst.getUsedRegisters()) {
                    VirtualRegister vr = (VirtualRegister) reg;
                    addRegsiter(vr);
                }
                for(Register reg : inst.getDefinedRegisters()) {
                    VirtualRegister vr = (VirtualRegister) reg;
                    addRegsiter(vr);
                }
            }
        }

        for(BasicBlock bb : function.getBasicBlocks()) {
            HashSet<VirtualRegister> liveNow = new HashSet<>(OUTs.get(bb));
            for(Instruction inst = bb.getTail(); inst != null; inst = inst.getPrev()) {
                for(Register reg : inst.getDefinedRegisters()) {
                    VirtualRegister vr1 = (VirtualRegister) reg;
                    for(VirtualRegister vr2 : liveNow) {
                        addEdge(vr1, vr2);
                    }
                }
                for(Register reg : inst.getDefinedRegisters()) {
                    VirtualRegister vr = (VirtualRegister) reg;
                    liveNow.remove(vr);
                }
                for(Register reg : inst.getUsedRegisters()) {
                    VirtualRegister vr = (VirtualRegister) reg;
                    liveNow.add(vr);
                }
            }
        }
    }
    private void process(Function function) {
        getInterferenceGraph(function);
        maximumCardinalitySearch();

    }

    private void maximumCardinalitySearch() {
        Set<VirtualRegister> W = new HashSet<>();
        HashMap<VirtualRegister, Integer> wt = new HashMap<>();
        Collection<VirtualRegister> V = getAllVertix();
        for(VirtualRegister vr : V) {
            wt.put(vr, 0);
        }
        W = new HashSet<>(V);
        VirtualRegister mvr = V.iterator().next();
        int maximalWeight = 0;
        for(VirtualRegister vr : V) {
            VirtualRegister v = mvr;
            vr = v;
            for(VirtualRegister u : W) {
                Integer old = wt.get(u);
                wt.put(u, old + 1);
            }
            W.remove(v);
        }
    }

    public void run() {
        for(Function function : program.getFunctions().values()) {
            if(function.getType() != Function.FuncType.UserDefined) {
                process(function);
            }
        }
    }

}
