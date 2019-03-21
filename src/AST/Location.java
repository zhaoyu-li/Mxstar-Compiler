package AST;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;

public class Location {
    private int line;
    private int column;

    public Location() {
        line = 0;
        column = 0;
    }

    public Location(int line, int column) {
        this.line = line;
        this.column = column;
    }

    public Location(Token token) {
        this.line = token.getLine();
        this.column = token.getCharPositionInLine();
    }

    public Location(ParserRuleContext ctx) {
        this(ctx.start);
    }

    public void setLocation(int line, int column) {
        this.line = line;
        this.column = column;
    }

    public void setLocation(Token token) {
        this.line = token.getLine();
        this.column = token.getCharPositionInLine();
    }

    public void setLocation(ParserRuleContext ctx) {
        setLocation(ctx.start);
    }

    public final int getLine() {
        return line;
    }

    public final int getColumn() {
        return column;
    }

    @Override
    public String toString() {
        return "line " + line + ": " + column;
    }
}
