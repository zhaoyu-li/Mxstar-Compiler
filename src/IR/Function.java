package IR;

import IR.Instruction.Return;
import IR.Operand.Operand;
import IR.Operand.PhysicalRegister;
import IR.Operand.VirtualRegister;
import Scope.VariableEntity;

import java.util.LinkedList;
import java.util.List;

public class Function {
    public enum FuncType {
        UserDefined, Library, External
    }
    private FuncType type;
    private String name;
    private BasicBlock headBB;
    private BasicBlock tailBB;
    private List<VirtualRegister> parameters;
    private List<VariableEntity> usedGlobalVariables;
    private List<PhysicalRegister> usedPhysicalRegisters;
    private List<Return> returnList;

    public Function(FuncType type, String name) {
        this.type = type;
        this.name = name;
        parameters = new LinkedList<>();
        usedGlobalVariables = new LinkedList<>();
        usedPhysicalRegisters = new LinkedList<>();
        returnList = new LinkedList<>();
    }

    public String getName() {
        return name;
    }

    public void setHeadBB(BasicBlock headBB) {
        this.headBB = headBB;
    }

    public BasicBlock getHeadBB() {
        return headBB;
    }

    public void setTailBB(BasicBlock tailBB) {
        this.tailBB = tailBB;
    }

    public BasicBlock getTailBB() {
        return tailBB;
    }

    public void addParameter(VirtualRegister parameter) {
        parameters.add(parameter);
    }

    public List<VirtualRegister> getParameters() {
        return parameters;
    }

    public void addGlobalVariable(VariableEntity var) {
        usedGlobalVariables.add(var);
    }

    public List<VariableEntity> getUsedGlobalVariables() {
        return usedGlobalVariables;
    }

    public void addReturn(Return ret) {
        returnList.add(ret);
    }

    public List<Return> getReturnList() {
        return returnList;
    }
}
