grammar Jobotwar;

program
    : declLine* line*
    ;

declLine
    : (comment | declaration)? EOL
    ;

declaration
    : 'def' ID (',' ID)*
    ;

line
    : (label | comment | statement)? EOL
    ;

label
    : ID ':'
    ;

statement
    : (assignStatement
    | gotoStatement
    | gosubStatement
    | endsubStatement) (ifClause | unlessClause)?
    ;

gotoStatement
    : 'goto' ID
    ;

gosubStatement
    : 'gosub' ID
    ;

endsubStatement
    : 'endsub'
    ;

assignStatement
    : term ('->' assignTarget)+
    ;

assignTarget
    : register
    | specialAssignTarget
    | ID
    ;

ifClause
    : 'if' condition
    ;

unlessClause
    : 'unless' condition
    ;

condition
    : condition conditionOperator comparison
    | comparison
    ;

conditionOperator
    : 'or'
    | 'and'
    ;

comparison
    : term comparator term
    | term
    ;

term
    : term termOperator product
    | product
    ;

termOperator
    : '+'
    | '-'
    ;

product
    : product productOperator molecule
    | molecule
    ;

productOperator
    : '*'
    | '/'
    | '%'
    ;

comparator
    : '<'
    | '<='
    | '>'
    | '>='
    | '='
    | '!='
    ;

molecule
    : func '(' condition ')'
    | atom
    ;

atom
    : number
    | register
    | ID
    | '(' condition ')'
    ;

register
    : AIM
    | SHOT
    | RADAR
    | SPEEDX
    | SPEEDY
    | RANDOM
    | DAMAGE
    | X
    | Y
    ;

func
    : ABS
    | NOT
    | TAN
    | SIN
    | COS
    | ATAN
    | ASIN
    | ACOS
    | SQRT
    | TRUNC
    ;

specialAssignTarget
    : OUT
    ;

AIM     : 'AIM';
SHOT    : 'SHOT';
RADAR   : 'RADAR';
DAMAGE  : 'DAMAGE';
SPEEDX  : 'SPEEDX';
SPEEDY  : 'SPEEDY';
RANDOM  : 'RANDOM';
X       : 'X';
Y       : 'Y';
ABS     : 'abs';
NOT     : 'not';
TAN     : 'tan';
SIN     : 'sin';
COS     : 'cos';
ATAN    : 'atan';
ASIN    : 'asin';
ACOS    : 'acos';
SQRT    : 'sqrt';
TRUNC   : 'trunc';

OUT     : 'OUT';

ID
    : ('a' .. 'z' | 'A' .. 'Z' | '_') ('a' .. 'z' | 'A' .. 'Z' | '_' | '0' .. '9') *
    ;

number
    : ('+' | '-')? NUMBER
    ;

comment
    : COMMENT
    ;

NUMBER
    : [0-9]+ ('.' [0-9]+)?
    ;

COMMENT
    : '//' ~ [\r\n]*
    ;

EOL
    : [\r\n] +
    ;

WS
    : [ \t] -> skip
    ;
