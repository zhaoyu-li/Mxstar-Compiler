package BackEnd;

import IR.BasicBlock;
import IR.Function;
import IR.IRProgram;
import IR.Instruction.Instruction;
import IR.Instruction.Move;
import IR.Operand.PhysicalRegister;
import IR.Operand.Register;
import IR.Operand.StackSlot;
import IR.Operand.VirtualRegister;
import IR.RegisterSet;

import java.util.*;

public class SimplifiedGraphAllocator {
    private IRProgram program;
    private LivenessAnalyzer livenessAnalyzer;
    private HashSet<PhysicalRegister> physicalRegisters;
    private HashMap<VirtualRegister, PhysicalRegister> colors;
    private int K;

    private HashMap<VirtualRegister, HashSet<VirtualRegister>> adjSet;
    private HashMap<VirtualRegister, Integer> degrees;
    private HashMap<VirtualRegister, HashSet<VirtualRegister>> adjList;
    private HashMap<VirtualRegister,HashSet<Move>> moveList;
    private HashSet<VirtualRegister> simplifyWorkList;
    private HashSet<Move> workListMoves;
    private HashSet<VirtualRegister> freezeWorkList;
    private HashSet<VirtualRegister> spillWorkList;
    private HashSet<VirtualRegister> selectStack;
    private HashSet<VirtualRegister> coalescedNodes;
    private HashSet<Move> activeMoves;
    private HashMap<VirtualRegister, VirtualRegister> alias;
    private HashSet<Move> coalescedMoves;
    private HashSet<Move> constrainMoves;
    private HashSet<Move> frozenMoves;
    private HashSet<VirtualRegister> spillNodes;
    private HashSet<VirtualRegister> coloredNodes;

    public SimplifiedGraphAllocator(IRProgram program) {
        this.program = program;
        this.livenessAnalyzer = new LivenessAnalyzer();
        this.physicalRegisters = new HashSet<>();
        for(PhysicalRegister pr : RegisterSet.allRegs) {
            if(!pr.getName().equals("rsp") && !pr.getName().equals("rbp")) {
                physicalRegisters.add(pr);
            }
        }
        this.K = physicalRegisters.size();
        init();
    }

    private void init() {
        this.colors = new HashMap<>();

        this.adjSet = new HashMap<>();
        this.adjList = new HashMap<>();
        this.degrees = new HashMap<>();
        this.moveList = new HashMap<>();
        this.simplifyWorkList = new HashSet<>();
        this.workListMoves = new HashSet<>();
        this.freezeWorkList = new HashSet<>();
        this.spillWorkList = new HashSet<>();

        this.selectStack = new HashSet<>();
        this.coalescedNodes = new HashSet<>();
        this.activeMoves = new HashSet<>();
        this.alias = new HashMap<>();
        this.coalescedMoves = new HashSet<>();
        this.constrainMoves = new HashSet<>();
        this.frozenMoves = new HashSet<>();
        this.spillNodes = new HashSet<>();
        this.coloredNodes = new HashSet<>();
    }

    private void addRegister(VirtualRegister vr) {
        if(!adjList.containsKey(vr)) {
            System.err.println("add reg " + vr.getName());
            adjSet.put(vr, new HashSet<>());
            adjList.put(vr, new HashSet<>());
            degrees.put(vr, 0);
            moveList.put(vr, new HashSet<>());
            alias.put(vr, vr);
        }
    }

    private void addEdge(VirtualRegister a, VirtualRegister b) {
        if (!a.equals(b) && !adjSet.get(a).contains(b)) {
            System.err.println("add edge " + a.getName() + " " + b.getName());
            adjSet.get(a).add(b);
            adjSet.get(b).add(a);
            if (a.getAllocatedPhysicalRegister() == null) {
                adjList.get(a).add(b);
                int oldDegree = degrees.get(a);
                degrees.put(a, oldDegree + 1);
            }
            if (b.getAllocatedPhysicalRegister() == null) {
                adjList.get(b).add(a);
                int oldDegree = degrees.get(b);
                degrees.put(b, oldDegree + 1);
            }
        }
    }

    private HashSet<VirtualRegister> adjacent(VirtualRegister n) {
        HashSet<VirtualRegister> adjacent = new HashSet<>(adjList.get(n));
        adjacent.removeAll(selectStack);
        adjacent.removeAll(coalescedNodes);
        return adjacent;
    }

    private HashSet<Move> nodeMoves(VirtualRegister n) {
        HashSet<Move> nodeMoves = new HashSet<>(activeMoves);
        nodeMoves.addAll(workListMoves);
        nodeMoves.retainAll(moveList.get(n));
        return nodeMoves;
    }

    private boolean moveRelated(VirtualRegister n) {
        return !nodeMoves(n).isEmpty();
    }

    private int degree(VirtualRegister n) {
        return degrees.get(n);
    }

    private void decrementDegree(VirtualRegister m) {
        int d = degree(m);
        degrees.put(m, d - 1);
        if(d == K) {
            HashSet<VirtualRegister> enableMove = new HashSet<>(adjacent(m));
            enableMove.add(m);
            enableMoves(enableMove);
            spillWorkList.remove(m);
            if(moveRelated(m)) {
                freezeWorkList.add(m);
            } else {
                simplifyWorkList.add(m);
            }
        }
    }

    private void enableMoves(HashSet<VirtualRegister> nodes) {
        for(VirtualRegister n : nodes) {
            for(Move m : nodeMoves(n)) {
                if(activeMoves.contains(m)) {
                    activeMoves.remove(m);
                    workListMoves.add(m);
                }
            }
        }
    }

    private void enableMoves(VirtualRegister vr) {
        for(Move m : nodeMoves(vr)) {
            if(activeMoves.contains(m)) {
                activeMoves.remove(m);
                workListMoves.add(m);
            }
        }
    }

    private void addWorkList(VirtualRegister u) {
        if(u.getAllocatedPhysicalRegister() == null && !moveRelated(u) && degree(u) < K) {
            freezeWorkList.remove(u);
            simplifyWorkList.add(u);
        }
    }

    private boolean ok(VirtualRegister t, VirtualRegister r) {
        return degree(t) < K || t.getAllocatedPhysicalRegister() != null || adjSet.get(t).contains(r);
    }

    private boolean conservative(HashSet<VirtualRegister> nodes) {
        int k = 0;
        for(VirtualRegister n : nodes) {
            if(degrees.get(n) >= K) k++;
        }
        return k <= K;
    }

    private void coalesce() {
        System.err.println("================================ coalesce ====================================");
        Move m = workListMoves.iterator().next();
        VirtualRegister y = getAlias((VirtualRegister) m.getDst());
        VirtualRegister x = getAlias((VirtualRegister) m.getSrc());
        VirtualRegister u;
        VirtualRegister v;
        if(y.getAllocatedPhysicalRegister() != null) {
            u = y;
            v = x;
        } else {
            u = x;
            v = y;
        }
        workListMoves.remove(m);
        if(u == v) {
            coalescedMoves.add(m);
            addWorkList(u);
        } else if(v.getAllocatedPhysicalRegister() != null || adjSet.get(u).contains(v)) {
            constrainMoves.add(m);
            addWorkList(u);
            addWorkList(v);
        } else if(u.getAllocatedPhysicalRegister() != null && judgeOk(u, v)
                || u.getAllocatedPhysicalRegister() == null && conservative(union(adjacent(u), adjacent(v)))) {
            coalescedMoves.add(m);
            combine(u, v);
            addWorkList(u);
        } else {
            activeMoves.add(m);
        }
    }

    private boolean judgeOk(VirtualRegister u, VirtualRegister v) {
        for(VirtualRegister t : adjacent(v)) {
            if(!ok(t, u)) return false;
        }
        return true;
    }

    private HashSet<VirtualRegister> union(HashSet<VirtualRegister> a, HashSet<VirtualRegister> b) {
        HashSet<VirtualRegister> union = new HashSet<>(a);
        union.addAll(b);
        return union; 
    }

    private void combine(VirtualRegister u, VirtualRegister v) {
        if(freezeWorkList.contains(v)) {
            freezeWorkList.remove(v);
        } else {
            spillWorkList.remove(v);
        }
        coalescedNodes.add(v);
        alias.put(v, u);
        moveList.get(u).addAll(moveList.get(v));
        enableMoves(v);
        for(VirtualRegister t : adjacent(v)) {
            addEdge(t, u);
            decrementDegree(t);
        }
        if(degree(u) >= K && freezeWorkList.contains(u)) {
            freezeWorkList.remove(u);
            spillWorkList.add(u);
        }
    }

    private VirtualRegister getAlias(VirtualRegister n) {
        if(coalescedNodes.contains(n)) return getAlias(alias.get(n));
        else return n;
    }

    private void simplify() {
        System.err.println("================================ simplify ====================================");
        VirtualRegister n = simplifyWorkList.iterator().next();
        simplifyWorkList.remove(n);
        selectStack.add(n);
        for(VirtualRegister m : adjacent(n)) {
            decrementDegree(m);
        }
    }

    private void freeze() {
        System.err.println("================================ freeze ====================================");
        VirtualRegister u = freezeWorkList.iterator().next();
        freezeWorkList.remove(u);
        simplifyWorkList.add(u);
        freezeMoves(u);
    }

    private void freezeMoves(VirtualRegister u) {
        for(Move m : nodeMoves(u)) {
            VirtualRegister v;
            if(getAlias((VirtualRegister) m.getDst()) == getAlias(u)) {
                v = getAlias((VirtualRegister) m.getSrc());
            } else {
                v = getAlias((VirtualRegister) m.getDst());
            }
            activeMoves.remove(m);
            frozenMoves.add(m);
            if(freezeWorkList.contains(v) && nodeMoves(v).isEmpty()) {
                freezeWorkList.remove(v);
                simplifyWorkList.add(v);
            }
        }
    }

    private void selectSpill() {
        System.err.println("================================ selectSpill ====================================");
        VirtualRegister m = null;
        int rank = -1;
        for(VirtualRegister vr : spillWorkList) {
            int curRank = degree(vr);
            if(vr.getAllocatedPhysicalRegister() != null)    //  spill the precolored vr at last
                curRank = 0;
            if(curRank > rank) {
                m = vr;
                rank = curRank;
            }
        }
        spillWorkList.remove(m);
        simplifyWorkList.add(m);
        freezeMoves(m);
    }
    
    private void assignColors() {
        for(VirtualRegister vr : selectStack) {
            if(vr.getAllocatedPhysicalRegister() != null) {
                colors.put(vr, vr.getAllocatedPhysicalRegister());
                coloredNodes.add(vr);
            }
        }
        while(!selectStack.isEmpty()) {
            VirtualRegister n = selectStack.iterator().next();
            selectStack.remove(n);
            if(n.getAllocatedPhysicalRegister() != null) continue;
            HashSet<PhysicalRegister> okColors = new HashSet<>(physicalRegisters);
            for(VirtualRegister w : adjList.get(n)) {
                if(coloredNodes.contains(getAlias(w)) || getAlias(w).getAllocatedPhysicalRegister() != null) {
                    okColors.remove(colors.get(getAlias(w)));
                }
            }
            if(okColors.isEmpty()) {
                spillNodes.add(n);
            } else {
                coloredNodes.add(n);
                PhysicalRegister c = null;
                for(PhysicalRegister reg : RegisterSet.callerSave) {
                    if(okColors.contains(reg)) {
                        c = reg;
                        break;
                    }
                }
                if(c == null) c = okColors.iterator().next();
                colors.put(n, c);
                System.err.println("assign color : vr = " + n.getName() + " pr = " + c.getName());
            }
        }
        for(VirtualRegister n : coalescedNodes) {
            colors.put(n, colors.get(getAlias(n)));
            System.err.println("assign color : vr = " + n.getName() + " pr = " + colors.get(getAlias(n)).getName());
        }
    }

    private Collection<VirtualRegister> getAllVertex() {
        assert adjSet.keySet().size() == adjList.keySet().size();
        return adjSet.keySet();
    }

    private HashSet<VirtualRegister> trans(LinkedList<Register> registers) {
        HashSet<VirtualRegister> virtualRegisters = new HashSet<>();
        for(Register reg : registers) {
            VirtualRegister vr = (VirtualRegister) reg;
            virtualRegisters.add(vr);
        }
        return virtualRegisters;
    }

    private boolean isMoveInstruction(Instruction inst) {
        return inst instanceof Move && ((Move) inst).getDst() instanceof VirtualRegister && ((Move) inst).getSrc() instanceof VirtualRegister;
    }

    private void build(Function function) {
        System.err.println("================================ build ====================================");

        HashMap<BasicBlock, HashSet<VirtualRegister>> OUTs = livenessAnalyzer.getOUTs(function, true);
        for(BasicBlock bb : function.getBasicBlocks()) {
            for(Instruction inst = bb.getHead(); inst != null; inst = inst.getNext()) {
                for(VirtualRegister vr : trans(inst.getUsedRegisters())) {
                    addRegister(vr);
                }
                for(VirtualRegister vr : trans(inst.getDefinedRegisters())) {
                    addRegister(vr);
                }
            }
        }

        for(BasicBlock bb : function.getBasicBlocks()) {
            HashSet<VirtualRegister> live = new HashSet<>(OUTs.get(bb));
            for(Instruction inst = bb.getTail(); inst != null; inst = inst.getPrev()) {
                if(isMoveInstruction(inst)) {
                    live.removeAll(trans(inst.getUsedRegisters()));
                    HashSet<VirtualRegister> registers = new HashSet<>(trans(inst.getDefinedRegisters()));
                    registers.addAll(trans(inst.getUsedRegisters()));
                    for(VirtualRegister n : registers) {
                        if(!moveList.containsKey(n)) moveList.put(n, new HashSet<>());
                        moveList.get(n).add((Move) inst);
                    }
                    workListMoves.add((Move) inst);
                }
                live.addAll(trans(inst.getDefinedRegisters()));
                for(VirtualRegister d : trans(inst.getDefinedRegisters())) {
                    for(VirtualRegister l : live) {
                        addEdge(l, d);
                    }
                }
                live.addAll(trans(inst.getUsedRegisters()));
                live.removeAll(trans(inst.getDefinedRegisters()));
            }
        }
    }

    private void makeWorkList() {
        System.err.println("================================ makeWorkList ====================================");
        HashSet<VirtualRegister> initial = new HashSet<>(getAllVertex());
        System.err.println("initial.size() = " + initial.size());
        for(VirtualRegister vr : initial) {
            if(degree(vr) >= K) {
                System.err.println("add spillWorkList " + vr.getName());
                spillWorkList.add(vr);
            } else if(moveRelated(vr)) {
                System.err.println("add freezeWorkList " + vr.getName());
                freezeWorkList.add(vr);
            } else {
                System.err.println("add simplifyWorkList " + vr.getName());
                simplifyWorkList.add(vr);
            }
        }
    }

    private void rewriteFunction(Function function) {
        for(VirtualRegister vr : spillWorkList) {
            if(vr.getSpillSpace() == null) {
                vr.setSpillSpace(new StackSlot());
            }
        }
        for(BasicBlock bb : function.getBasicBlocks()) {
            for(Instruction inst = bb.getHead(); inst != null; inst = inst.getNext()) {
                HashSet<VirtualRegister> use = new HashSet<>();
                HashSet<VirtualRegister> def = new HashSet<>();
                HashMap<Register, Register> renameMap = new HashMap<>();
                for(Register reg : inst.getUsedRegisters()) {
                    VirtualRegister vr = (VirtualRegister) reg;
                    if(spillWorkList.contains(vr)) {
                        renameMap.put(vr, new VirtualRegister(""));
                        use.add(vr);
                    }
                }
                for(Register reg : inst.getDefinedRegisters()) {
                    VirtualRegister vr = (VirtualRegister) reg;
                    if(spillWorkList.contains(vr)) {
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
    
    private void preColor() {
        System.err.println("================================ precolor ====================================");
        for(VirtualRegister n : getAllVertex()) {
            if(n.getAllocatedPhysicalRegister() != null) {
                colors.put(n, n.getAllocatedPhysicalRegister());
                coloredNodes.add(n);
            }
        }
    }

    private void process(Function function) {
        while(true){
            init();
            build(function);
            makeWorkList();
            System.err.println("simplifyWorkList.size() = " + simplifyWorkList.size());
            do{
                if(!simplifyWorkList.isEmpty()) simplify();
                else if(!workListMoves.isEmpty()) coalesce();
                else if(!freezeWorkList.isEmpty()) freeze();
                else if(!spillWorkList.isEmpty()) selectSpill();
            } while(!simplifyWorkList.isEmpty() || !workListMoves.isEmpty() || !freezeWorkList.isEmpty() || !spillWorkList.isEmpty());
//            preColor();
            assignColors();
            if(!spillNodes.isEmpty()) {
                rewriteFunction(function);
            } else {
                allocateRegisters(function);
                break;
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
