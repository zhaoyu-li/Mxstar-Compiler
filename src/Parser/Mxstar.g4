grammar Mxstar;

program
    : globalDeclaration* EOF
    ;

//Declaration
globalDeclaration
    : functionDeclaration
    | classDeclaration
    | variableDeclaration
    ;

functionDeclaration
    : type IDENTIFIER '(' parameterList? ')' block
    ;

classDeclaration
    : CLASS IDENTIFIER '{' (functionDeclaration | constructorDeclaration | fieldDeclaration)* '}'
    ;

variableDeclaration
    : type variableDeclarators ';'
    ;

constructorDeclaration
    : IDENTIFIER '(' parameterList? ')' block
    ;

fieldDeclaration
    : type variableDeclarators ';'
    ;

variableDeclarators
    : variableDeclarator (',' variableDeclarator)*
    ;

variableDeclarator
    : IDENTIFIER ('=' expression)?
    ;

parameterList
    : parameter (',' parameter)*
    ;

parameter
    : type IDENTIFIER
    ;

block
    : '{' statementList '}'
    ;

type
    : (primitiveType | classType) ('['']')*
    ;

primitiveType
    : token=INT
    | token=BOOL
    | token=VOID
    | token=STRING
    ;

classType
    : token=IDENTIFIER
    ;

statementList
    : statement*
    ;

statement
    : IF '(' expression ')' statement (ELSE statement)?  #ifStatement
    | WHILE '(' expression ')' statement                 #whileStatement
    | FOR '(' forInit=expression? ';'
              forCondition=expression? ';'
              forUpdate=expression? ')'                  #forStatement
    | BREAK ';'                                          #breakStatement
    | CONTINUE ';'                                       #continueStatement
    | RETURN expression? ';'                             #returnStatement
    | expression ';'                                     #exprStatement
    | variableDeclaration                                #varDeclStatement
    | block                                              #blockStatement
    | ';'                                                #blankStatement
    ;

expressionList
    : expression (',' expression)*
    ;

expression
    : '(' expression ')'                                 #primaryExpression
    | token=THIS                                         #primaryExpression
    | token=NULL_LITERAL                                 #primaryExpression
    | token=BOOL_LITERAL                                 #primaryExpression
    | token=INT_LITERAL                                  #primaryExpression
    | token=STRING_LITERAL                               #primaryExpression
    | token=IDENTIFIER                                   #primaryExpression
    | expression op='.' IDENTIFIER                       #memberExpression
    | expression '[' expression ']'                      #arrayExpression
    | expression '(' expressionList? ')'                 #funcCallExpression
    | NEW creator                                        #newExpression
    | expression op=('++' | '--')                        #suffixExpression
    | op=('++' | '--') expression                        #prefixExpression
    | op=('+' | '-') expression                          #prefixExpression
    | op=('~' | '!') expression                          #prefixExpression
    | expression op=('*' | '/' | '%') expression         #binaryExpression
    | expression op=('+' | '-') expression               #binaryExpression
    | expression op=('<<' | '>>') expression             #binaryExpression
    | expression op=('<' | '>' | '>=' | '<=') expression #binaryExpression
    | expression op=('==' | '!=') expression             #binaryExpression
    | expression op='&' expression                       #binaryExpression
    | expression op='^' expression                       #binaryExpression
    | expression op='|' expression                       #binaryExpression
    | expression op='&&' expression                      #binaryExpression
    | expression op='||' expression                      #binaryExpression
    | <assoc=right> expression op='=' expression         #assignExpression
    ;

creator
    : (primitiveType | classType) ('[' expression ']')*('['']')*
    ;

// Reserved Words
BOOL : 'bool';
INT : 'int';
STRING : 'string';
VOID : 'void';
IF : 'if';
ELSE : 'else';
FOR : 'for';
WHILE : 'while';
BREAK : 'break';
CONTINUE : 'continue';
RETURN : 'return';
NEW : 'new';
CLASS : 'class';
THIS : 'this';

// Literal
BOOL_LITERAL : 'true' | 'false';
INT_LITERAL : [1-9][0-9]* | '0';
STRING_LITERAL : '"' (~["\\\n\r] | '\\' ["n\\])* '"';
NULL_LITERAL : 'null';

IDENTIFIER : [a-zA-Z][a-zA-Z0-9_]*;
LINECOMMENT : '//'~[\r\n]* -> skip;
BLOCKCOMMENT : '/*' .*? '*/' -> skip;
WHITESPACE : [ \t\r\n] -> skip;