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
    : Ident ('=' expression)?
    ;

stateDecl
    : 'state' Ident '(' parameters? ')' '{' statement* '}'
    ;

parameters
    : Ident (',' Ident)*
    ;

statement
    : (compoundVariableDecl
    | assignStmt
    | ifStmt
    | whileStmt
    | yieldStmt
    | returnStmt) ';'?
    ;

assignStmt
    : lvalue assignOperator expression
    ;

assignOperator
    : Beq
    | PlusEq
    | MinusEq
    | TimesEq
    | DivEq
    ;

lvalue
    : Ident
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

whileStmt
    : 'while' '(' expression ')' '{' statement* '}'
    ;

yieldStmt
    : 'yield' functionCall
    ;

returnStmt
    : 'return' expression
    ;

functionDecl
    : 'def' Ident '(' parameters? ')' '{' statement* '}'
    ;

expression
    : condition
    ;

condition
    : condition conditionOperator comparison
    | comparison
    ;

conditionOperator
    : Or
    | And
    ;

comparison
    : term comparator term
    | term
    ;

comparator
    : Lt
    | Le
    | Gt
    | Ge
    | Eq
    | Ne
    ;

term
    : term termOperator product
    | product
    ;

termOperator
    : Plus
    | Minus
    ;

product
    : product productOperator atom
    | atom
    ;

productOperator
    : Times
    | Div
    | Mod
    ;

atom
    : functionCall
    | member
    | Ident
    | literal
    | '(' expression ')'
    ;

member
    : '@' functionCall
    ;

functionCall
    : Ident '(' arguments? ')'
    ;

arguments
    : expression (',' expression)*
    ;

literal
    : number
    | bool
    ;

number
    : (Plus | Minus)? Number
    ;

bool
    : False
    | True
    ;

Or      : 'or';
And     : 'and';
Plus    : '+';
Minus   : '-';
Times   : '*';
Div     : '/';
Mod     : '%';
Lt      : '<';
Le      : '<=';
Gt      : '>';
Ge      : '>=';
Eq      : '==';
Ne      : '!=';
Beq     : '=';
PlusEq  : '+=';
MinusEq : '-=';
TimesEq : '*=';
DivEq   : '/=';

False   : 'false';
True    : 'true';

Ident
    : ('a' .. 'z' | 'A' .. 'Z' | '_') ('a' .. 'z' | 'A' .. 'Z' | '_' | '0' .. '9') *
    ;

Number
    : [0-9]+ ('.' [0-9]+)?
    ;

Comment
    : '//' ~ [\r\n]* -> skip
    ;

Ws
    : [ \t\r\n] -> skip
    ;
