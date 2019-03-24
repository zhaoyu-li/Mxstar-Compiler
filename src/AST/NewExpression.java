package AST;

import java.util.LinkedList;
import java.util.List;

public class NewExpression extends Expression {
    private TypeNode typeNode;
    private List<Expression> dimensions;
    private int numDimension;

    public NewExpression() {
        typeNode = null;
        dimensions = new LinkedList<>();
        numDimension = 0;
    }

    public void setTypeNode(TypeNode typeNode) {
        this.typeNode = typeNode;
    }

    public TypeNode getTypeNode() {
        return typeNode;
    }

    public void setDimensions(List<Expression> dimensions) {
        this.dimensions = dimensions;
    }

    public List<Expression> getDimensions() {
        return dimensions;
    }

    public void setNumDimension(int numDimension) {
        this.numDimension = numDimension;
    }

    public int getNumDimension() {
        return numDimension;
    }

    @Override
    public void accept(ASTVistor vistor) {
        vistor.visit(this);
    }
}
