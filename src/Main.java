import AST.Program;
import BackEnd.*;
import FrontEnd.*;
import FrontEnd.UselessLoopEliminator;
import IR.IRProgram;
import IR.RegisterSet;
import Parser.MxstarLexer;
import Parser.MxstarParser;
import Utility.Config;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;


import java.io.FileInputStream;
import java.io.InputStream;
import java.io.PrintStream;

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
        RegisterSet.init();

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

        if(Config.printAST) {
            ASTPrinter astPrinter = new ASTPrinter();
            astPrinter.visit(program);
            astPrinter.print();
        }

        ScopeBuilder scopeBuilder = new ScopeBuilder();
        scopeBuilder.visit(program);

        SemanticChecker semanticChecker = new SemanticChecker(scopeBuilder.getGlobalScope());
        semanticChecker.visit(program);

        if(Config.useUselessLoopElimination) {
            UselessLoopEliminator uselessLoopEliminator = new UselessLoopEliminator(program);
            uselessLoopEliminator.run();
        }

        if(Config.useLoopConditionOptimization) {
            LoopConditionOptimizer loopConditionOptimizer = new LoopConditionOptimizer(program);
            loopConditionOptimizer.run();
        }

        if(Config.useCommonExpressionElimination) {
            CommonExpressionElimination commonExpressionElimination = new CommonExpressionElimination(program);
            commonExpressionElimination.run();
        }

        IRBuilder irBuilder = new IRBuilder(scopeBuilder.getGlobalScope());
        irBuilder.visit(program);
        IRProgram irProgram = irBuilder.getProgram();

        if(Config.useMemorization) {
            Memorization memorization = new Memorization(irProgram);
            memorization.run();
        }

        if(Config.useBasicBlockOptimization) {
            BasicBlockOptimizer basicBlockOptimizer = new BasicBlockOptimizer(irProgram);
            basicBlockOptimizer.run();
        }

        if(Config.useLocalValueOptimization) {
            LocalValueNumberOptimizer localValueNumberOptimizer = new LocalValueNumberOptimizer(irProgram);
            localValueNumberOptimizer.run();
        }

        if(Config.useDeadCodeElimination) {
            DeadCodeEliminator deadCodeEliminator = new DeadCodeEliminator(irProgram);
            deadCodeEliminator.run();
        }

        IRCorrector irCorrector = new IRCorrector();
        irCorrector.visit(irProgram);

        if(Config.printIR) {
            IRPrinter irPrinter = new IRPrinter();
            irPrinter.visit(irProgram);
            irPrinter.print();
        }

        switch(Config.useAllocator) {
            case 1:
                SimpleAllocator simpleAllocator = new SimpleAllocator(irProgram);
                simpleAllocator.allocateRegisters();
                break;
            case 2:
                ChordalGraphAllocator chordalGraphAllocator = new ChordalGraphAllocator(irProgram);
                chordalGraphAllocator.run();
                break;
            case 3:
                SimpleGraphAllocator simpleGraphAllocator = new SimpleGraphAllocator(irProgram);
                simpleGraphAllocator.run();
                break;
            case 4:
                GraphAllocator graphAllocator = new GraphAllocator(irProgram);
                graphAllocator.run();
                break;
        }

        StackBuilder stackBuilder = new StackBuilder(irProgram);
        stackBuilder.build();

        CodeGenerator codeGenerator = new CodeGenerator();
        codeGenerator.visit(irProgram);
        codeGenerator.print(new PrintStream("program.asm"));
    }
}
