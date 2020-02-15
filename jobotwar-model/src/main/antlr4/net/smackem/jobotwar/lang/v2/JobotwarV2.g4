grammar JobotwarV2;

program
    : declaration*
    ;

declaration
    : compoundVariableDecl
    | stateDecl
    | functionDecl
    ;

compoundVariableDecl
    : 'def' variableDecl (',' variableDecl)*
    ;

variableDecl
    : ID ('=' literal)?
    ;

stateDecl
    : 'state' ID '(' parameters? ')' '{' statement* '}'
    ;

parameters
    : ID (',' ID)*
    ;

statement
    : (compoundVariableDecl
    | assignStmt
    | ifStmt
    | yieldStmt
    | returnStmt) ';'?
    ;

assignStmt
    : lvalue '=' expression
    ;

lvalue
    : ID
    ;

ifStmt
    : 'if' expression '{' statement* '}' elseIfClause* elseClause?
    ;

elseIfClause
    : 'else' 'if' expression '{' statement* '}'
    ;

elseClause
    : 'else' '{' statement* '}'
    ;

yieldStmt
    : 'yield' functionCall
    ;

returnStmt
    : 'return' expression
    ;

functionDecl
    : 'def' ID '(' parameters? ')' '{' statement* '}'
    ;

expression
    : literal
    | member
    | functionCall
    ;

member
    : '@' functionCall
    ;

functionCall
    : ID '(' arguments? ')'
    ;

arguments
    : expression (',' expression)*
    ;

ID
    : ('a' .. 'z' | 'A' .. 'Z' | '_') ('a' .. 'z' | 'A' .. 'Z' | '_' | '0' .. '9') *
    ;

literal
    : number
    ;

number
    : ('+' | '-')? NUMBER
    ;

NUMBER
    : [0-9]+ ('.' [0-9]+)?
    ;

COMMENT
    : '//' ~ [\r\n]* -> skip
    ;

WS
    : [ \t\r\n] -> skip
    ;
