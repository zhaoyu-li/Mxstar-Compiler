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

public class GraphAllocator {
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
    private HashSet<VirtualRegister> precolored;
    private HashSet<VirtualRegister> initial;

    public GraphAllocator(IRProgram program) {
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
        this.precolored = new HashSet<>();
        this.initial = new HashSet<>();
    }

    private void addRegister(VirtualRegister vr) {
        if(!adjList.containsKey(vr)) {
            adjSet.put(vr, new HashSet<>());
            adjList.put(vr, new HashSet<>());
            moveList.put(vr, new HashSet<>());
            alias.put(vr, vr);
            if(vr.getAllocatedPhysicalRegister() != null) {
                precolored.add(vr);
                degrees.put(vr, Integer.MAX_VALUE);
            } else {
                initial.add(vr);
                degrees.put(vr, 0);
            }
        }
    }

    private void addEdge(VirtualRegister a, VirtualRegister b) {
        if (!a.equals(b) && !adjSet.get(a).contains(b)) {
            adjSet.get(a).add(b);
            adjSet.get(b).add(a);
            if (!precolored.contains(a)) {
                adjList.get(a).add(b);
                degrees.put(a, degree(a) + 1);
            }
            if (!precolored.contains(b)) {
                adjList.get(b).add(a);
                degrees.put(b, degree(b) + 1);
            }
        }
    }

    private HashSet<VirtualRegister> adjacent(VirtualRegister n) {
        HashSet<VirtualRegister> adjacent = new HashSet<>(adjList.get(n));
        adjacent.removeAll(union(selectStack, coalescedNodes));
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
        if(!precolored.contains(u) && !moveRelated(u) && degree(u) < K) {
            freezeWorkList.remove(u);
            simplifyWorkList.add(u);
        }
    }

    private boolean ok(VirtualRegister t, VirtualRegister r) {
        return degree(t) < K || precolored.contains(t) || adjSet.get(t).contains(r);
    }

    private boolean conservative(HashSet<VirtualRegister> nodes) {
        int k = 0;
        for(VirtualRegister n : nodes) {
            if(degree(n) >= K) k++;
        }
        return k < K;
    }

    private void coalesce() {
        System.err.println("================================ coalesce ====================================");
        Move m = workListMoves.iterator().next();
        VirtualRegister x = getAlias((VirtualRegister) m.getDst());
        VirtualRegister y = getAlias((VirtualRegister) m.getSrc());
        VirtualRegister u;
        VirtualRegister v;
        if(precolored.contains(y)) {
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
        } else if(precolored.contains(v) || adjSet.get(u).contains(v)) {
            constrainMoves.add(m);
            addWorkList(u);
            addWorkList(v);
        } else if((precolored.contains(u) && judgeOk(u, v))
                || (!precolored.contains(u) && conservative(union(adjacent(u), adjacent(v))))) {
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
            VirtualRegister x = (VirtualRegister) m.getDst();
            VirtualRegister y = (VirtualRegister) m.getSrc();
            if(getAlias(y) == getAlias(u)) {
                v = getAlias(x);
            } else {
                v = getAlias(y);
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
        int d = -1;
        for(VirtualRegister vr : spillWorkList) {
            if(degree(vr) > d) {
                d = degree(vr);
                m = vr;
            }
        }
        m = spillWorkList.iterator().next();
        spillWorkList.remove(m);
        spillWorkList.remove(m);
        simplifyWorkList.add(m);
        freezeMoves(m);
    }
    
    private void assignColors() {
        System.err.println("================================ assign colors ====================================");
        for(VirtualRegister n : selectStack) {
            HashSet<PhysicalRegister> okColors = new HashSet<>(physicalRegisters);
            for(VirtualRegister w : adjList.get(n)) {
                if(coloredNodes.contains(getAlias(w)) || precolored.contains(getAlias(w))) {
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
        selectStack.clear();
        for(VirtualRegister n : coalescedNodes) {
            System.err.println("n = " + n.getName() + " " + "alias(n) = " + getAlias(n).getName());
            coloredNodes.add(n);
            colors.put(n, colors.get(getAlias(n)));
            System.err.println("assign color : vr = " + n.getName() + " pr = " + colors.get(getAlias(n)).getName());
        }
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
            /*if(bb == function.getHeadBB()) {
                for(VirtualRegister vr : RegisterSet.vcalleeSave) {
                    addRegister(vr);
                }
            }*/
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
            /*if(bb == function.getTailBB()) {
                live.addAll(RegisterSet.vcalleeSave);
            }*/
            for(Instruction inst = bb.getTail(); inst != null; inst = inst.getPrev()) {
                if(isMoveInstruction(inst)) {
                    live.removeAll(trans(inst.getUsedRegisters()));
                    for(VirtualRegister n : union(trans(inst.getDefinedRegisters()), trans(inst.getUsedRegisters()))) {
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
                live.removeAll(trans(inst.getDefinedRegisters()));
                live.addAll(trans(inst.getUsedRegisters()));
            }
            /*if(bb == function.getHeadBB()) {
                HashSet<VirtualRegister> def = new HashSet<>(RegisterSet.vcalleeSave);
                live.addAll(def);
                for(VirtualRegister d : def) {
                    for(VirtualRegister l : live) {
                        addEdge(l, d);
                    }
                }
                live.removeAll(def);
            }*/
        }
    }

    private void makeWorkList() {
        System.err.println("================================ makeWorkList ====================================");
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
        for(VirtualRegister vr : spillNodes) {
            if(vr.getSpillSpace() == null) {
                vr.setSpillSpace(new StackSlot());
            }
        }
        for(BasicBlock bb : function.getBasicBlocks()) {
            for(Instruction inst = bb.getHead(); inst != null; inst = inst.getNext()) {
                HashSet<VirtualRegister> use = new HashSet<>();
                HashSet<VirtualRegister> def = new HashSet<>();
                HashMap<Register, Register> renameMap = new HashMap<>();
                for(VirtualRegister vr : trans(inst.getUsedRegisters())) {
                    if(spillNodes.contains(vr)) {
                        renameMap.put(vr, new VirtualRegister(""));
                        use.add(vr);
                    }
                }
                for(VirtualRegister vr : trans(inst.getDefinedRegisters())) {
                    if(spillNodes.contains(vr)) {
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
        for(VirtualRegister n : precolored) {
            colors.put(n, n.getAllocatedPhysicalRegister());
        }
    }

    private void check() {
        for(VirtualRegister u :initial) {
            HashSet<VirtualRegister> rhs = new HashSet<>(union(precolored, initial));
            rhs.retainAll(adjList.get(u));
            assert degree(u) == rhs.size();
        }
        for(VirtualRegister u : simplifyWorkList) {
            HashSet<Move> rhs = new HashSet<>(activeMoves);
            rhs.addAll(workListMoves);
            rhs.retainAll(moveList.get(u));
            assert degree(u) < K && rhs.isEmpty();
        }
        for(VirtualRegister u : freezeWorkList) {
            HashSet<Move> rhs = new HashSet<>(activeMoves);
            rhs.addAll(workListMoves);
            rhs.retainAll(moveList.get(u));
            assert degree(u) < K && rhs.isEmpty();
        }
        for(VirtualRegister u : spillWorkList) {
            assert degree(u) >= K;
        }
    }

    private void process(Function function) {
        while(true){
            init();
            build(function);
            preColor();
            makeWorkList();
//            check();
            do{
                if(!simplifyWorkList.isEmpty()) simplify();
                else if(!workListMoves.isEmpty()) coalesce();
                else if(!freezeWorkList.isEmpty()) freeze();
                else if(!spillWorkList.isEmpty()) selectSpill();
            } while(!simplifyWorkList.isEmpty() || !workListMoves.isEmpty() || !freezeWorkList.isEmpty() || !spillWorkList.isEmpty());
            assignColors();
            if(!spillNodes.isEmpty()) {
                System.err.println("SpillNodes.size() = " + spillNodes.size());
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
                System.err.println("====================== allocate register in " + function.getName() + " =====================");
                process(function);
            }
        }
    }
}
