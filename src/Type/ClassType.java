package Type;

import Scope.ClassEntity;
import Utility.Config;

public class ClassType extends Type {
    private ClassEntity classEntity;

    public ClassType(String name) {
        type = types.CLASS;
        typeName = name;
        classEntity = new ClassEntity();
    }

    public void setClassEntity(ClassEntity classEntity) {
        this.classEntity = classEntity;
    }

    public ClassEntity getClassEntity() {
        return classEntity;
    }

    @Override
    public boolean match(Type other) {
        if(other instanceof ClassType) {
            return typeName.equals(other.getTypeName());
        } else if(other.getType() == types.NULL) {
            return true;
        } else return false;
    }

    @Override
    public int getBytes() {
        return classEntity.getScope().getVariables().values().size() * Config.getRegSize();
    }
}
