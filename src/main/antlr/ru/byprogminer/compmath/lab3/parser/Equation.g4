grammar Equation;

@header {
    package ru.byprogminer.compmath.lab3.parser;
}

// Tokens

VARIABLE: [a-z];
NUMBER: '-'?[0-9]+('.'[0-9]+)?;
WHITESPACE: [ \t\n\r]+ -> skip;

// Rules

equation
    : right=expr '=' left=expr EOF
;

expr
    : '(' expr ')' #exprBraces
    | '|' expr '|' #exprAbs
    | op='-' expr #exprUnaryMinus
    | <assoc=right> left=expr '^' right=expr #exprPower
    | left=expr op=('*'|'/') right=expr #exprMultiplyDivide
    | left=expr op=('+'|'-') right=expr #exprPlusMinus
    | op='sqrt' expr #exprFunction
    | op='sin' expr #exprFunction
    | op='cos' expr #exprFunction
    | op='tan' expr #exprFunction
    | op='asin' expr #exprFunction
    | op='acos' expr #exprFunction
    | op='atan' expr #exprFunction
    | 'log' '_' base=expr expr #exprLog
    | op='log' expr #exprFunction
    | op='ln' expr #exprFunction
    | op='lg' expr #exprFunction
    | variable #exprVariable
    | NUMBER #exprNumber
;

variable
    : VARIABLE ('_' index=NUMBER)*
;
