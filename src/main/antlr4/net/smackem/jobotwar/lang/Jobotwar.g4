/*
 [The "BSD licence"]
 Copyright (c) 2013 Tom Everett
 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions
 are met:
 1. Redistributions of source code must retain the above copyright
    notice, this list of conditions and the following disclaimer.
 2. Redistributions in binary form must reproduce the above copyright
    notice, this list of conditions and the following disclaimer in the
    documentation and/or other materials provided with the distribution.
 3. The name of the author may not be used to endorse or promote products
    derived from this software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

grammar Jobotwar;

program
    : declLine* line+
    ;

declLine
    : (comment | declaration)? EOL
    ;

declaration
    : 'def' ID
    ;

line
    : (label | comment | statement)? EOL
    ;

label
    : ID ':'
    ;

statement
    : (assignStatement | gotoStatement) (ifClause | unlessClause)?
    ;

gotoStatement
    : 'goto' ID
    ;

assignStatement
    : term ('->' assignTarget)+
    ;

assignTarget
    : register
    | ID
    ;

ifClause
    : 'if' condition
    ;

unlessClause
    : 'unless' condition
    ;

condition
    : comparison conditionOperator condition
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
    : product termOperator term
    | product
    ;

termOperator
    : '+'
    | '-'
    ;

product
    : atom productOperator product
    | atom
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

atom
    : number
    | register
    | ID
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

AIM : 'AIM';
SHOT : 'SHOT';
RADAR : 'RADAR';
DAMAGE : 'DAMAGE';
SPEEDX : 'SPEEDX';
SPEEDY : 'SPEEDY';
RANDOM : 'RANDOM';
X : 'X';
Y : 'Y';

DOT : '.';
COMMA : ',';

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
