package com.halberdski.engine;

/** * @author Wael Abouelsaadat */

public class SQLTerm {

	public String _strTableName, _strColumnName, _strOperator;
	public Object _objValue;

	public SQLTerm() {

	}

	public SQLTerm(String _strTableName, String _strColumnName, String _strOperator, Object _objValue) {
		this._strTableName = _strTableName;
		this._strColumnName = _strColumnName;
		this._strOperator = _strOperator;
		this._objValue = _objValue;
	}

	@Override
	public String toString() {
		return "SQLTerm [_strTableName:" + _strTableName + ", _strColumnName:" + _strColumnName + ", _strOperator:"
				+ _strOperator + ", _objValue:" + _objValue + "]";

	}

	// public class SQLTerm {
	// private String tableName;
	// private String columnName;
	// private String operator;
	// private Object value;

	// public SQLTerm(String columnName, String operator, Object value) {
	// this.columnName = columnName;
	// this.operator = operator;
	// this.value = value;
	// }

	// public String getColumnName() {
	// return columnName;
	// }

	// public void setColumnName(String columnName) {
	// this.columnName = columnName;
	// }

	// public String getOperator() {
	// return operator;
	// }

	// public void setOperator(String operator) {
	// this.operator = operator;
	// }

	// public String getTableName() {
	// return operator;
	// }

	// public void setTableName(String operator) {
	// this.operator = operator;
	// }

	// public Object getValue() {
	// return value;
	// }

	// public void setValue(Object value) {
	// this.value = value;
	// }
	// }
}