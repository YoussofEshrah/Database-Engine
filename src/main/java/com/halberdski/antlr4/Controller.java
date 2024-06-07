package com.halberdski.antlr4;

import java.util.Arrays;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import org.antlr.v4.runtime.tree.ParseTree;

import com.halberdski.engine.DBApp;
import com.halberdski.engine.SQLTerm;
import com.halberdski.engine.utility;
import com.halberdski.exceptions.DBAppException;

public class Controller extends BasicSQLBaseListener {

    @SuppressWarnings("rawtypes")
    @Override
    public void enterSelectStatement(BasicSQLParser.SelectStatementContext ctx) {
        System.out.println("Entering selectStatement: " + ctx.toStringTree());

        String tableName = ctx.tableName().getText();

        // First, count the actual conditions (excluding logical operators)
        int conditionCount = 0;
        for (int i = 0; i < ctx.selectCondition().getChildCount(); i++) {
            if (ctx.selectCondition().getChild(i).getChildCount() > 0) {
                conditionCount++;
            }
        }

        SQLTerm[] array = new SQLTerm[conditionCount];
        int arrayPointer = 0;

        Vector<String> strValueOperators = new Vector<>();

        for (int i = 0; i < ctx.selectCondition().getChildCount(); i++) {
            ParseTree child = ctx.selectCondition().getChild(i);

            if (child.getChildCount() > 0) { // Condition
                System.out.println("COLUMN: " + child.getText());

                SQLTerm term = new SQLTerm();

                String columnName = child.getChild(0).getText();
                String operator = child.getChild(1).getText();
                String colValueString = child.getChild(2).getText();

                Object colValue = null;

                if (colValueString.contains("'")) {
                    colValue = colValueString.substring(1, colValueString.length() - 1);
                } else {
                    try {
                        colValue = Integer.parseInt(colValueString);
                    } catch (NumberFormatException e) {
                        try {
                            colValue = Double.parseDouble(colValueString);
                        } catch (NumberFormatException nfe) {
                            // Not a number, ignore
                        }
                    }
                }

                term._strTableName = tableName;
                term._strColumnName = columnName;
                term._strOperator = operator;
                term._objValue = colValue;
                array[arrayPointer++] = term;

            } else { // Logical operator
                strValueOperators.add(child.getText().toUpperCase());
            }
        }

        System.out.println("Conditions: " + Arrays.toString(array));
        System.out.println("Logical Operators: " + strValueOperators);

        try {
            DBApp dbApp = new DBApp();
            dbApp.it = dbApp.selectFromTable(array, strValueOperators.toArray(new String[0]));
        } catch (DBAppException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public void enterInsertStatement(BasicSQLParser.InsertStatementContext ctx) throws RuntimeException {
        // System.out.println("Entering insertStatement: " + ctx.toStringTree());
        String tableName = ctx.tableName().getText();

        Vector<String> columnNames = new Vector<String>();

        for (int i = 0; i < ctx.columnNameList().getChildCount(); i++) {
            if (ctx.columnNameList().getChild(i).getChildCount() > 0) {
                columnNames.add(ctx.columnNameList().getChild(i).getText());
            }
        }

        Vector<Object> columnValues = new Vector<Object>();

        for (int i = 0; i < ctx.valueList().getChildCount(); i++) {
            ParseTree child = ctx.valueList().getChild(i);

            if (child.getChildCount() > 0) {
                String subChild = child.getChild(0).getText();

                // System.out.println(subChild);

                if (subChild.contains("'")) {
                    columnValues.add(subChild.substring(1, subChild.length() - 1));
                    continue;
                }
                try {
                    Integer integer = Integer.parseInt(subChild);
                    columnValues.add(integer);
                    continue;
                } catch (Exception e) {
                    // TODO: handle exception
                }
                try {
                    Double doubleVal = Double.parseDouble(subChild);
                    columnValues.add(doubleVal);
                    continue;
                } catch (Exception e) {
                    // TODO: handle exception
                }
            }
        }

        if (columnNames.size() != columnValues.size()) {
            throw new RuntimeException("Column Names and Values do not match");
        }

        try {
            Hashtable<String, Object> colNameVal = new Hashtable<>();

            for (int i = 0; i < columnNames.size(); i++) {
                colNameVal.put(columnNames.get(i), columnValues.get(i));
            }

            DBApp dbApp = new DBApp();
            dbApp.insertIntoTable(tableName, colNameVal);
        } catch (DBAppException e) {
            throw new RuntimeException(e.getMessage());
        }

        // System.out.println("Table Name: " + tableName);
        // System.out.println("Column Names: " + columnNames);
        // System.out.println("Column Values: " + columnValues);

    }

    @Override
    public void enterCreateTableStatement(BasicSQLParser.CreateTableStatementContext ctx) {
        String tableName = ctx.tableName().getText(); // Get the table name
        Hashtable<String, String> colNameType = new Hashtable<String, String>();
        String primaryKeyColumn = null;

        // System.out.println(ctx.toStringTree());

        for (int i = 0; i < ctx.tableDefinition().getChildCount(); i++) {
            if (ctx.tableDefinition().getChild(i).getChildCount() > 0) {
                ParseTree columnEntry = ctx.tableDefinition().getChild(i);
                // System.out.println(ctx.tableDefinition().getChild(i).getText());

                String columnName = columnEntry.getChild(0).getText();
                String dataType = columnEntry.getChild(1).getText();

                if (dataType.toLowerCase().equals("int")) {
                    colNameType.put(columnName, "java.lang.Integer");
                } else if (dataType.toLowerCase().equals("varchar")) {
                    colNameType.put(columnName, "java.lang.String");
                } else if (dataType.toLowerCase().equals("float")) {
                    colNameType.put(columnName, "java.lang.Double");
                } else {
                    throw new RuntimeException("Unsupported Data Type");
                }

                if (columnEntry.getChildCount() > 2) {
                    if (primaryKeyColumn == null) {
                        primaryKeyColumn = columnName;
                    } else {
                        throw new RuntimeException("Multiple Primary Keys");
                    }
                }
            }
        }
        // System.out.println(tableName);
        // System.out.println(colNameType);
        // System.out.println(primaryKeyColumn);

        try {
            DBApp dbApp = new DBApp();
            dbApp.createTable(tableName, primaryKeyColumn, colNameType);
        } catch (DBAppException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public void enterCreateIndexStatement(BasicSQLParser.CreateIndexStatementContext ctx) {
        // System.out.println("Entering CreateIndexStatement: " + ctx.toStringTree());

        String indexName = ctx.indexName().getText();
        String tableName = ctx.tableName().getText();
        Vector<String> columnNames = new Vector<String>();

        System.out.println(ctx.columnName().getChild(0).getText());
        columnNames.add(ctx.columnName().getChild(0).getText());

        try {

            DBApp dbApp = new DBApp();
            for (int i = 0; i < columnNames.size(); i++) {
                dbApp.createIndex(tableName, columnNames.get(i), indexName);
            }
        } catch (DBAppException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    // delete
    @Override
    public void enterDeleteStatement(BasicSQLParser.DeleteStatementContext ctx) {
        // System.out.println("Entering DeleteStatement: " + ctx.toStringTree());
        String tableName = ctx.tableName().getText();

        Hashtable<String, Object> colNameValue = new Hashtable<String, Object>();

        for (int i = 0; i < ctx.deleteCondition().getChildCount(); i++) {
            if (ctx.deleteCondition().getChild(i).getChildCount() > 0) {
                String columnName = ctx.deleteCondition().getChild(i).getChild(0).getText();
                String strColumnValue = ctx.deleteCondition().getChild(i).getChild(2).getText();

                // System.out.println(strColumnValue);

                if (strColumnValue.contains("'")) {
                    colNameValue.put(columnName, strColumnValue.substring(1, strColumnValue.length() - 1));
                    continue;
                }
                try {
                    Integer integer = Integer.parseInt(strColumnValue);
                    colNameValue.put(columnName, integer);
                    continue;
                } catch (Exception e) {
                    // TODO: handle exception
                }
                try {
                    Double doubleVal = Double.parseDouble(strColumnValue);
                    colNameValue.put(columnName, doubleVal);
                    continue;
                } catch (Exception e) {
                    // TODO: handle exception
                }
            }
        }

        try {
            DBApp dbApp = new DBApp();
            dbApp.deleteFromTable(tableName, colNameValue);
        } catch (DBAppException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public void enterUpdateStatement(BasicSQLParser.UpdateStatementContext ctx) {
        // Get the table name
        String tableName = ctx.tableName().getText();

        // Hashtable for column-value pairs to set (SET clause)
        Hashtable<String, Object> setClause = new Hashtable<>();

        System.out.println(ctx.updateSetClause().getText());

        for (int i = 0; i < ctx.updateSetClause().getChildCount(); i++) {
            ParseTree setChild = ctx.updateSetClause().getChild(i);

            if (setChild.getChildCount() > 0) {

                // System.out.println(setChild.getText());
                String[] array = setChild.getText().split("=");

                String columnName = array[0].trim();
                String strValue = array[1].trim();
                Object value = null;

                // Handle different data types
                if (strValue.contains("'")) {
                    value = strValue.substring(1, strValue.length() - 1); // Remove quotes
                } else {
                    try {
                        value = Integer.parseInt(strValue);
                    } catch (NumberFormatException e) {
                        try {
                            value = Double.parseDouble(strValue);
                        } catch (NumberFormatException e2) {
                            value = strValue; // Default to the raw string value if it's neither int nor double
                        }
                    }

                }
                setClause.put(columnName, value);
            }
        }
        String primaryKeyString = ctx.updateWhereClause().getChild(1).getChild(2).getText().replace("'", "");
        System.out.println("Primary Key: " + primaryKeyString);

        try {
            // Pass the table name, SET clause, and WHERE clause to your DBApp's update
            // method
            DBApp dbApp = new DBApp();
            dbApp.updateTable(tableName, primaryKeyString, setClause);
        } catch (DBAppException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public static void main(String[] args) {
        utility.clearDatabaseSystem();
        // QueryTester.testQuery("CREATE INDEX ta ON tableName (column1);");

        // QueryTester.testQuery("insert into mytable (name, age) values ('mo', 5);");
        QueryTester.testQuery("CREATE TABLE moski (age INT, name VARCHAR primary key);");

        QueryTester.testQuery("CREATE INDEX idx_age ON moski age;");

        // QueryTester.testQuery("DELETE FROM moski WHERE name = 'moski' AND age =
        // 10;");

        QueryTester.testQuery("UPDATE moski SET age = 15 WHERE name = '5';");
        // QueryTester.testQuery("select * from moski where name = 'moski' and age =
        // 5;");

    }
}
