package Scope;

public class ClassEntity extends Entity {
    private Scope scope;

    public ClassEntity() {
        scope = new Scope();
    }

    public Scope getScope() {
        return scope;
    }

    public void setScope(Scope scope) {
        this.scope = scope;
    }
}
