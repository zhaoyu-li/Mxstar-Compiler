package FrontEnd;

import AST.*;
import Scope.*;
import Type.*;
import Utility.SemanticError;

import java.util.LinkedList;
import java.util.List;

public class ScopeBuilder implements ASTVistor {
    private GlobalScope globalScope;
    private Scope curScope;
    private String curClassName;
    private FunctionEntity curFunction;

    public ScopeBuilder() {
        globalScope = new GlobalScope();
        curScope = globalScope;
        curClassName = null;
        curFunction = null;
    }

    public GlobalScope getGlobalScope() {
        return globalScope;
    }

    private void enterScope(Scope scope) {
        curScope = scope;
    }

    private void exitScope() {
        curScope = curScope.getParent();
    }

    private Type resolveType(TypeNode typeNode) {
        if(typeNode.isPrimitiveType()) {
            return typeNode.getType();
        } else if(typeNode instanceof ArrayTypeNode) {
            return resolveArrayType((ArrayTypeNode) typeNode);
        } else if(typeNode.isClassType()) {
            if(globalScope.getClassEntity(typeNode.getType().getTypeName()) == null) {
                return null;
            } else {
                ClassType classType = new ClassType(typeNode.getType().getTypeName());
                classType.setClassEntity(globalScope.getClassEntity(typeNode.getType().getTypeName()));
                return classType;
            }
        } else {
            return null;
        }
    }

    private Type resolveArrayType(ArrayTypeNode typeNode) {
        int dimension = typeNode.getDimension();
        if(dimension == 1) {
            TypeNode newType = new TypeNode(typeNode.getBaseType().getType());
            return new ArrayType(resolveType(newType));
        } else {
            ArrayTypeNode newType = new ArrayTypeNode(typeNode.getBaseType(), dimension - 1);
            return new ArrayType(resolveType(newType));
        }
    }

    private VariableEntity getVariableEntity (VariableDeclaration variableDeclaration) {
        VariableEntity variableEntity = new VariableEntity();
        variableEntity.setType(resolveType(variableDeclaration.getType()));
        variableEntity.setLocation(variableDeclaration.getLocation());
        if(variableDeclaration.getType().getType().getType() == Type.types.VOID) {
            throw new SemanticError(variableDeclaration.getLocation(), "VariableDeclaration's type cannot be void");
        }
        variableEntity.setName(variableDeclaration.getName());
        return variableEntity;
    }

    private void registerFunction(FunctionDeclaration node) {
        if(curScope.getFunction(node.getName()) != null) {
            throw new SemanticError(node.getLocation(), "Duplicate FunctionDeclaration");
        }
        if(globalScope.getClassEntity(node.getName()) != null && node.getReturnType().getType().getType() != Type.types.NULL) {
            throw new SemanticError(node.getLocation(), "The name of function conflicts with a class");
        }
        FunctionEntity functionEntity = new FunctionEntity();
        if(curClassName == null) {
            functionEntity.setName(node.getName());
        } else {
            functionEntity.setName(curClassName + '_' + node.getName());
            functionEntity.setGlobal(false);
        }
        if(resolveType(node.getReturnType()) == null){ //constructor
            if(globalScope.getClassEntity(node.getName()) != null) {
                if(curScope == globalScope.getClassEntity(node.getName()).getScope()) {
                    Type type = new Type();
                    type.setType(Type.types.VOID);
                    functionEntity.setReturnType(type);
                } else {
                    throw new SemanticError(node.getLocation(), "Invalid type of function");
                }
            } else {
                throw new SemanticError(node.getLocation(), "Invalid return type of function");
            }
        } else {
            functionEntity.setReturnType(resolveType(node.getReturnType()));
        }
        List<VariableEntity> parameters = new LinkedList<VariableEntity>();
        for(VariableDeclaration parameter : node.getParameters()) {
            parameters.add(getVariableEntity(parameter));
        }
        functionEntity.setParameters(parameters);
        node.setFunctionEntity(functionEntity);
        curScope.putFunction(node.getName(), functionEntity);
    }

    private void registerClass(ClassDeclaration node) {
        if(globalScope.getClassEntity(node.getName()) != null) {
            throw new SemanticError(node.getLocation(), "Duplicate ClassDeclaration");
        }
        if(globalScope.getFunction(node.getName()) != null) {
            throw new SemanticError(node.getLocation(), "The name of class conflicts with a function");
        }
        ClassEntity classEntity = new ClassEntity();
        classEntity.setName(node.getName());
        classEntity.setScope(new Scope(globalScope));
        node.setClassEntity(classEntity);
        globalScope.putClassEntity(classEntity.getName(), classEntity);
    }

    private void registerClassFunction(ClassDeclaration node) {
        ClassEntity classEntity = globalScope.getClassEntity(node.getName());
        curClassName = classEntity.getName();
        enterScope(classEntity.getScope());
        if(node.getConstructor() != null) {
            registerFunction(node.getConstructor());
        }
        for(FunctionDeclaration functionDeclaration : node.getMethods()) {
            registerFunction(functionDeclaration);
        }
        exitScope();
        curClassName = null;
    }

    private void registerClassVariable(ClassDeclaration node) {
        ClassEntity classEntity = globalScope.getClassEntity(node.getName());
        curClassName = classEntity.getName();
        enterScope(classEntity.getScope());
        VariableEntity thisVariable = new VariableEntity();
        thisVariable.setName("this");
        ClassType thisType = new ClassType(curClassName);
        thisType.setClassEntity(globalScope.getClassEntity(curClassName));
        thisVariable.setType(thisType);
        curScope.putVariable(thisVariable.getName(), thisVariable);
        for(VariableDeclaration variableDeclaration : node.getFields()) {
            visit(variableDeclaration);
        }
        exitScope();
        curClassName = null;
    }

    @Override
    public void visit(Program node) {
        for(ClassDeclaration classDeclaration : node.getClasses()) {
            registerClass(classDeclaration);
        }
        for(ClassDeclaration classDeclaration : node.getClasses()) {
            registerClassFunction(classDeclaration);
        }
        for(ClassDeclaration classDeclaration : node.getClasses()) {
            registerClassVariable(classDeclaration);
        }
        for(FunctionDeclaration functionDeclaration : node.getFunctions()) {
            registerFunction(functionDeclaration);
        }
        for(VariableDeclaration variableDeclaration : node.getVariables()) {
            visit(variableDeclaration);
        }
        for(ClassDeclaration classDeclaration : node.getClasses()) {
            visit(classDeclaration);
        }
        for(FunctionDeclaration functionDeclaration : node.getFunctions()) {
            visit(functionDeclaration);
        }
    }

    @Override
    public void visit(FunctionDeclaration node) {
        FunctionEntity functionEntity = curScope.getFunction(node.getName());
        functionEntity.setScope(new Scope(curScope));
        curFunction = functionEntity;
        enterScope(functionEntity.getScope());
        for(VariableDeclaration variableDeclaration : node.getParameters()) {
            visit(variableDeclaration);
        }
        for(Statement statement : node.getBody()) {
            statement.accept(this);
        }
        exitScope();
        curFunction = null;
    }

    @Override
    public void visit(ClassDeclaration node) {
        ClassEntity classEntity = globalScope.getClassEntity(node.getName());
        enterScope(classEntity.getScope());
        curClassName = node.getName();
        if(node.getConstructor() != null) {
            visit(node.getConstructor());
        }
        for(FunctionDeclaration functionDeclaration : node.getMethods()) {
            visit(functionDeclaration);
        }
        exitScope();
        curClassName = null;
    }

    @Override
    public void visit(VariableDeclaration node) {
        if(node.getInit() != null) {
            node.getInit().accept(this);
        }
        VariableEntity variableEntity = new VariableEntity();
        variableEntity.setName(node.getName());
        variableEntity.setType(resolveType(node.getType()));
        node.getType().setType(variableEntity.getType());
        variableEntity.setLocation(node.getLocation());
        if(node.getType().getType().isVoidType()) {
            throw new SemanticError(node.getLocation(), "VariableDeclaration's type cannot be void");
        }
        if(curScope == globalScope) {
            variableEntity.setGlobal(true);
            globalScope.putGlobalVariable(variableEntity);
        }
        if(curClassName != null) {
            if(curScope == globalScope.getClassEntity(curClassName).getScope()) {
                variableEntity.setInClass(true);
            }
        }
        if(curScope.getVariable(node.getName()) != null) {
            throw new SemanticError(node.getLocation(), "Duplicate VariableDeclaration");
        } else {
            curScope.putVariable(variableEntity.getName(), variableEntity);
        }
        node.setVariableEntity(variableEntity);
    }

    @Override
    public void visit(TypeNode node) {

    }

    @Override
    public void visit(ArrayTypeNode node) {

    }

    @Override
    public void visit(IfStatement node) {
        node.getCondition().accept(this);
        node.getThenStatement().accept(this);
        if(node.getElseStatement() != null) {
            node.getElseStatement().accept(this);
        }
    }

    @Override
    public void visit(WhileStatement node) {
        node.getCondition().accept(this);
        node.getBody().accept(this);
    }

    @Override
    public void visit(ForStatement node) {
        if(node.getInit() != null) {
            node.getInit().accept(this);
        }
        if(node.getCondition() != null) {
            node.getCondition().accept(this);
        }
        if(node.getUpdate() != null) {
            node.getUpdate().accept(this);
        }
        node.getBody().accept(this);
    }

    @Override
    public void visit(BreakStatement node) {

    }

    @Override
    public void visit(ContinueStatement node) {

    }

    @Override
    public void visit(ReturnStatement node) {
        if(node.getRet() != null) {
            node.getRet().accept(this);
        }
    }

    @Override
    public void visit(ExprStatement node) {
        node.getExpr().accept(this);
    }

    @Override
    public void visit(VarDeclStatement node) {
        node.getDeclaration().accept(this);
    }

    @Override
    public void visit(BlockStatement node) {
        Scope blockScope = new Scope(curScope);
        enterScope(blockScope);
        for(Statement statement : node.getStatements()) {
            statement.accept(this);
        }
        exitScope();
    }

    @Override
    public void visit(EmptyStatement node) {

    }

    @Override
    public void visit(ThisExpression node) {
        node.setVariableEntity(curScope.getRecursiveVariable("this"));
        node.setType(node.getVariableEntity().getType());
    }

    @Override
    public void visit(NullLiteral node) {
        node.setType(new Type("null"));
    }

    @Override
    public void visit(BoolLiteral node) {
        node.setType(new Type("bool"));
    }

    @Override
    public void visit(IntLiteral node) {
        node.setType(new Type("int"));
    }

    @Override
    public void visit(StringLiteral node) {
        node.setType(new Type("string"));
    }

    @Override
    public void visit(Identifier node) {
        VariableEntity variableEntity = curScope.getRecursiveVariable(node.getName());
        if(variableEntity == null) {
            throw new SemanticError(node.getLocation(), "Cannot find identifier");
        } else {
            if(!(variableEntity.getType() instanceof ClassType)){
                if(variableEntity.getLocation().getLine() > node.getLocation().getLine()) {
                    throw new SemanticError(node.getLocation(), "Cannot find identifier");
                }
            }
            node.setVariableEntity(curScope.getRecursiveVariable(node.getName()));
        }
        node.setType(node.getVariableEntity().getType());
        if(curFunction != null && variableEntity.isGlobal()) {
            curFunction.addGlobalVariable(variableEntity);
        }
    }

    @Override
    public void visit(MemberExpression node) {
        node.getExpr().accept(this);
        if(node.getExpr().getType() instanceof ClassType) {
            ClassEntity classEntity = ((ClassType) node.getExpr().getType()).getClassEntity();
            if(node.getMember() != null) {
                if(classEntity.getScope().getVariable(node.getMember().getName()) == null) {
                    throw new SemanticError(node.getLocation(), "Cannot find member");
                } else {
                    node.getMember().setVariableEntity(classEntity.getScope().getVariable(node.getMember().getName()));
                    node.getMember().setType(node.getMember().getVariableEntity().getType());
                }
                node.setType(node.getMember().getType());
            } else {
                if(classEntity.getScope().getFunction(node.getFuncCall().getName().getName()) == null) {
                    throw new SemanticError(node.getLocation(), "Cannot find function");
                } else {
                    node.getFuncCall().setFunctionEntity(classEntity.getScope().getFunction(node.getFuncCall().getName().getName()));
                    for(Expression expression : node.getFuncCall().getArguments()) {
                        expression.accept(this);
                    }
                    node.getFuncCall().setType(node.getFuncCall().getFunctionEntity().getReturnType());
                }
                node.setType(node.getFuncCall().getType());
            }
        } else if (node.getExpr().getType().isStringType()) {
            if(node.getFuncCall() != null) {
                if(globalScope.getFunction("string_" + node.getFuncCall().getName().getName()) == null) {
                    throw new SemanticError(node.getLocation(), "Cannot find function");
                } else {
                    node.getFuncCall().setFunctionEntity(globalScope.getFunction("string_" + node.getFuncCall().getName().getName()));
                    for(Expression expression : node.getFuncCall().getArguments()) {
                        expression.accept(this);
                    }
                    node.getFuncCall().setType(node.getFuncCall().getFunctionEntity().getReturnType());
                }
                node.setType(node.getFuncCall().getType());
            }
        } else if (node.getExpr().getType() instanceof ArrayType) {
            if(node.getFuncCall() != null) {
                if(globalScope.getFunction("array_" + node.getFuncCall().getName().getName()) == null) {
                    throw new SemanticError(node.getLocation(), "Cannot find function");
                } else {
                    node.getFuncCall().setFunctionEntity(globalScope.getFunction("array_" + node.getFuncCall().getName().getName()));
                    for(Expression expression : node.getFuncCall().getArguments()) {
                        expression.accept(this);
                    }
                    node.getFuncCall().setType(node.getFuncCall().getFunctionEntity().getReturnType());
                }
                node.setType(node.getFuncCall().getType());
            }
        } else {
            throw new SemanticError(node.getLocation(), "Invalid MemberExpression");
        }
    }

    @Override
    public void visit(ArrayExpression node) {
        node.getArr().accept(this);
        node.getIdx().accept(this);
        if(node.getArr().getType() instanceof ArrayType){
            node.setType(((ArrayType) node.getArr().getType()).getBaseType());
        } else {
            throw new SemanticError(node.getLocation(), "Expect a array type");
        }
    }

    @Override
    public void visit(FuncCallExpression node) {
        if(curScope.getRecursiveFunction(node.getName().getName()) == null) {
            throw new SemanticError(node.getLocation(), "Cannot find function");
        } else {
            node.setFunctionEntity(curScope.getRecursiveFunction(node.getName().getName()));
        }
        for(Expression expression : node.getArguments()) {
            expression.accept(this);
        }
        node.setType(node.getFunctionEntity().getReturnType());
    }

    @Override
    public void visit(NewExpression node) {
        int dimension = node.getDimensions().size() + node.getNumDimension();
        node.setType(resolveType(node.getTypeNode()));
        if(node.getDimensions() != null) {
            for(Expression expression : node.getDimensions()) {
                expression.accept(this);
            }
        }
        if(dimension != 0) {
            for(int i = 0; i < dimension; i++) {
                node.setType(new ArrayType(node.getType()));
            }
        }
        if(node.getTypeNode().getType().getType() == Type.types.VOID) {
            throw new SemanticError(node.getLocation(), "Cannot new void");
        }
    }

    @Override
    public void visit(SuffixExpression node) {
        node.getExpr().accept(this);
        node.setType(node.getExpr().getType());
    }

    @Override
    public void visit(PrefixExpression node) {
        node.getExpr().accept(this);
        node.setType(node.getExpr().getType());
    }

    @Override
    public void visit(BinaryExpression node) {
        node.getLhs().accept(this);
        node.getRhs().accept(this);
        switch (node.getOp()) {
            case "<":
            case ">":
            case "<=":
            case ">=":
            case "==":
            case "!=":
                node.setType(new Type("bool"));
                break;
            default:
                node.setType(node.getLhs().getType());
                break;
        }
    }

    @Override
    public void visit(AssignExpression node) {
        node.getLhs().accept(this);
        node.getRhs().accept(this);
        node.setType(new Type("void"));
    }

}