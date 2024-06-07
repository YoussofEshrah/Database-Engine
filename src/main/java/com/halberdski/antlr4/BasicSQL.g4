grammar BasicSQL;

sqlStatement: selectStatement | insertStatement | createTableStatement | createIndexStatement | deleteStatement| updateStatement;

selectStatement: SELECT STAR FROM tableName WHERE selectCondition SEMICOLON EOF;
insertStatement: INSERT INTO tableName columnNameList VALUES valueList SEMICOLON EOF;

createTableStatement: CREATE TABLE tableName tableDefinition SEMICOLON EOF;

createIndexStatement: CREATE INDEX indexName ON tableName '(' columnName ')' SEMICOLON EOF;

deleteStatement: DELETE FROM tableName deleteCondition SEMICOLON EOF;

updateStatement: UPDATE tableName SET updateSetClause updateWhereClause SEMICOLON EOF;

updateSetClause: condition (',' condition)*;

updateWhereClause: WHERE condition;

tableDefinition: '(' columnDefinition (',' columnDefinition)* ')';

columnConstraint: PRIMARYKEY;

columnName: ID;

literalValue: STRING | NUMBER ;
columnNameList: '(' columnName (',' columnName)* ')';
valueList: '(' literalValue (',' literalValue)* ')';
tableName: ID;
condition: ID OPERATOR (STRING | NUMBER);
deleteCondition: (WHERE condition (LOGICAL_OPERATOR condition)*)?;

selectCondition: condition (LOGICAL_OPERATOR condition)*;


columnDefinition: columnName dataType (columnConstraint)?;

dataType: INTTYPE | FLOATTYPE | VARCHARTYPE;

indexName: ID;



// -------------------------------------------------------------------

SEMICOLON: ';';

SELECT: 'SELECT' | 'select';
STAR: '*';
FROM: 'FROM' | 'from';
WHERE: 'WHERE' | 'where';

INSERT: 'INSERT' | 'insert';
INTO: 'INTO' | 'into';
VALUES: 'VALUES' | 'values';

LOGICAL_OPERATOR: AND | OR | XOR;
AND: 'AND' | 'and';
OR: 'OR' | 'or';
XOR: 'XOR' | 'xor';


CREATE: 'CREATE' | 'create';

TABLE: 'TABLE' | 'table';

INDEX: 'INDEX' | 'index';
ON: 'ON' | 'on';

DELETE: 'DELETE' | 'delete';

UPDATE: 'UPDATE' | 'update';

SET: 'SET' | 'set';

INTTYPE: 'INT' | 'int';
FLOATTYPE: 'FLOAT' | 'float';
VARCHARTYPE: 'VARCHAR' | 'varchar';

PRIMARYKEY: 'PRIMARY KEY' | 'primary key';

BOOLEAN: 'TRUE' | 'FALSE' | 'true' | 'false' ;

ID: [a-zA-Z][a-zA-Z0-9_]*;

STRING: '\''ID'\'';

NUMBER: INT | DECIMAL;

INT: DIGIT+;

DOT: '.';
DECIMAL: DIGIT+ DOT DIGIT+;

OPERATOR: EQUAL | '<' | '>' | '<=' | '>=' | '!=';
EQUAL: '=';

WS : [ \t\r\n]+ -> skip;
DIGIT: [0-9];

