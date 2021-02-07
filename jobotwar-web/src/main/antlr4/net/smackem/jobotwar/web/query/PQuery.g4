grammar PQuery;

query
    : orCondition
    ;

orCondition
    : andCondition (Or andCondition)*
    ;

andCondition
    : condition (And condition)*
    ;

condition
    : Not? comparison
    ;

comparison
    : atom comparator atom
    | LParen orCondition RParen
    ;

comparator
    : Lt
    | Le
    | Gt
    | Ge
    | Eq
    | Ne
    | Match
    ;

atom
    : Ident
    | number
    | String
    ;

number
    : (Plus | Minus)? (Integer | Real)
    ;

Plus    : '+';
Minus   : '-';
Not     : 'not';
Or      : 'or';
And     : 'and';
Lt      : 'lt';
Le      : 'le';
Gt      : 'gt';
Ge      : 'ge';
Eq      : 'eq';
Ne      : 'ne';
Match   : 'match';
LParen  : '(';
RParen  : ')';

Integer
    : [0-9]+
    ;

Real
    : [0-9]+ '.' [0-9]+
    ;

Ident
    : ('a' .. 'z' | 'A' .. 'Z' | '_') ('a' .. 'z' | 'A' .. 'Z' | '_' | '0' .. '9') *
    ;

String
    : '\'' .*? '\''
    ;

Ws
    : [ \t\r\n] -> skip
    ;
