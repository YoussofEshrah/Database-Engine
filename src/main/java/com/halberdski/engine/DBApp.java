package com.halberdski.engine;

/** * @author Wael Abouelsaadat */

import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import com.halberdski.model.Metadata;
import com.halberdski.model.Table;
import com.halberdski.antlr4.BasicSQLLexer;
import com.halberdski.antlr4.BasicSQLParser;
import com.halberdski.antlr4.CustomErrorListener;
import com.halberdski.antlr4.Controller;
import com.halberdski.exceptions.DBAppException;
import java.util.Arrays;
import java.util.Hashtable;

public class DBApp {
	public static Iterator it;

	public DBApp() {
		init();
	}

	// this does whatever initialization you would like
	// or leave it empty if there is no code you want to
	// execute at application startup
	public void init() {
	}

	// following method creates one table only
	// strClusteringKeyColumn is the name of the column that will be the primary
	// key and the clustering column as well. The data type of that column will
	// be passed in htblColNameType
	// htblColNameValue will have the column name as key and the data
	// type as value
	public void createTable(String strTableName, String strClusteringKeyColumn,
			Hashtable<String, String> htblColNameType) throws DBAppException {

		if (strTableName.trim().length() == 0) {
			throw new DBAppException("Table name cannot be empty");
		}
		if (strClusteringKeyColumn.trim().length() == 0) {
			throw new DBAppException("Primary key column cannot be empty");
		}
		if (htblColNameType.size() == 0) {
			throw new DBAppException("Table must have at least one column");
		}

		for (Map.Entry<String, String> entry : htblColNameType.entrySet()) {
			if (entry.getKey().trim().length() == 0) {
				throw new DBAppException("Column name cannot be empty");
			}
			if (entry.getValue().trim().length() == 0) {
				throw new DBAppException("Column type cannot be empty");
			}
			if (!(entry.getValue().equals("java.lang.Integer") || entry.getValue().equals("java.lang.Double")
					|| entry.getValue().equals("java.lang.String"))) {
				throw new DBAppException("Column type must be either Integer, Double, or String");
			}
		}

		if (Metadata.tableExists(strTableName)) {
			throw new DBAppException("Table " + strTableName + " already exists!");
		}
		if (!htblColNameType.containsKey(strClusteringKeyColumn)) {
			throw new DBAppException("Hashtable must include primary key!");
		}

		if (strClusteringKeyColumn.trim().length() == 0) {
			throw new DBAppException("You must specify a primary key!");
		}
		if (htblColNameType.isEmpty()) {
			throw new DBAppException("Table columns can not be empty");
		}

		Table myTable = new Table(strTableName, strClusteringKeyColumn, htblColNameType);
		myTable.saveTable();
		Metadata.addTable(strTableName, strClusteringKeyColumn, htblColNameType);
		System.out.println("Created Table: " + strTableName);
	}

	// following method creates a B+tree index
	public void createIndex(String strTableName,
			String strColName,
			String strIndexName) throws DBAppException {

		if (strTableName.trim().length() == 0) {
			throw new DBAppException("Table name cannot be empty");
		}
		if (strColName.trim().length() == 0) {
			throw new DBAppException("Column name cannot be empty");
		}
		if (strIndexName.trim().length() == 0) {
			throw new DBAppException("Index name cannot be empty");
		}

		if (strIndexName.toLowerCase().equals("null")) {
			throw new DBAppException("Index name cannot be null");
		}

		if (!Metadata.tableExists(strTableName)) {
			throw new DBAppException("Table " + strTableName + " does not exist!");

		}
		if (!Metadata.tableHasColumn(strTableName, strColName)) {
			throw new DBAppException("Column " + strColName + " does not exist in Table " + strTableName);
		}
		if (Metadata.tableHasIndexOnColumn(strTableName, strColName)) {
			throw new DBAppException("Column " + strColName + " in Table " + strTableName + " is already indexed");

		}
		if (Metadata.indexExists(strIndexName)) {
			throw new DBAppException("Index " + strIndexName + " already exists in Database ");
		}

		Metadata.addIndex(strTableName, strColName, strIndexName);

		Table table = Table.loadTable(strTableName);

		table.createIndex(strColName, strIndexName);

		System.out
				.println("Index (" + strIndexName + ") created on Table (" + strTableName + ") on Column (" + strColName
						+ ")");
	}

	// following method inserts one row only.
	// htblColNameValue must include a value for the primary key
	// DONE
	public void insertIntoTable(String strTableName,
			Hashtable<String, Object> htblColNameValue) throws DBAppException {

		if (strTableName.trim().length() == 0) {
			throw new DBAppException("Table name cannot be empty");
		}

		if (!Metadata.tableExists(strTableName)) {
			throw new DBAppException("Table " + strTableName + " does not exist!");
		}
		if (!Metadata.validInsert(strTableName, htblColNameValue)) {
			throw new DBAppException("Not the correct format for insert!");
		}

		Table table = Table.loadTable(strTableName);
		table.insert(htblColNameValue);

		System.out.println("Inserted into Table: " + strTableName);
	}

	// following method updates one row only
	// htblColNameValue holds the key and new value
	// htblColNameValue will not include clustering key as column name
	// strClusteringKeyValue is the value to look for to find the row to update.
	// DONE
	public void updateTable(String strTableName,
			String strClusteringKeyValue,
			Hashtable<String, Object> htblColNameValue) throws DBAppException {

		if (strTableName.trim().length() == 0) {
			throw new DBAppException("Primary key value cannot be empty");
		}
		if (strClusteringKeyValue.trim().length() == 0) {
			throw new DBAppException("Primary key value cannot be empty");
		}

		if (!Metadata.tableExists(strTableName)) {
			throw new DBAppException("Table " + strTableName + " does not exist!");
		}
		if (htblColNameValue.isEmpty()) {
			throw new DBAppException("No columns to update");
		}

		if (htblColNameValue.containsKey(Metadata.getPrimaryKeyName(strTableName))) {
			throw new DBAppException("Primary key cannot be updated");
		}

		// Check if primary key value violates metadata
		String primaryKeyName = Metadata.getPrimaryKeyName(strTableName);
		String primaryKeyType = Metadata.getColumnType(strTableName, primaryKeyName);

		Object primaryKeyValue = null;

		switch (primaryKeyType) {
			case "java.lang.Integer":
				try {
					primaryKeyValue = Integer.parseInt(strClusteringKeyValue);
				} catch (NumberFormatException e) {
					throw new DBAppException("Primary key value must be an integer");
				}
				break;
			case "java.lang.Double":
				try {
					primaryKeyValue = Double.parseDouble(strClusteringKeyValue);
				} catch (NumberFormatException e) {
					throw new DBAppException("Primary key value must be a double");
				}
				break;
			case "java.lang.String":
				if (strClusteringKeyValue.trim().length() == 0) {
					throw new DBAppException("Primary key value must be a non-empty string");
				}
				primaryKeyValue = strClusteringKeyValue;
				break;

			default:
				throw new DBAppException("Primary key value must be an integer, double, or string");
		}

		if (!Metadata.validColumnNamesAndTypes(strTableName, htblColNameValue)) {
			throw new DBAppException("Invalid column names or types!");
		}

		Table table = Table.loadTable(strTableName);

		table.updateTuple(primaryKeyValue, htblColNameValue);

		System.out.println("Updated Table: " + strTableName);
	}

	// following method could be used to delete one or more rows.
	// htblColNameValue holds the key and value. This will be used in search
	// to identify which rows/tuples to delete.
	// htblColNameValue enteries are ANDED together
	// DONE
	public void deleteFromTable(String strTableName,
			Hashtable<String, Object> htblColNameValue) throws DBAppException {

		if (strTableName.trim().length() == 0) {
			throw new DBAppException("Table name cannot be empty");
		}

		if (!Metadata.tableExists(strTableName)) {
			throw new DBAppException("Table " + strTableName + " does not exist!");
		}

		if (!Metadata.validColumnNamesAndTypes(strTableName, htblColNameValue)) {
			throw new DBAppException("Invalid column names or types!");
		}

		Table table = Table.loadTable(strTableName);
		// System.out.println("test2");
		table.delete(strTableName, htblColNameValue);

		System.out.println("Deleted from Table: " + strTableName);
	}

	// DONE
	@SuppressWarnings("rawtypes")
	public Iterator selectFromTable(SQLTerm[] arrSQLTerms,
			String[] strarrOperators) throws DBAppException {

		if (arrSQLTerms == null || strarrOperators == null) {
			throw new DBAppException("Error: Null SQL Terms array or Operators array");

		}
		// can only select from one table at a time if (arrSQLTerms.length == 0) {
		if (arrSQLTerms.length == 0) {
			throw new DBAppException("Error: No Sql Terms detected");
		}

		String tableName = arrSQLTerms[0]._strTableName;

		for (SQLTerm sqlTerm : arrSQLTerms) {
			if (!sqlTerm._strTableName.equals(tableName)) {
				throw new DBAppException("Error: Can only select from one table at a time");
			}
		}

		if (!Metadata.tableExists(tableName)) {
			throw new DBAppException("Table " + tableName + " does not exist!");
		}
		if (arrSQLTerms.length != strarrOperators.length + 1) {
			throw new DBAppException("Invalid number of SQL Terms / Operators");
		}

		String[] operators = { "=", "!=", "<", "<=", ">", ">=" };

		// Convert the array to a Vector
		Vector<String> validOperators = new Vector<>(Arrays.asList(operators));

		for (SQLTerm sqlTerm : arrSQLTerms) {
			Hashtable<String, Object> htblColNameValue = new Hashtable<String, Object>();
			htblColNameValue.put(sqlTerm._strColumnName, sqlTerm._objValue);
			if (!Metadata.validColumnNamesAndTypes(tableName, htblColNameValue)) {
				throw new DBAppException("Invalid column name or type!");
			}
			if (!validOperators.contains(sqlTerm._strOperator)) {
				throw new DBAppException("Invalid operator: " + sqlTerm._strOperator);
			}
		}
		// System.out.println(htblColNameValue);
		// System.out.println(Metadata.validColumnNamesAndTypes(tableName,
		// htblColNameValue));

		Table table = Table.loadTable(tableName);
		Iterator it = table.selectTuple(arrSQLTerms, strarrOperators);

		System.out.println("Selected from Table: " + tableName);

		while (it.hasNext()) {
			System.out.println(it.next());
		}
		return it;
	}

	public static void printTable(String strTableName) {
		if (!Metadata.tableExists(strTableName)) {
			System.out.println("Table " + strTableName + " does not exist!");
			return;
		}
		Table myTable = Table.loadTable(strTableName);
		System.out.println(myTable);
	}

	// public static void logTable(String strTableName) {
	// if (!Metadata.tableExists(strTableName)) {
	// System.out.println("Table " + strTableName + " does not exist!");
	// return;
	// }

	// Table myTable = Table.loadTable(strTableName);
	// String tableString = myTable.toString();

	// try (FileWriter writer = new FileWriter("output.txt")) {
	// writer.write(tableString);
	// System.out.println("Table " + strTableName + " logged successfully to
	// output.txt");
	// } catch (IOException e) {
	// System.err.println("Error writing to output.txt: " + e.getMessage());
	// }
	// }

	// @SuppressWarnings("rawtypes")
	// protected void bulkInsertIntoTable(String tableName, int startNum, int
	// endNum) throws DBAppException {
	// Vector<Integer> nums = new Vector<>();
	// for (int i = startNum; i <= endNum; i++) {
	// nums.add(i);

	// }
	// Collections.shuffle(nums);

	// System.out.println(nums);

	// for (int i : nums) {
	// Hashtable<String, Object> htblColNameValue = new Hashtable<>();
	// htblColNameValue.put("id", i);
	// htblColNameValue.put("name", "Moski no " + i);
	// htblColNameValue.put("gpa", 3.5);
	// // System.out.println("Before inserting" + htblColNameValue);
	// this.insertIntoTable(tableName, htblColNameValue);
	// }
	// printTable(tableName);
	// Iterator iterator = this.selectFromTable(
	// new SQLTerm[] { new SQLTerm(tableName, "id", ">", 0) },
	// new String[] {});

	// while (iterator.hasNext()) {
	// Tuple currentTuple = (Tuple) iterator.next();
	// nums.remove(currentTuple.getPrimaryKey());
	// }

	// if (nums.isEmpty()) {
	// System.out.println("All numbers inserted");
	// } else {
	// System.out.println("These numbers not inserted");
	// System.out.println(nums);
	// }
	// }

	public static void main(String[] args) {

		try {
			// DBApp dbApp = new DBApp();

			// utility.clearDatabaseSystem();

			// String strTableName = "Student";
			// Hashtable<String, String> htblColNameType = new Hashtable<String, String>();
			// htblColNameType.put("id", "java.lang.Integer");
			// htblColNameType.put("name", "java.lang.String");
			// htblColNameType.put("gpa", "java.lang.Double");
			// dbApp.createTable(strTableName, "id", htblColNameType);

			// // String strTableName2 = "Student2";
			// // Hashtable<String, String> htblColNameType2 = new Hashtable<String,
			// String>();
			// // htblColNameType2.put("id", "java.lang.Integer");
			// // htblColNameType2.put("name", "java.lang.String");
			// // htblColNameType2.put("gpa", "java.lang.Double");
			// // dbApp.createTable(strTableName2, "id", htblColNameType2);

			// // System.out.println(Metadata.getAllTables());

			// // // dbApp.createIndex("Student2", "id", "index2");

			// // // String strTableName = "Employee";
			// // // Hashtable<String, String> htblColNameType = new Hashtable<>();
			// // // dbApp.createTable(strTableName, "id", htblColNameType);

			// // // String strTableName = "Student";
			// // // Hashtable<String, String> htblColNameType = new Hashtable<String,
			// // String>();
			// // // htblColNameType.put("id", "java.lang.Integer");
			// // // htblColNameType.put("name", "java.lang.String");
			// // // htblColNameType.put("gpa", "java.lang.Double");
			// // // dbApp.createTable(strTableName, "id", htblColNameType);

			// // // ---------------------Employee Table--------------------------
			// // // String strTableName = "Employee";
			// // // Hashtable<String, String> htblColNameType = new Hashtable<>();
			// // // htblColNameType.put("id", "java.lang.Integer");
			// // // htblColNameType.put("name", "java.lang.String");
			// // // htblColNameType.put("gpa", "java.lang.Double");
			// // // dbApp.createTable(strTableName, "id", htblColNameType);
			// // // -------------------------------------------------------------

			// // // printTable("Employee");
			// // // printTable("o");

			// // // String strTableName = "allNumsTable";
			// // // Hashtable<String, String> htblColNameType = new Hashtable<String,
			// // String>();
			// // // htblColNameType.put("id", "java.lang.Integer");
			// // // htblColNameType.put("age", "java.lang.Integer");
			// // // htblColNameType.put("height", "java.lang.Integer");
			// // // dbApp.createTable(strTableName, "id", htblColNameType);

			// // // Hashtable<String, Object> htblColNameValue = new Hashtable<String,
			// // Object>();
			// // // htblColNameValue.put("id", 1.5);
			// // // htblColNameValue.put("id", 20);
			// // // htblColNameValue.put("id", 180);
			// // // dbApp.insertIntoTable("allNumsTable", htblColNameValue);
			// // // ------------------------SELECT TESTING-------------------------//

			// dbApp.createIndex("Student", "id", "index");
			// for (int i = 1; i <= 20; i++) {
			// Hashtable<String, Object> htblColNameValue = new Hashtable<>();
			// htblColNameValue.put("id", i);
			// htblColNameValue.put("name", "Moski no " + i);
			// htblColNameValue.put("gpa", 3.5);
			// dbApp.insertIntoTable("Student", htblColNameValue);
			// }
			// DBApp.printTable("Student");
			DBApp dbApp = new DBApp();
			SQLTerm[] sqlArray = {
					new SQLTerm("Student", "sID", "=", 8) };
			String[] ops = {};
			// SQLTerm[] sqlArray2 = {};

			dbApp.selectFromTable(sqlArray, ops);

			// // // // <= 7 1,2,3,4,5,6,7
			// // // // <= 5 1,2,3,4,5

			// // // while (iterator.hasNext()) {
			// // // System.out.println(iterator.next());
			// // // }

			// // // ----------------- Delete Table Testing ---------------------\\

			// // // for (int i = 1; i <= 20; i++) {
			// // // Hashtable<String, Object> htblColNameValue = new Hashtable<>();
			// // // htblColNameValue.put("id", i);
			// // // htblColNameValue.put("name", "Moski no " + i);
			// // // if (i > 6) {
			// // // htblColNameValue.put("gpa", 3.5);
			// // // } else {
			// // // htblColNameValue.put("gpa", 2.5);
			// // // }
			// // // dbApp.insertIntoTable("Student", htblColNameValue);
			// // // }
			// // // dbApp.createIndex("Student", "gpa", "index");
			// // // Hashtable<String, Object> htblTuplesToDelete = new Hashtable<>();
			// // // // htblTuplesToDelete.put("id", 6);
			// // // // htblTuplesToDelete.put("name", "Moski no " + "6");
			// // // htblTuplesToDelete.put("gpa", 3.5);
			// // // dbApp.printTable("Student");
			// // // dbApp.deleteFromTable("Student", htblTuplesToDelete);
			// // // dbApp.printTable("Student");

			// // // ----------------- Update Table Testing ---------------------\\

			// // // for (int i = 1; i <= 20; i++) {
			// // // Hashtable<String, Object> htblColNameValue = new Hashtable<>();
			// // // htblColNameValue.put("id", i);
			// // // htblColNameValue.put("name", "Moski no " + i);
			// // // if (i > 6) {
			// // // htblColNameValue.put("gpa", 3.5);
			// // // } else {
			// // // htblColNameValue.put("gpa", 2.5);
			// // // }
			// // // dbApp.insertIntoTable("Student", htblColNameValue);
			// // // }
			// // // dbApp.createIndex("Student", "gpa", "index");
			// // // Hashtable<String, Object> htblUpdatedTuple = new Hashtable<>();
			// // // // htblUpdatedTuple.put("id", 7);
			// // // htblUpdatedTuple.put("gpa", 6.0);
			// // // htblUpdatedTuple.put("name", "Updated");
			// // // dbApp.updateTable("Student", "6", htblUpdatedTuple);
			// // // dbApp.printTable("Student");

			// // // ----------------- Insert Table Testing ---------------------\\

			// // // for (int i = 1; i <= 20; i++) {
			// // // Hashtable<String, Object> htblColNameValue = new Hashtable<>();
			// // // htblColNameValue.put("id", i);
			// // // htblColNameValue.put("name", "Moski no " + i);
			// // // if (i > 6) {
			// // // htblColNameValue.put("gpa", 3.5);
			// // // } else {
			// // // htblColNameValue.put("gpa", 2.5);
			// // // }
			// // // if (i != 7) {
			// // // dbApp.insertIntoTable("Student", htblColNameValue);
			// // // }
			// // // }
			// // // dbApp.createIndex("Student", "gpa", "index");
			// // // Hashtable<String, Object> htblTupleToInsert = new Hashtable<>();
			// // // htblTupleToInsert.put("id", 7);
			// // // htblTupleToInsert.put("gpa", 6.0);
			// // // htblTupleToInsert.put("name", "Abso");
			// // // dbApp.insertIntoTable("Student", htblTupleToInsert);
			// // // dbApp.printTable("Student");
			// // // System.out.println("valid insert: " + Metadata.validInsert("Student",
			// // // htblTupleToInsert));
			// // // System.out.println("valid ColNameType: " +
			// // // Metadata.validColumnNamesAndTypes("Student", htblTupleToInsert));

		} catch (Exception exp) {
			exp.printStackTrace();
		}
	}

	public Iterator parseSQL(StringBuffer sql) throws DBAppException {
		it = null;
		// Create a CharStream from the input SQL string
		CharStream input = CharStreams.fromString(sql.toString());

		// Create a lexer that feeds off of the input CharStream
		BasicSQLLexer lexer = new BasicSQLLexer(input);

		// Create a buffer of tokens pulled from the lexer
		CommonTokenStream tokens = new CommonTokenStream(lexer);

		// Create a parser that feeds off the tokens buffer
		BasicSQLParser parser = new BasicSQLParser(tokens);

		// Set custom error listeners to suppress error messages
		lexer.removeErrorListeners();
		parser.removeErrorListeners();

		parser.addErrorListener(new CustomErrorListener(parser));

		try {
			// parser.sqlStatement();
			BasicSQLParser.SqlStatementContext tree = parser.sqlStatement();

			Controller listener = new Controller();

			ParseTreeWalker walker = new ParseTreeWalker();
			walker.walk(listener, tree);

		} catch (RuntimeException e) {
			throw new DBAppException(e.getMessage());
		}
		return this.it;
	}
}
// public static void main(String[] args) {
// // String sql = "CREATE TABLE moski3 (age INT, name VARCHAR primary key);";
// // String sql = "insert into moski (name, age) values ('mo3', 10);";
// // String sql = "select * from moski where age >= 5 and age <= 5;";
// // String sql = "select * from moski2 where age = 5;";
// // String sql = "update moski SET age = 5 WHERE name = 'mo3';";

// String[] sqlStatements = {
// // "select * from moski where name = 'mo3';",
// // "update moski SET age = 90 WHERE name = 'mo3';",
// // "delete from moski where name = 'mo4';",
// // "insert into moski (name, age) values ('mo4', 10);",
// // "insert into moski (name, age) values ('mo6', 10);",
// // "delete from moski where age = 5;",
// // // "insert into moski (name, age) values ('mo7', 5);",
// "create table moski (name VARCHAR primary key, age INT);",
// "insert into moski (name, age) values ('mo1', 10);",
// "insert into moski (name, age) values ('mo2', 10);",
// "insert into moski (name, age) values ('mo3', 10);",
// "insert into moski (name, age) values ('mo4', 10);",
// "insert into moski (name, age) values ('mo5', 10);",
// "insert into moski (name, age) values ('mo6', 10);",
// "insert into moski (name, age) values ('mo7', 10);",
// "create index indexMoski2 on moski (age);",
// "insert into moski (name, age) values ('mo8', 10);",
// // "select * from moski where age >= 0;",
// "delete from moski where name = 'mo2';",
// "update moski SET age = 3 WHERE name = 'mo7';",
// "delete from moski where age=3;",
// // "insert into moski (name, age) values ('mo5', 10);",
// "select * from moski where age >= 0;"
// };

// try {
// utility.clearDatabaseSystem();
// DBApp db = new DBApp();
// // DBApp.runSQL(sql);
// for (String sql : sqlStatements) {
// // DBApp db = new DBApp();
// // String tableName = "moski";
// // String clusteringKeyValue = "mo3";
// // Hashtable<String, Object> htblColNameVal = new Hashtable<>();

// // htblColNameVal.put("age", 5);

// // db.updateTable(tableName, clusteringKeyValue, htblColNameVal);
// Iterator it = db.parseSQL(new StringBuffer(sql));
// System.out.println(it == null ? "null" : "ITERATOR: " + it);
// }
// System.out.println("dunzo");
// DBApp.printTable("moski");
// } catch (DBAppException e) {
// System.out.println(e.getMessage());
// }

// // utility.clearDatabaseSystem();
// }

// public static void main(String[] args) {
// DBApp dbApp = new DBApp();
// StringBuffer sql = new StringBuffer(
// // "insert into Student (sID, name , gpa, major) values (4, 'moski', 3.0,
// // 'CS');");
// // "insert into Student (sID, name , gpa, major) values (6, 'cs', 3.1,
// 'CS');");
// // "update City SET population = 3 WHERE name = 'Alex';");
// // "update Student SET gpa = 9.0 WHERE sID = 4;");
// // "create index nameIndex on Student (name);");
// // "delete from Student where sID = 4;");
// "select * from Student where name = 'kr';");
// try {
// dbApp.parseSQL(sql);
// // throw new DBAppException("Error: Invalid SQL statement");
// } catch (DBAppException e) {
// System.out.println(e.getMessage());
// }

// // DBApp.printTable("Student");
// }
// }

// String[] sqlStatements = {
// // "select * from moski where name = 'mo3';",
// // "update moski SET age = 90 WHERE name = 'mo3';",
// // "delete from moski where name = 'mo4';",
// // "insert into moski (name, age) values ('mo4', 10);",
// // "insert into moski (name, age) values ('mo6', 10);",
// // "delete from moski where age = 5;",
// // // "insert into moski (name, age) values ('mo7', 5);",
// "create table moski (name VARCHAR primary key, age INT);",
// "insert into moski (name, age) values ('mo1', 10);",
// "insert into moski (name, age) values ('mo2', 10);",
// "insert into moski (name, age) values ('mo7', 10);",
// "create index indexMoski2 on moski (age);",
// "insert into moski (name, age) values ('mo8', 10);",
// // "select * from moski where age >= 0;",
// "delete from moski where name = 'mo2';",
// "update moski SET age = 3 WHERE name = 'mo7';",
// "delete from moski where age=3;",
// // "insert into moski (name, age) values ('mo5', 10);",
// "select * from moski where age >= 0;"
// };

// }