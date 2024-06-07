package com.halberdski.antlr4;

// Generated from BasicSQL.g4 by ANTLR 4.13.1
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link BasicSQLParser}.
 */
public interface BasicSQLListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link BasicSQLParser#sqlStatement}.
	 * @param ctx the parse tree
	 */
	void enterSqlStatement(BasicSQLParser.SqlStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link BasicSQLParser#sqlStatement}.
	 * @param ctx the parse tree
	 */
	void exitSqlStatement(BasicSQLParser.SqlStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link BasicSQLParser#selectStatement}.
	 * @param ctx the parse tree
	 */
	void enterSelectStatement(BasicSQLParser.SelectStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link BasicSQLParser#selectStatement}.
	 * @param ctx the parse tree
	 */
	void exitSelectStatement(BasicSQLParser.SelectStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link BasicSQLParser#insertStatement}.
	 * @param ctx the parse tree
	 */
	void enterInsertStatement(BasicSQLParser.InsertStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link BasicSQLParser#insertStatement}.
	 * @param ctx the parse tree
	 */
	void exitInsertStatement(BasicSQLParser.InsertStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link BasicSQLParser#createTableStatement}.
	 * @param ctx the parse tree
	 */
	void enterCreateTableStatement(BasicSQLParser.CreateTableStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link BasicSQLParser#createTableStatement}.
	 * @param ctx the parse tree
	 */
	void exitCreateTableStatement(BasicSQLParser.CreateTableStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link BasicSQLParser#createIndexStatement}.
	 * @param ctx the parse tree
	 */
	void enterCreateIndexStatement(BasicSQLParser.CreateIndexStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link BasicSQLParser#createIndexStatement}.
	 * @param ctx the parse tree
	 */
	void exitCreateIndexStatement(BasicSQLParser.CreateIndexStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link BasicSQLParser#deleteStatement}.
	 * @param ctx the parse tree
	 */
	void enterDeleteStatement(BasicSQLParser.DeleteStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link BasicSQLParser#deleteStatement}.
	 * @param ctx the parse tree
	 */
	void exitDeleteStatement(BasicSQLParser.DeleteStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link BasicSQLParser#updateStatement}.
	 * @param ctx the parse tree
	 */
	void enterUpdateStatement(BasicSQLParser.UpdateStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link BasicSQLParser#updateStatement}.
	 * @param ctx the parse tree
	 */
	void exitUpdateStatement(BasicSQLParser.UpdateStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link BasicSQLParser#updateSetClause}.
	 * @param ctx the parse tree
	 */
	void enterUpdateSetClause(BasicSQLParser.UpdateSetClauseContext ctx);
	/**
	 * Exit a parse tree produced by {@link BasicSQLParser#updateSetClause}.
	 * @param ctx the parse tree
	 */
	void exitUpdateSetClause(BasicSQLParser.UpdateSetClauseContext ctx);
	/**
	 * Enter a parse tree produced by {@link BasicSQLParser#updateWhereClause}.
	 * @param ctx the parse tree
	 */
	void enterUpdateWhereClause(BasicSQLParser.UpdateWhereClauseContext ctx);
	/**
	 * Exit a parse tree produced by {@link BasicSQLParser#updateWhereClause}.
	 * @param ctx the parse tree
	 */
	void exitUpdateWhereClause(BasicSQLParser.UpdateWhereClauseContext ctx);
	/**
	 * Enter a parse tree produced by {@link BasicSQLParser#tableDefinition}.
	 * @param ctx the parse tree
	 */
	void enterTableDefinition(BasicSQLParser.TableDefinitionContext ctx);
	/**
	 * Exit a parse tree produced by {@link BasicSQLParser#tableDefinition}.
	 * @param ctx the parse tree
	 */
	void exitTableDefinition(BasicSQLParser.TableDefinitionContext ctx);
	/**
	 * Enter a parse tree produced by {@link BasicSQLParser#columnConstraint}.
	 * @param ctx the parse tree
	 */
	void enterColumnConstraint(BasicSQLParser.ColumnConstraintContext ctx);
	/**
	 * Exit a parse tree produced by {@link BasicSQLParser#columnConstraint}.
	 * @param ctx the parse tree
	 */
	void exitColumnConstraint(BasicSQLParser.ColumnConstraintContext ctx);
	/**
	 * Enter a parse tree produced by {@link BasicSQLParser#columnName}.
	 * @param ctx the parse tree
	 */
	void enterColumnName(BasicSQLParser.ColumnNameContext ctx);
	/**
	 * Exit a parse tree produced by {@link BasicSQLParser#columnName}.
	 * @param ctx the parse tree
	 */
	void exitColumnName(BasicSQLParser.ColumnNameContext ctx);
	/**
	 * Enter a parse tree produced by {@link BasicSQLParser#literalValue}.
	 * @param ctx the parse tree
	 */
	void enterLiteralValue(BasicSQLParser.LiteralValueContext ctx);
	/**
	 * Exit a parse tree produced by {@link BasicSQLParser#literalValue}.
	 * @param ctx the parse tree
	 */
	void exitLiteralValue(BasicSQLParser.LiteralValueContext ctx);
	/**
	 * Enter a parse tree produced by {@link BasicSQLParser#columnNameList}.
	 * @param ctx the parse tree
	 */
	void enterColumnNameList(BasicSQLParser.ColumnNameListContext ctx);
	/**
	 * Exit a parse tree produced by {@link BasicSQLParser#columnNameList}.
	 * @param ctx the parse tree
	 */
	void exitColumnNameList(BasicSQLParser.ColumnNameListContext ctx);
	/**
	 * Enter a parse tree produced by {@link BasicSQLParser#valueList}.
	 * @param ctx the parse tree
	 */
	void enterValueList(BasicSQLParser.ValueListContext ctx);
	/**
	 * Exit a parse tree produced by {@link BasicSQLParser#valueList}.
	 * @param ctx the parse tree
	 */
	void exitValueList(BasicSQLParser.ValueListContext ctx);
	/**
	 * Enter a parse tree produced by {@link BasicSQLParser#tableName}.
	 * @param ctx the parse tree
	 */
	void enterTableName(BasicSQLParser.TableNameContext ctx);
	/**
	 * Exit a parse tree produced by {@link BasicSQLParser#tableName}.
	 * @param ctx the parse tree
	 */
	void exitTableName(BasicSQLParser.TableNameContext ctx);
	/**
	 * Enter a parse tree produced by {@link BasicSQLParser#condition}.
	 * @param ctx the parse tree
	 */
	void enterCondition(BasicSQLParser.ConditionContext ctx);
	/**
	 * Exit a parse tree produced by {@link BasicSQLParser#condition}.
	 * @param ctx the parse tree
	 */
	void exitCondition(BasicSQLParser.ConditionContext ctx);
	/**
	 * Enter a parse tree produced by {@link BasicSQLParser#deleteCondition}.
	 * @param ctx the parse tree
	 */
	void enterDeleteCondition(BasicSQLParser.DeleteConditionContext ctx);
	/**
	 * Exit a parse tree produced by {@link BasicSQLParser#deleteCondition}.
	 * @param ctx the parse tree
	 */
	void exitDeleteCondition(BasicSQLParser.DeleteConditionContext ctx);
	/**
	 * Enter a parse tree produced by {@link BasicSQLParser#selectCondition}.
	 * @param ctx the parse tree
	 */
	void enterSelectCondition(BasicSQLParser.SelectConditionContext ctx);
	/**
	 * Exit a parse tree produced by {@link BasicSQLParser#selectCondition}.
	 * @param ctx the parse tree
	 */
	void exitSelectCondition(BasicSQLParser.SelectConditionContext ctx);
	/**
	 * Enter a parse tree produced by {@link BasicSQLParser#columnDefinition}.
	 * @param ctx the parse tree
	 */
	void enterColumnDefinition(BasicSQLParser.ColumnDefinitionContext ctx);
	/**
	 * Exit a parse tree produced by {@link BasicSQLParser#columnDefinition}.
	 * @param ctx the parse tree
	 */
	void exitColumnDefinition(BasicSQLParser.ColumnDefinitionContext ctx);
	/**
	 * Enter a parse tree produced by {@link BasicSQLParser#dataType}.
	 * @param ctx the parse tree
	 */
	void enterDataType(BasicSQLParser.DataTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link BasicSQLParser#dataType}.
	 * @param ctx the parse tree
	 */
	void exitDataType(BasicSQLParser.DataTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link BasicSQLParser#indexName}.
	 * @param ctx the parse tree
	 */
	void enterIndexName(BasicSQLParser.IndexNameContext ctx);
	/**
	 * Exit a parse tree produced by {@link BasicSQLParser#indexName}.
	 * @param ctx the parse tree
	 */
	void exitIndexName(BasicSQLParser.IndexNameContext ctx);
}