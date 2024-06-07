package com.halberdski.antlr4;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

public class QueryTester {
    public static void testQuery(String sql) {
        // Create a CharStream from the input SQL string
        CharStream input = CharStreams.fromString(sql);

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

            // Traverse the parse tree using the listener
            ParseTreeWalker walker = new ParseTreeWalker();
            walker.walk(listener, tree);
            // System.out.println("Valid SQL: " + sql);

            // System.out.println(tree.toStringTree());
            // System.out.println(tree.getText());
        } catch (ParseCancellationException e) {
            System.out.println("Invalid SQL: " + sql);
            System.out.println(e.getMessage());
        }

    }

    public static void main(String[] args) {
        // String[] selectStatements = {
        // "hi",
        // "SELECT * FROM table",
        // "SELECT * FROM table;",
        // "select * FROM table WHERE id = 5;",
        // "SELECT * FROM table WHERE id = 5;",
        // "SELECT * FROM table WHERE id = 5 AND age = 1;",
        // "SELECT * FROM table WHERE id = 5 OR age = 1;",
        // "SELECT * FROM table WHERE id = 5 OR age = 10;",
        // "SELECT * FROM table WHERE id = 'mo' OR age = 10;",
        // "SELECT * FROM table WHERE id = mo OR age = 1.5.;",
        // "SELECT * FROM table WHERE id = mo OR age = 1. 5.;",
        // "SELECT * FROM table WHERE id = 'mo' OR age = 1. 5.;",
        // "SELECT * FROM table WHERE id = 'mo' OR age = 1.5..;",
        // "select * FROM table WHERE id = 5;;;",
        // };
        // String[] validInsertStatements = {
        // "INSERT INTO tableName (column1, column2) VALUES ('value1', 'value2');",
        // "INSERT INTO tableName (column1, column2) VALUES (123, 456);",
        // "INSERT INTO tableName (column1, column2) VALUES (TRUE, FALSE);",
        // "INSERT INTO tableName (column1) VALUES ('value1');",
        // };

        // String[] invalidInsertStatements = {
        // "INSERT INTO tableName (column1) VALUES (value1);",
        // "INSERT INTO tableName (column1, column2) VALUES (NULL, NULL);",
        // "INSERT INTO tableName VALUES (value1, value2);",
        // "INSERT INTO tableName VALUES ('value1', 'value2');",
        // "INSERT INTO tableName VALUES (123, 456);",
        // "INSERT INTO tableName VALUES (TRUE, FALSE);",
        // "INSERT INTO tableName VALUES (NULL, NULL);"
        // };

        // for (String sql : invalidInsertStatements) {
        // testQuery(sql);
        // System.out.println("\n");
        // }

        // System.err.println("-----------VALID-----------");
        // System.err.println();
        // for (String sql : validInsertStatements) {
        // testQuery(sql);
        // System.out.println("\n");
        // }

        // // -------------------SELECT STATEMENTS-------------------

        // String[] invalidCreateStatements = {
        // "CREATE TABLE (id INT;",
        // "CREATE TABLE medhat (id INT;",
        // "CREATE TABLE (id INT);",
        // "CREATE TABLE tableName ();",
        // "CREATE TABLE employees (id INT, name VARCHAR(100));",
        // "CREATE TABLE employees (id INT, name VARCHAR(100) NOT NULL, age, department
        // VARCHAR(50));",
        // "CREATE TABLE products (product_id INT PRIMARY KEY, product_name
        // VARCHAR(255), price);",
        // "CREATE TABLE users (user_id INT PRIMARY KEY, username VARCHAR(50) UNIQUE,
        // password VARCHAR(100) UNIQUE);",
        // "CREATE TABLE tableName (column1 INT;, column2 VARCHAR);",
        // "CREATE TABLE tableName (column1 INT, column2 VARCHAR;);",
        // };

        // String[] validCreateStatements = {
        // "CREATE TABLE tableName (column1 INT, column2 VARCHAR);",
        // "CREATE TABLE employees (id INT PRIMARY KEY, name VARCHAR , age INT,
        // department VARCHAR);",
        // "CREATE TABLE products (product_id int primary key, product_name varchar,
        // price float);",
        // "CREATE TABLE users (user_id INT PRIMARY KEY, username varchar, password
        // VARCHAR);"
        // };

        // for (String sql : invalidCreateStatements) {
        // testQuery(sql);
        // System.out.println("\n");
        // }

        // System.err.println("-----------VALID-----------");
        // System.err.println();
        // for (String sql : validCreateStatements) {
        // testQuery(sql);
        // System.out.println("\n");
        // }

        // -------------------Create Index STATEMENTS-------------------

        // String[] invalidCreateIndexStatements = {
        // "CREATE INDEX ON tableName (column1);", // Missing index name
        // "CREATE INDEX idx_name ON (column1);", // Missing table name
        // "CREATE INDEX idx_age ON employees;", // Missing column list
        // "CREATE UNIQUE INDEX idx_username ON users (username) WHERE age > 18;", //
        // Invalid UNIQUE clause
        // "CREATE INDEX idx_price ON products (price) WHERE price < 0;", // Invalid
        // WHERE clause condition
        // "CREATE UNIQUE INDEX idx_username ON users (username);",
        // "CREATE UNIQUE INDEX idx_email ON contacts (email);",
        // "CREATE INDEX idx_product_name ON products (product_name) WHERE price >
        // 100;",
        // };

        // String[] validCreateIndexStatements = {
        // "CREATE INDEX idx_name ON tableName (column1);",
        // "CREATE INDEX idx_age ON employees (age);",
        // "CREATE INDEX idx_age ON employees (age,moski);",
        // };

        // for (String sql : invalidCreateIndexStatements) {
        // testQuery(sql);
        // System.out.println("\n");
        // }

        // System.err.println("-----------VALID-----------");
        // System.err.println();
        // for (String sql : validCreateIndexStatements) {
        // testQuery(sql);
        // System.out.println("\n");
        // }

        // -------------------Delete STATEMENTS-------------------

        // String[] invalidDeleteStatements = {
        // "DELETE FROM tableName WHERE id = 5 OR;",
        // "DELETE FROM tableName WHERE;",
        // "DELETE FROM tableName WHERE id IN (1, 2, 3);",
        // "DELETE FROM tableName WHERE EXISTS (SELECT * FROM otherTable WHERE
        // otherTable.id = tableName.id);"
        // };

        // String[] validDeleteStatements = {
        // "DELETE FROM tableName WHERE id = 5 AND age = 10;",
        // "DELETE FROM tableName;",
        // "DELETE FROM tableName WHERE id = 5;",
        // "DELETE FROM tableName WHERE id = 5 AND age > 18;",
        // "DELETE FROM tableName WHERE id = 5 OR age < 18;",
        // };

        // for (String sql : invalidDeleteStatements) {
        // testQuery(sql);
        // System.out.println("\n");
        // }

        // System.err.println("-----------VALID-----------");
        // System.err.println();
        // for (String sql : validDeleteStatements) {
        // testQuery(sql);
        // System.out.println("\n");
        // }

        // -------------------Update STATEMENTS-------------------

        String[] invalidUpdateStatements = {
                "UPDATE SET column1 = value1 WHERE id = 5;",
                "UPDATE tableName SET WHERE id = 5;",
                "UPDATE tableName SET column1 = value1 AND column2 = value2 WHERE id = 5;",
                "UPDATE tableName SET column1 = WHERE id = 5;",
                "UPDATE tableName WHERE id = 5 SET column1 = value1;",
                "UPDATE tableName SET column1 = 'value1' WHERE id == 5;",
                "UPDATE tableName SET column1 = 'value1' WHERE id > 5;",
                "UPDATE tableName SET column1 = 'value1' WHERE id =< 5;",
                "UPDATE tableName SET column1 = 'value1', column2 = 'value2' WHERE id IN (1, 2, 3);",
                "UPDATE tableName SET column1 = 'value1', column2 = 'value2' WHERE EXISTS (SELECT * FROM otherTable WHERE otherTable.id = tableName.id);",
                "UPDATE tableName SET column1 = 'value1' WHERE id = ;",
        };

        String[] validUpdateStatements = {
                "UPDATE tableName SET column1 = 'value1' WHERE id = 5;",
                "UPDATE tableName SET column1 = 'value1', column2 = 'value2' WHERE id = 5;",
                "UPDATE tableName SET column1 = 'value1', column2 = 'value2',    column3 = 1 WHERE id = 5;",
        };

        for (String sql : invalidUpdateStatements) {
            testQuery(sql);
            System.out.println("\n");
        }

        System.err.println("-----------VALID-----------");
        System.err.println();
        for (String sql : validUpdateStatements) {
            testQuery(sql);
            System.out.println("\n");
        }
    }
}
