package Type;

import Scope.ClassEntity;

public class ClassType extends Type {
    private String name;
    private ClassEntity classEntity;

    public ClassType(String name) {
        type = types.CLASS;
        this.name = name;
        classEntity = new ClassEntity();
    }

    public void setClassEntity(ClassEntity classEntity) {
        this.classEntity = classEntity;
    }

    public ClassEntity getClassEntity() {
        return classEntity;
    }

    public String getName() {
        return name;
    }
}
