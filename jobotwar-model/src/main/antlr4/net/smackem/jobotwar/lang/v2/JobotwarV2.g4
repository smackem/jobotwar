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
    : ID ('=' number)?
    ;

stateDecl
    : 'state' ID '(' parameters? ')' '{' statement* '}'
    ;

parameters
    : ID (',' ID)*
    ;

statement
    : (compoundVariableDecl) ';'?
    ;

functionDecl
    : 'function' ID '(' parameters? ')' '{' statement* '}'
    ;

ID
    : ('a' .. 'z' | 'A' .. 'Z' | '_') ('a' .. 'z' | 'A' .. 'Z' | '_' | '0' .. '9') *
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
