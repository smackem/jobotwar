grammar JobotwarV2;

program
    : (COMMENT | declaration)*
    ;

declaration
    : 'def' ID ('=' number)? (',' ID)*
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
    : '//' ~ [\r\n]*
    ;

EOL
    : [\r\n] +
    ;

WS
    : [ \t] -> skip
    ;
