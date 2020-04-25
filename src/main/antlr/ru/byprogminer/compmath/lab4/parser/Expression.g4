grammar Expression;

@header {
    package ru.byprogminer.compmath.lab4.parser;
}

// Tokens

VARIABLE: [a-z];
NUMBER: [0-9]+('.'[0-9]+)?;
WHITESPACE: [ \t\n\r]+ -> skip;

// Rules

expression
    : expr EOF
;

expr
    : '(' expr ')' #exprBraces
    | '|' expr '|' #exprAbs
    | <assoc=right> left=expr '^' right=expr #exprPower
    | op='-' expr #exprUnaryMinus
    | function expr #exprFunction
    | left=expr op=('*'|'/') right=expr #exprMultiplyDivide
    | left=expr op=('+'|'-') right=expr #exprPlusMinus
    | variable #exprVariable
    | NUMBER #exprNumber
;

function
    : name='sqrt'
    | name='sin'
    | name='cos'
    | name='tan'
    | name='asin'
    | name='acos'
    | name='atan'
    | name='log' ('_' base=expr)?
    | name='ln'
    | name='lg'
;

variable
    : VARIABLE ('_' index=NUMBER)*
;
