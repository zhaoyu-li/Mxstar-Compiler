package AST;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;

public abstract class Node {
    protected Location location;

    public Node() {
        location = new Location();
    }

    public Node(int line, int column) {
        location = new Location(line, column);
    }

    public Node(Token token) {
        location = new Location(token);
    }

    public Node(ParserRuleContext ctx) {
        location = new Location(ctx);
    }

    public void setLocation(int line, int column) {
        location.setLocation(line, column);
    }

    public void setLocation(Token token) {
        location.setLocation(token);
    }

    public void setLocation(ParserRuleContext ctx) {
        location.setLocation(ctx);
    }

    public Location getLocation() {
        return location;
    }

    abstract public void accept(ASTVistor vistor);
}
