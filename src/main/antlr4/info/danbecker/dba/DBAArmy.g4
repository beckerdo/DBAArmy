/** Grammars always start with a grammar header.
 *  Grammar name Blah must match the filename: Blah.g4 */
grammar DBAArmy;

// Parser rules are lowercase.
exprs: expr (LIST_DELIM expr)*
    ;

expr:
    // ANTLR4 matches in order top to bottom. Put more atomic higher precedence higher.
    // ANTLR4 only supports direct left recursion. You can label alternatives, but deeper LR rule trees not allowed.
    type                            #exprType
    | GROUP_OPEN expr GROUP_CLOSE   #exprGroup
    | expr DISMOUNT_DELIM expr      #exprDismount
    | expr AND_DELIM expr           #exprAnd   // bi-op, very shallow balanced trees
    // | expr (AND_DELIM expr)+     #exprAnd // tail recursion, very deep trail trees
    | INTEGER EITHER_DELIM type     #exprEitherUnit
    | expr EITHER_DELIM expr        #exprEither
    | expr OR_DELIM expr            #exprOr // bi-op, very shallow balanced trees
    // | expr (OR_DELIM expr)+      #exprOr // tail recursion, very deep trail trees
    | INTEGER MULTIPLE_DELIM expr   #exprMultiple
    ;

type:
    general |
    elephants |
    knights |
    cavalry |
    light_horse |
    scythed_chariots |
    camelry |
    mounted_infantry |
    spears |
    pikes |
    blades |
    auxilia |
    bows |
    psiloi |
    warband |
    hordes |
    artillery |
    war_wagons;

general: 'CP' | 'Lit' | 'CWg' | 'Gen';
elephants: 'El';
knights: '3Kn' | '4Kn' | '6Kn' | 'Kn' | 'HCh';
cavalry: 'Cv' | '6Cv' | 'LCh';
light_horse: 'LH' | 'LCm';
scythed_chariots: 'SCh';
camelry: 'Cm';
mounted_infantry: 'Mtd-X';
spears: '8Sp' | 'Sp';
pikes: '4Pk' | '3Pk' | 'Pk';
blades: '4Bd' | '3Bd' | '6Bd' | 'Bd';
auxilia: '4Ax' | '3Ax' |'Ax';
bows: bow | crossbow | lightbow;
bow: '4Bw' | '3Bw' | '8Bw' | 'Bw';
crossbow: '4Cb' | '3Cb' | '8Cb' | 'Cb';
lightbow: '4Lb' | '3Lb' | '8Lb' | 'Lb';
psiloi: 'Ps';
warband: '4Wb' | '3Wb' | 'Wb';
hordes: '7Hd' | '5Hd' | 'Hd';
artillery: 'Art';
war_wagons: 'WWg';

// Lexer rules are uppercase.
LIST_DELIM: ',';
MULTIPLE_DELIM: 'x';
DISMOUNT_DELIM: '//';
EITHER_DELIM: '/';
OR_DELIM: 'or';
AND_DELIM: '+';
GROUP_OPEN: '(';
GROUP_CLOSE: ')';

INTEGER:    [0-9]+;
WS:      [ \r\n\t]+ -> skip; // toss out whitespace