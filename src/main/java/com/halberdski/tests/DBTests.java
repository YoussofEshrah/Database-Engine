package com.halberdski.tests;

import java.util.Collections;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import com.halberdski.engine.DBApp;
import com.halberdski.engine.SQLTerm;
import com.halberdski.exceptions.DBAppException;
import com.halberdski.model.BTree;
import com.halberdski.engine.utility;

// @SuppressWarnings("unused")
class DBTests {

    private static void testDeletes(int n) throws DBAppException {
        DBApp dbApp = new DBApp();
        utility.clearDatabaseSystem();

        String strTableName = "Student";
        Hashtable<String, String> htblColNameType = new Hashtable<String, String>();
        htblColNameType.put("id", "java.lang.Integer");
        htblColNameType.put("name", "java.lang.String");
        htblColNameType.put("gpa", "java.lang.Double");
        dbApp.createTable(strTableName, "id", htblColNameType);

        Vector<Integer> v = new Vector<Integer>();
        for (int i = 1; i <= n; i++) {
            v.add(i);
        }
        Collections.shuffle(v);

        for (int num : v) {
            Hashtable<String, Object> htblColNameValue = new Hashtable<String, Object>();
            htblColNameValue.put("id", num);
            htblColNameValue.put("name", "name" + num);
            htblColNameValue.put("gpa", num * 0.01);
            dbApp.insertIntoTable(strTableName, htblColNameValue);

        }

        DBApp.printTable(strTableName);

        for (int i = -190; i <= 0; i++) {
            Hashtable<String, Object> criteria = new Hashtable<String, Object>();
            // criteria.put("name", "name" + i);
            criteria.put("id", i);
            // criteria.put("gpa", 0.01 * i);

            dbApp.deleteFromTable(strTableName, criteria);
        }

        DBApp.printTable(strTableName);
    }

    private static void testUpdates(int n) throws DBAppException {
        DBApp dbApp = new DBApp();
        utility.clearDatabaseSystem();

        String strTableName = "Student";
        Hashtable<String, String> htblColNameType = new Hashtable<String, String>();
        htblColNameType.put("id", "java.lang.Integer");
        htblColNameType.put("name", "java.lang.String");
        htblColNameType.put("gpa", "java.lang.Double");
        dbApp.createTable(strTableName, "id", htblColNameType);

        Vector<Integer> v = new Vector<Integer>();
        for (int i = 1; i <= n; i++) {
            v.add(i);
        }
        Collections.shuffle(v);

        for (int num : v) {
            Hashtable<String, Object> htblColNameValue = new Hashtable<String, Object>();
            htblColNameValue.put("id", num);
            htblColNameValue.put("name", "name" + num);
            htblColNameValue.put("gpa", num * 0.01);
            dbApp.insertIntoTable(strTableName, htblColNameValue);

        }

        DBApp.printTable(strTableName);

        for (int i = 7; i <= 10; i++) {
            Hashtable<String, Object> criteria = new Hashtable<String, Object>();
            // criteria.put("name", "name" + i);
            criteria.put("name", "updated" + i);
            criteria.put("gpa", i * 1.0);
            // criteria.put("mahmoud", i * 1.0);
            // criteria.put("gpa", 0.01 * i);

            // dbApp.updateTable(strTableName, "" + i * 1.0, criteria);
            dbApp.updateTable(strTableName, i + "", criteria);
        }

        DBApp.printTable(strTableName);
    }

    private static void testInserts(int n) throws DBAppException {
        DBApp dbApp = new DBApp();
        utility.clearDatabaseSystem();

        String strTableName = "Student";
        Hashtable<String, String> htblColNameType = new Hashtable<String, String>();
        htblColNameType.put("id", "java.lang.Integer");
        htblColNameType.put("name", "java.lang.String");
        htblColNameType.put("gpa", "java.lang.Double");
        dbApp.createTable(strTableName, "id", htblColNameType);

        Vector<Integer> v = new Vector<Integer>();
        for (int i = 1; i <= n; i++) {
            v.add(i);
        }
        Collections.shuffle(v);

        for (int num : v) {
            Hashtable<String, Object> htblColNameValue = new Hashtable<String, Object>();
            htblColNameValue.put("id", num);
            htblColNameValue.put("name", "name" + num);
            htblColNameValue.put("gpa", num * 0.01);
            // htblColNameValue.put("mahmoud", num * 0.01);
            dbApp.insertIntoTable(strTableName, htblColNameValue);
            // dbApp.insertIntoTable(strTableName + "ko", htblColNameValue);

        }

        DBApp.printTable(strTableName);

        for (int num : v) {
            try {
                Hashtable<String, Object> htblColNameValue = new Hashtable<String, Object>();
                htblColNameValue.put("id", num);
                htblColNameValue.put("name", "name" + num);
                htblColNameValue.put("gpa", num * 0.01);
                // htblColNameValue.put("mahmoud", num * 0.01);
                dbApp.insertIntoTable(strTableName, htblColNameValue);
                // dbApp.insertIntoTable(strTableName + "ko", htblColNameValue);
                throw new Exception("Shouldnt throw this exception");
            } catch (DBAppException e) {
            } catch (Exception e) {
                System.out.println(e.getMessage());
                DBApp.printTable(strTableName);
            }
        }
    }

    private static void testInsertsStringPrimaryKey(int n) throws DBAppException {
        DBApp dbApp = new DBApp();
        utility.clearDatabaseSystem();

        String strTableName = "Student";
        Hashtable<String, String> htblColNameType = new Hashtable<String, String>();
        htblColNameType.put("id", "java.lang.String");
        htblColNameType.put("name", "java.lang.String");
        htblColNameType.put("gpa", "java.lang.Double");
        dbApp.createTable(strTableName, "id", htblColNameType);

        Vector<Integer> v = new Vector<Integer>();
        for (int i = 1; i <= n; i++) {
            v.add(i);
        }
        Collections.shuffle(v);

        for (int num : v) {
            Hashtable<String, Object> htblColNameValue = new Hashtable<String, Object>();
            htblColNameValue.put("id", "ID: " + num);
            htblColNameValue.put("name", "name" + num);
            htblColNameValue.put("gpa", num * 0.01);
            // htblColNameValue.put("mahmoud", num * 0.01);
            dbApp.insertIntoTable(strTableName, htblColNameValue);
            // dbApp.insertIntoTable(strTableName + "ko", htblColNameValue);

        }

        DBApp.printTable(strTableName);

        for (int num : v) {
            try {
                Hashtable<String, Object> htblColNameValue = new Hashtable<String, Object>();
                htblColNameValue.put("id", "ID: " + num);
                htblColNameValue.put("name", "name" + num);
                htblColNameValue.put("gpa", num * 0.01);
                // htblColNameValue.put("mahmoud", num * 0.01);
                dbApp.insertIntoTable(strTableName, htblColNameValue);
                // dbApp.insertIntoTable(strTableName + "ko", htblColNameValue);
                throw new Exception("Shouldnt throw this exception");
            } catch (DBAppException e) {
            } catch (Exception e) {
                System.out.println(e.getMessage());
                DBApp.printTable(strTableName);
            }
        }
    }

    private static void testInsertsDoublePrimaryKey(int n) throws DBAppException {
        DBApp dbApp = new DBApp();
        utility.clearDatabaseSystem();

        String strTableName = "Student";
        Hashtable<String, String> htblColNameType = new Hashtable<String, String>();
        htblColNameType.put("id", "java.lang.Double");
        htblColNameType.put("name", "java.lang.String");
        htblColNameType.put("gpa", "java.lang.Double");
        dbApp.createTable(strTableName, "id", htblColNameType);

        Vector<Integer> v = new Vector<Integer>();
        for (int i = 1; i <= n; i++) {
            v.add(i);
        }
        Collections.shuffle(v);

        for (int num : v) {
            Hashtable<String, Object> htblColNameValue = new Hashtable<String, Object>();
            htblColNameValue.put("id", num * 1.0);
            htblColNameValue.put("name", "name" + num);
            htblColNameValue.put("gpa", num * 0.01);
            // htblColNameValue.put("mahmoud", num * 0.01);
            dbApp.insertIntoTable(strTableName, htblColNameValue);
            // dbApp.insertIntoTable(strTableName + "ko", htblColNameValue);

        }

        DBApp.printTable(strTableName);

        for (int num : v) {
            try {
                Hashtable<String, Object> htblColNameValue = new Hashtable<String, Object>();
                htblColNameValue.put("id", num * 1.0);
                htblColNameValue.put("name", "name" + num);
                htblColNameValue.put("gpa", num * 0.01);
                // htblColNameValue.put("mahmoud", num * 0.01);
                dbApp.insertIntoTable(strTableName, htblColNameValue);
                // dbApp.insertIntoTable(strTableName + "ko", htblColNameValue);
                throw new Exception("Shouldnt throw this exception");
            } catch (DBAppException e) {
            } catch (Exception e) {
                System.out.println(e.getMessage());
                DBApp.printTable(strTableName);
            }
        }
    }

    private static void testCreateIndex() throws DBAppException {
        DBApp dbApp = new DBApp();
        utility.clearDatabaseSystem();

        String strTableName = "Student";
        Hashtable<String, String> htblColNameType = new Hashtable<String, String>();
        htblColNameType.put("id", "java.lang.Integer");
        htblColNameType.put("name", "java.lang.String");
        htblColNameType.put("gpa", "java.lang.Double");
        dbApp.createTable(strTableName, "id", htblColNameType);

        Vector<Integer> v = new Vector<Integer>();
        for (int i = 1; i <= 10; i++) {
            v.add(i);
        }
        Collections.shuffle(v);

        for (int num : v) {
            Hashtable<String, Object> htblColNameValue = new Hashtable<String, Object>();
            htblColNameValue.put("id", num);
            htblColNameValue.put("name", "name" + num);
            htblColNameValue.put("gpa", num * 0.01);
            dbApp.insertIntoTable(strTableName, htblColNameValue);

        }

        DBApp.printTable(strTableName);

        // dbApp.createIndex(strTableName, "gpa", "myIndex");
        dbApp.createIndex(strTableName, "gpa", "myIndex");
        dbApp.createIndex(strTableName, "id", "myIndex2");
        // dbApp.createIndex(strTableName, "id", "myIndex");

    }

    @SuppressWarnings("rawtypes")
    private static void wrongTestCase(int n) throws DBAppException {
        DBApp dbApp = new DBApp();
        utility.clearDatabaseSystem();

        String strTableName = "Student";
        Hashtable<String, String> htblColNameType = new Hashtable<String, String>();
        htblColNameType.put("id", "java.lang.Integer");
        htblColNameType.put("name", "java.lang.String");
        htblColNameType.put("gpa", "java.lang.Double");
        dbApp.createTable(strTableName, "id", htblColNameType);

        Vector<Integer> v = new Vector<Integer>();
        for (int i = 1; i <= n; i++) {
            v.add(i);
        }
        Collections.shuffle(v);

        for (int num : v) {
            Hashtable<String, Object> htblColNameValue = new Hashtable<String, Object>();
            htblColNameValue.put("id", num);
            htblColNameValue.put("name", "name" + num);
            htblColNameValue.put("gpa", num * 0.01);
            dbApp.insertIntoTable(strTableName, htblColNameValue);

        }

        DBApp.printTable(strTableName);

        dbApp.createIndex(strTableName, "name", "nameIndex");
        dbApp.createIndex(strTableName, "gpa", "gpaIndex");

        SQLTerm[] arrSQLTerms = new SQLTerm[1];
        arrSQLTerms[0] = new SQLTerm("Student", "name", ">", "name3");
        // arrSQLTerms[1] = new SQLTerm("Student", "gpa", "<=", 110.0);
        // arrSQLTerms[1] = new SQLTerm("Student2", "gpa", "=", 0.0);

        // String[] strarrOperators = new String[1];
        // strarrOperators[0] = "OR";

        String[] strarrOperators = new String[0];
        // Call the method under test
        Iterator resultSet = dbApp.selectFromTable(arrSQLTerms, strarrOperators);

        System.out.println("Result set:");
        while (resultSet.hasNext()) {
            System.out.println(resultSet.next());
        }

        DBApp.printTable(strTableName);
        BTree index = BTree.loadIndex("nameIndex");
        System.out.println(index.search(index.getMin(), index.getMax(), true, true));
        System.out.println(index.search("name1", index.getMax(), true, true));
    }

    @SuppressWarnings("rawtypes")
    private static void testSelect() throws DBAppException {
        DBApp dbApp = new DBApp();
        utility.clearDatabaseSystem();

        String strTableName = "Student";
        Hashtable<String, String> htblColNameType = new Hashtable<String, String>();
        htblColNameType.put("id", "java.lang.Integer");
        htblColNameType.put("name", "java.lang.String");
        htblColNameType.put("gpa", "java.lang.Double");
        dbApp.createTable(strTableName, "id", htblColNameType);

        String strTableName2 = "Student2";
        Hashtable<String, String> htblColNameType2 = new Hashtable<String, String>();
        htblColNameType2.put("id", "java.lang.Integer");
        htblColNameType2.put("name", "java.lang.String");
        htblColNameType2.put("gpa", "java.lang.Double");
        dbApp.createTable(strTableName2, "id", htblColNameType2);

        Vector<Integer> v = new Vector<Integer>();
        for (int i = 1; i <= 10; i++) {
            v.add(i);
        }
        Collections.shuffle(v);

        for (int num : v) {
            Hashtable<String, Object> htblColNameValue = new Hashtable<String, Object>();
            htblColNameValue.put("id", num);
            htblColNameValue.put("name", "name" + num);
            htblColNameValue.put("gpa", num * 0.01);
            dbApp.insertIntoTable(strTableName, htblColNameValue);

        }

        DBApp.printTable(strTableName);

        dbApp.createIndex(strTableName, "name", "nameIndex");
        dbApp.createIndex(strTableName, "gpa", "gpaIndex");

        SQLTerm[] arrSQLTerms = new SQLTerm[2];
        arrSQLTerms[0] = new SQLTerm("Student", "id", ">", 5);
        arrSQLTerms[1] = new SQLTerm("Student", "id", ">=", 2);
        // arrSQLTerms[1] = new SQLTerm("Student2", "gpa", "=", 0.0);

        String[] strarrOperators = new String[1];
        strarrOperators[0] = "XOR";

        // Call the method under test
        Iterator resultSet = dbApp.selectFromTable(arrSQLTerms, strarrOperators);

        System.out.println("Result set:");
        while (resultSet.hasNext()) {
            System.out.println(resultSet.next());
        }
    }

    @SuppressWarnings("rawtypes")
    private static void testSelectOperatorPrecedence() throws DBAppException {
        DBApp dbApp = new DBApp();
        utility.clearDatabaseSystem();

        String strTableName = "Student";
        Hashtable<String, String> htblColNameType = new Hashtable<String, String>();
        htblColNameType.put("id", "java.lang.Integer");
        htblColNameType.put("name", "java.lang.String");
        htblColNameType.put("gpa", "java.lang.Double");
        dbApp.createTable(strTableName, "id", htblColNameType);

        String strTableName2 = "Student2";
        Hashtable<String, String> htblColNameType2 = new Hashtable<String, String>();
        htblColNameType2.put("id", "java.lang.Integer");
        htblColNameType2.put("name", "java.lang.String");
        htblColNameType2.put("gpa", "java.lang.Double");
        dbApp.createTable(strTableName2, "id", htblColNameType2);

        Vector<Integer> v = new Vector<Integer>();
        for (int i = 1; i <= 10; i++) {
            v.add(i);
        }
        Collections.shuffle(v);

        for (int num : v) {
            Hashtable<String, Object> htblColNameValue = new Hashtable<String, Object>();
            htblColNameValue.put("id", num);
            htblColNameValue.put("name", "name" + num);
            htblColNameValue.put("gpa", num * 0.01);
            dbApp.insertIntoTable(strTableName, htblColNameValue);

        }

        DBApp.printTable(strTableName);

        dbApp.createIndex(strTableName, "name", "nameIndex");
        dbApp.createIndex(strTableName, "gpa", "gpaIndex");

        SQLTerm[] arrSQLTerms = new SQLTerm[3];
        arrSQLTerms[0] = new SQLTerm("Student", "id", ">", 5);
        arrSQLTerms[2] = new SQLTerm("Student", "id", ">=", 10);
        arrSQLTerms[1] = new SQLTerm("Student", "id", ">=", 2);

        String[] strarrOperators = new String[2];
        strarrOperators[1] = "OR";
        strarrOperators[0] = "AND";

        // Call the method under test
        Iterator resultSet = dbApp.selectFromTable(arrSQLTerms, strarrOperators);

        System.out.println("Result set:");
        while (resultSet.hasNext()) {
            System.out.println(resultSet.next());
        }
    }

    private static void testDeleteEmpty(int n) throws DBAppException {
        DBApp dbApp = new DBApp();
        utility.clearDatabaseSystem();

        String strTableName = "Student";
        Hashtable<String, String> htblColNameType = new Hashtable<String, String>();
        htblColNameType.put("id", "java.lang.Integer");
        htblColNameType.put("name", "java.lang.String");
        htblColNameType.put("gpa", "java.lang.Double");
        dbApp.createTable(strTableName, "id", htblColNameType);

        Vector<Integer> v = new Vector<Integer>();
        for (int i = 1; i <= n; i++) {
            v.add(i);
        }
        Collections.shuffle(v);

        for (int num : v) {
            Hashtable<String, Object> htblColNameValue = new Hashtable<String, Object>();
            htblColNameValue.put("id", num);
            htblColNameValue.put("name", "name" + num);
            htblColNameValue.put("gpa", num * 0.01);
            dbApp.insertIntoTable(strTableName, htblColNameValue);

        }

        DBApp.printTable(strTableName);

        Hashtable<String, Object> criteria = new Hashtable<String, Object>();
        // criteria.put("name", "name" + i);
        // criteria.put("id", i);
        // criteria.put("gpa", 0.01 * i);

        dbApp.deleteFromTable(strTableName, criteria);

        DBApp.printTable(strTableName);
    }

    private static void testUpdateEmpty(int n) throws DBAppException {
        DBApp dbApp = new DBApp();
        utility.clearDatabaseSystem();

        String strTableName = "Student";
        Hashtable<String, String> htblColNameType = new Hashtable<String, String>();
        htblColNameType.put("id", "java.lang.Integer");
        htblColNameType.put("name", "java.lang.String");
        htblColNameType.put("gpa", "java.lang.Double");
        dbApp.createTable(strTableName, "id", htblColNameType);

        Vector<Integer> v = new Vector<Integer>();
        for (int i = 1; i <= n; i++) {
            v.add(i);
        }
        Collections.shuffle(v);

        for (int num : v) {
            Hashtable<String, Object> htblColNameValue = new Hashtable<String, Object>();
            htblColNameValue.put("id", num);
            htblColNameValue.put("name", "name" + num);
            htblColNameValue.put("gpa", num * 0.01);
            dbApp.insertIntoTable(strTableName, htblColNameValue);

        }

        DBApp.printTable(strTableName);

        for (int i = 1; i <= n; i++) {
            Hashtable<String, Object> criteria = new Hashtable<String, Object>();
            dbApp.updateTable(strTableName, i + "", criteria);
            // criteria.put("name", "name" + i);
            // criteria.put("name", "updated" + i);
            // criteria.put("gpa", i * 1.0);
            // criteria.put("mahmoud", i * 1.0);
            // criteria.put("gpa", 0.01 * i);

            // dbApp.updateTable(strTableName, "" + i * 1.0, criteria);
        }

        DBApp.printTable(strTableName);
    }

    public static void main(String[] args) throws DBAppException {
        testInserts(101);
        testDeletes(10);
        testUpdates(10);
        testCreateIndex();
        wrongTestCase(10);
        testInsertsStringPrimaryKey(101);
        testInsertsDoublePrimaryKey(101);
        testSelect();
        testSelectOperatorPrecedence();
        testDeleteEmpty(100);
        testUpdateEmpty(100);

        // System.out.println(System.getProperty("user.dir"));

        utility.clearDatabaseSystem();

    }

}
