import AST.Program;
import BackEnd.*;
import FrontEnd.*;
import FrontEnd.LoopOptimizer;
import IR.IRProgram;
import IR.RegisterSet;
import Parser.MxstarLexer;
import Parser.MxstarParser;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;


import java.io.FileInputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Objects;

public class Main {
    public static void main(String[] args) throws Exception {
        String inputFile = "program.cpp";
        InputStream inputStream = new FileInputStream(inputFile);
        try{
            compile(inputStream);
        } catch (Error error) {
            System.err.println(error.getMessage());
            System.exit(1);
        }
    }

    private static void compile(InputStream sourceCode) throws Exception {

        ANTLRInputStream input = new ANTLRInputStream(sourceCode);
        MxstarLexer lexer = new MxstarLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        MxstarParser parser = new MxstarParser(tokens);
        parser.removeErrorListeners();
        parser.addErrorListener(new PaserErrorListener());
        ParseTree tree = parser.program();
        ASTBuilder astBuilder = new ASTBuilder();
        astBuilder.visit(tree);
        Program program = astBuilder.getProgram();

        ScopeBuilder scopeBuilder = new ScopeBuilder();
        scopeBuilder.visit(program);
        SemanticChecker semanticChecker = new SemanticChecker(scopeBuilder.getGlobalScope());
        semanticChecker.visit(program);
        RegisterSet.init();

        LoopOptimizer loopOptimizer = new LoopOptimizer(program);
        loopOptimizer.run();

        IRBuilder irBuilder = new IRBuilder(scopeBuilder.getGlobalScope());
        irBuilder.visit(program);

        IRProgram irProgram = irBuilder.getProgram();

        BasicBlockOptimizer basicBlockOptimizer = new BasicBlockOptimizer(irProgram);
        basicBlockOptimizer.run();

        LocalValueNumberOptimizer localValueNumberOptimizer = new LocalValueNumberOptimizer(irProgram);
        localValueNumberOptimizer.run();

        DeadCodeEliminator deadCodeEliminator = new DeadCodeEliminator(irProgram);
        deadCodeEliminator.run();

        NASMTransformer nasmTransformer = new NASMTransformer();
        nasmTransformer.visit(irProgram);

        IRPrinter irPrinter = new IRPrinter();
        irPrinter.visit(irProgram);
        irPrinter.print();

//        SimpleAllocator simpleAllocator = new SimpleAllocator(irProgram);
//        simpleAllocator.allocateRegisters();
        ChordalGraphAllocator chordalGraphAllocator = new ChordalGraphAllocator(irProgram);
        chordalGraphAllocator.run();
//        SimpleGraphAllocator simpleGraphAllocator = new SimpleGraphAllocator(irProgram);
//        simpleGraphAllocator.run();
//        GraphAllocator graphAllocator = new GraphAllocator(irProgram);
//        graphAllocator.run();

        StackBuilder stackBuilder = new StackBuilder(irProgram);
        stackBuilder.build();

        CodeGenerator codeGenerator = new CodeGenerator();
        codeGenerator.visit(irProgram);
        codeGenerator.print(new PrintStream("program.asm"));
    }
}
