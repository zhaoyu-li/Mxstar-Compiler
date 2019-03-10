import FrontEnd.PaserErrorListener;
import Parser.MxstarLexer;
import Parser.MxstarParser;
import org.antlr.v4.runtime.ANTLRFileStream;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;


import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.LinkedList;
import java.util.List;

public class Main {
    public static void main(String[] args) throws Exception {
        parseOption(args);

        //InputStream is = new FileInputStream();
        //PrintStream os = new PrintStream();


    }

    public static void parseOption(String []args) {

    }

    public static void compile(InputStream sourceCode, PrintStream asmCode) throws Exception{
        ANTLRInputStream input = new ANTLRInputStream(sourceCode);
        MxstarLexer lexer = new MxstarLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        MxstarParser parser = new MxstarParser(tokens);
        parser.removeErrorListeners();
        parser.addErrorListener(new PaserErrorListener());
        ParseTree tree = parser.program();

        ParseTreeWalker walker = new ParseTreeWalker();


    }


}
