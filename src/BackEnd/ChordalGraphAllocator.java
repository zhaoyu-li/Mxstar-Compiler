package BackEnd;

import IR.BasicBlock;
import IR.Function;
import IR.IRProgram;
import IR.Instruction.Call;
import IR.Instruction.Instruction;
import IR.Instruction.Move;
import IR.Operand.PhysicalRegister;
import IR.Operand.Register;
import IR.Operand.StackSlot;
import IR.Operand.VirtualRegister;
import IR.RegisterSet;

import java.util.*;

public class ChordalGraphAllocator {
    private IRProgram program;
    private LivenessAnalyzer livenessAnalyzer;
    private HashMap<VirtualRegister, HashSet<VirtualRegister>> interferenceGraph;
    private LinkedList<PhysicalRegister> physicalRegisters;
    private HashMap<VirtualRegister, PhysicalRegister> colors;
    private LinkedList<VirtualRegister> spillList;


    public ChordalGraphAllocator(IRProgram program) {
        this.program = program;
        this.livenessAnalyzer = new LivenessAnalyzer();
        this.interferenceGraph = new HashMap<>();
        this.physicalRegisters = new LinkedList<>();
        this.colors = new HashMap<>();
        this.spillList = new LinkedList<>();
        for(PhysicalRegister pr : RegisterSet.allRegs) {
            if(!pr.getName().equals("rsp") && !pr.getName().equals("rbp")) {
                physicalRegisters.add(pr);
            }
        }
    }

    private void addRegister(VirtualRegister vr) {
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

    private Collection<VirtualRegister> getAllVertex() {
        return interferenceGraph.keySet();
    }

    private void getInterferenceGraph(Function function) {
        HashMap<BasicBlock, HashSet<VirtualRegister>> OUTs = livenessAnalyzer.getOUTs(function, true);
        interferenceGraph.clear();
        for(BasicBlock bb : function.getBasicBlocks()) {
            for(Instruction inst = bb.getHead(); inst != null; inst = inst.getNext()) {
                for(Register reg : inst.getUsedRegisters()) {
                    VirtualRegister vr = (VirtualRegister) reg;
                    addRegister(vr);
                }
                for(Register reg : inst.getDefinedRegisters()) {
                    VirtualRegister vr = (VirtualRegister) reg;
                    addRegister(vr);
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

    private ArrayList<VirtualRegister> maximumCardinalitySearch() {
        HashMap<VirtualRegister, Integer> weight = new HashMap<>();
        ArrayList<VirtualRegister> V = new ArrayList<>(getAllVertex());
        ArrayList<VirtualRegister> orderedVirtualRegisters = new ArrayList<>();
        for(VirtualRegister vr : V) {
            weight.put(vr, 0);
        }
        HashSet<VirtualRegister> W = new HashSet<>(V);
        int maximalWeight;
        VirtualRegister v = V.iterator().next();
        for(int i = 0; i < V.size(); i++) {
            maximalWeight = -1;
            for(VirtualRegister vr : W) {
                if(weight.get(vr) > maximalWeight) {
                    maximalWeight = weight.get(vr);
                    v = vr;
                }
            }
            orderedVirtualRegisters.add(i, v);
            for(VirtualRegister u : W) {
                if(interferenceGraph.get(v).contains(u)) {
                    Integer newValue = weight.get(u) + 1;
                    weight.put(u, newValue);
                }
            }
            W.remove(v);
        }
        return orderedVirtualRegisters;
    }

    private void greedyColor(ArrayList<VirtualRegister> vertices) {
        spillList.clear();
        colors.clear();
        for(VirtualRegister vr : vertices) {
            if(vr.getAllocatedPhysicalRegister() != null) {
                colors.put(vr, vr.getAllocatedPhysicalRegister());
            }
        }
        for(VirtualRegister vr : vertices) {
            if(vr.getAllocatedPhysicalRegister() == null) {
                LinkedList<PhysicalRegister> canBeUsedColors = new LinkedList<>(physicalRegisters);
                for(VirtualRegister neighbor : interferenceGraph.get(vr)) {
                    if(colors.containsKey(neighbor)) {
                        canBeUsedColors.remove(colors.get(neighbor));
                    }
                }
                if(canBeUsedColors.isEmpty()) {
                    spillList.add(vr);
                } else {
                    PhysicalRegister pr = null;
                    for(PhysicalRegister reg : RegisterSet.callerSave) {
                        if(canBeUsedColors.contains(reg)) {
                            pr = reg;
                            break;
                        }
                    }
                    if(pr == null) {
                        pr = canBeUsedColors.getFirst();
                    }
//                    System.out.println("assign color : vr = " + vr.getName() + " pr = " + pr.getName());
                    colors.put(vr, pr);
                }
            }
        }
    }

    private void spillRegisters(Function function) {
        for(VirtualRegister vr : spillList) {
            if(vr.getSpillSpace() == null) {
                vr.setSpillSpace(new StackSlot());
            }
        }
        for(BasicBlock bb : function.getBasicBlocks()) {
            for(Instruction inst = bb.getHead(); inst != null; inst = inst.getNext()) {
                LinkedList<VirtualRegister> use = new LinkedList<>();
                LinkedList<VirtualRegister> def = new LinkedList<>();
                HashMap<Register, Register> renameMap = new HashMap<>();
                for(Register reg : inst.getUsedRegisters()) {
                    VirtualRegister vr = (VirtualRegister) reg;
                    if(spillList.contains(vr)) {
                        renameMap.put(vr, new VirtualRegister(""));
                        use.add(vr);
                    }
                }
                for(Register reg : inst.getDefinedRegisters()) {
                    VirtualRegister vr = (VirtualRegister) reg;
                    if(spillList.contains(vr)) {
                        renameMap.put(vr, new VirtualRegister(""));
                        def.add(vr);
                    }
                }
                inst.renameUsedRegisters(renameMap);
                inst.renameDefinedRegisters(renameMap);
                for(VirtualRegister vr : use) {
                    inst.prepend(new Move(inst.getBB(), renameMap.get(vr), vr.getSpillSpace()));
                }
                for(VirtualRegister vr : def) {
                    inst.append(new Move(inst.getBB(), vr.getSpillSpace(), renameMap.get(vr)));
                    inst = inst.getNext();
                }
            }
        }
    }

    private void allocateRegisters(Function function) {
        HashMap<Register, Register> renameMap = new HashMap<>(colors);
        for(BasicBlock bb : function.getBasicBlocks()) {
            for(Instruction inst = bb.getHead(); inst != null; inst = inst.getNext()) {
                inst.renameUsedRegisters(renameMap);
                inst.renameDefinedRegisters(renameMap);
            }
        }
    }

    private void process(Function function) {
        while(true){
            getInterferenceGraph(function);
            ArrayList<VirtualRegister> vertices = maximumCardinalitySearch();
            greedyColor(vertices);
            if(spillList.isEmpty()) {
                allocateRegisters(function);
                break;
            } else {
                spillRegisters(function);
            }
        }
        function.calcUsedPhysicalRegisters();
    }

    public void run() {
        for(Function function : program.getFunctions().values()) {
            if(function.getType() == Function.FuncType.UserDefined) {
                process(function);
            }
        }
    }

}
