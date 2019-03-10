package AST;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;

public class Location {
    private final int line;
    private final int column;

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

    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }

    @Override
    public String toString() {
        return "line " + line + ":" + column;
    }
}
