package FrontEnd;

import AST.Location;
import Utility.*;
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;

public class PaserErrorListener extends BaseErrorListener {
    @Override
    public void syntaxError(Recognizer<?, ?> recognizer, Object symbol, int line, int column, String message, RecognitionException e) {
        throw new SemanticError(new Location(line, column), message);
    }
}
