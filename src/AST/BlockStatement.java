package AST;

import Scope.Scope;

import java.util.LinkedList;
import java.util.List;

public class BlockStatement extends Statement {
    private List<Statement> statements;

    public BlockStatement() {
        statements = new LinkedList<>();
    }

    public void setStatements(List<Statement> statements) {
        this.statements = statements;
    }

    public List<Statement> getStatements() {
        return statements;
    }

    @Override
    public void accept(ASTVistor vistor) {
        vistor.visit(this);
    }
}
