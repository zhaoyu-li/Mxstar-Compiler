package AST;

import java.util.List;

public class BlockStatement extends Statement {
    public List<Statement> statements;

    BlockStatement() {}

    @Override
    public void accept(ASTVistor vistor) {
        vistor.visit(this);
    }
}
