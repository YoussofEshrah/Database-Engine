package com.halberdski.model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;

import com.halberdski.exceptions.DBAppException;

public class Metadata {
    private static Hashtable<String, Hashtable<String, Hashtable<String, String>>> ht = new Hashtable<String, Hashtable<String, Hashtable<String, String>>>();

    private static String pathToMetadataFile = "./src/main/resources/metadata.csv";

    /*
     * 
     * DONE:
     * addTable
     * addIndex
     * tableExists
     * tableHasColumn
     * tableHasIndexOnColumn
     * getIndexName
     * getPrimaryKeyName
     * getColumnType
     * compatibleOperation
     * indexExists
     * getIndicesOnTable
     * getAllTables
     * 
     *
     * 
     * still:
     */

    public static void addTable(String strTableName, String primaryKeyName,
            Hashtable<String, String> htblColNameType) {

        readMetadata();

        Hashtable<String, Hashtable<String, String>> table = new Hashtable<String, Hashtable<String, String>>();

        for (Map.Entry<String, String> entry : htblColNameType.entrySet()) {
            String columnName = entry.getKey();
            String columnType = entry.getValue();

            Hashtable<String, String> columnAttributes = new Hashtable<String, String>();
            columnAttributes.put("columnType", columnType);
            columnAttributes.put("clusteringKey", columnName.equals(primaryKeyName) ? "True" : "False");
            columnAttributes.put("indexName", "null");
            columnAttributes.put("indexType", "null");

            table.put(columnName, columnAttributes);
        }

        ht.put(strTableName, table);
        saveMetadata();
    }

    public static boolean tableExists(String tableName) {
        readMetadata();
        return ht.containsKey(tableName);
    }

    public static void saveMetadata() {
        String content = "";

        // Vector<String> tables = new Vector<String>();
        // Vector<String> columns = new Vector<String>();
        // Vector<String> attributes = new Vector<String>();

        for (Map.Entry<String, Hashtable<String, Hashtable<String, String>>> tableEntry : Metadata.ht.entrySet()) {
            String tableName = tableEntry.getKey();
            // tables.add(tableName);
            Hashtable<String, Hashtable<String, String>> columnHashtable = tableEntry.getValue();
            for (Map.Entry<String, Hashtable<String, String>> columnEntry : columnHashtable.entrySet()) {
                String columnName = columnEntry.getKey();
                // columns.add("(" + tableName + "," + columnName + ")");
                Hashtable<String, String> attributeHashtable = columnEntry.getValue();

                String columnType = attributeHashtable.get("columnType");
                String clusteringKey = attributeHashtable.get("clusteringKey");
                String indexName = attributeHashtable.get("indexName");
                String indexType = attributeHashtable.get("indexType");

                String attributesString = "," + columnType + "," + clusteringKey + "," + indexName + "," + indexType;
                // attributes.add("(" + tableName + "," + columnName + "," + attributesString +
                // ")");

                String metadataRow = tableName + "," + columnName + attributesString;
                // System.out.println(metadataRow);
                content += metadataRow + "\n";
            }
        }
        if (content.length() > 0 && content.endsWith("\n")) {
            content = content.substring(0, content.length() - 1);
        }

        // System.out.println(tables);
        // System.out.println(columns);
        // System.out.println(attributes);

        // // Write to metadata.csv file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(Metadata.pathToMetadataFile, false))) {
            writer.write(content);
        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
        }
    }

    public static void readMetadata() {
        // Load the metadata from the file
        ht.clear();

        try (BufferedReader reader = new BufferedReader(new FileReader(Metadata.pathToMetadataFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 6) {
                    String tableName = parts[0];
                    String columnName = parts[1];
                    String columnType = parts[2];
                    String clusteringKey = parts[3];
                    String indexName = parts[4];
                    String indexType = parts[5];

                    // Create a hashtable to store column attributes
                    Hashtable<String, String> columnAttributes = new Hashtable<>();
                    columnAttributes.put("columnType", columnType);
                    columnAttributes.put("clusteringKey", clusteringKey);
                    columnAttributes.put("indexName", indexName);
                    columnAttributes.put("indexType", indexType);

                    // Get the columns hashtable for the current table or create a new one if it
                    // doesn't exist
                    Hashtable<String, Hashtable<String, String>> columns = ht.getOrDefault(tableName,
                            new Hashtable<String, Hashtable<String, String>>());

                    // Put the column attributes into the columns hashtable
                    columns.put(columnName, columnAttributes);

                    // Put the updated columns hashtable back into the ht hashtable
                    ht.put(tableName, columns);

                } else {
                    System.err.println("Invalid metadata line: " + line);
                    // throw new Exception("Invalid Metadata Line: " + line);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading metadata file: " + e.getMessage());
        }
    }

    public static boolean tableHasColumn(String tableName, String columnName) {
        readMetadata();
        return ht.get(tableName).containsKey(columnName);
    }

    public static boolean tableHasIndexOnColumn(String tableName, String columnName) {
        readMetadata();
        return !ht.get(tableName).get(columnName).get("indexType").equals("null");
    }

    public static String getIndexName(String tableName, String columnName) {
        readMetadata();
        return ht.get(tableName).get(columnName).get("indexName");
    }

    public static String getColumnType(String tableName, String columnName) {
        readMetadata();
        return ht.get(tableName).get(columnName).get("columnType");
    }

    public static String getPrimaryKeyName(String tableName) {
        readMetadata();
        for (Map.Entry<String, Hashtable<String, String>> entry : ht.get(tableName).entrySet()) {
            if (entry.getValue().get("clusteringKey").equals("True")) {
                return entry.getKey();
            }
        }
        return "";
    }

    // Used in:
    // DBAPP.insert

    public static boolean validInsert(String strTableName,
            Hashtable<String, Object> htblColNameValue) {
        readMetadata();
        if (htblColNameValue.size() != ht.get(strTableName).size()) {
            return false;
        }
        // System.out.println("valid size");
        return validColumnNamesAndTypes(strTableName, htblColNameValue);
    }

    // Used in:
    // DBAPP.delete
    // DBAPP.update
    // DBAPP.selectFromTable
    public static boolean validColumnNamesAndTypes(String strTableName,
            Hashtable<String, Object> htblColNameValue) {
        readMetadata();
        for (Map.Entry<String, Object> entry : htblColNameValue.entrySet()) {
            String columnName = entry.getKey();
            Object columnValue = entry.getValue();

            if (columnName.trim().length() == 0) {
                return false;
            }

            if (!tableHasColumn(strTableName, columnName)) {
                return false;
            }

            String columnType = getColumnType(strTableName, columnName);
            // System.out.println("column " + columnName + " is of type: " + columnType);
            // System.out.println(columnType + " " + columnValue.getClass().getName());
            if (columnType.equals("java.lang.Integer")) {
                // System.out.println("column is an Integer");
                if (!(columnValue instanceof Integer)) {
                    return false;
                }
            } else if (columnType.equals("java.lang.Double")) {
                // System.out.println("column is a Double");
                if (!(columnValue instanceof Double)) {
                    return false;
                }
            } else if (columnType.equals("java.lang.String")) {
                // System.out.println("column is a string");
                if (!(columnValue instanceof String && ((String) columnValue).trim().length() > 0)) {
                    return false;
                }
            }
        }
        return true;
    }

    public static boolean indexExists(String strIndexName) {
        readMetadata();
        for (Map.Entry<String, Hashtable<String, Hashtable<String, String>>> tableEntry : ht.entrySet()) {
            for (Map.Entry<String, Hashtable<String, String>> columnEntry : tableEntry.getValue().entrySet()) {
                if (columnEntry.getValue().get("indexName").equals(strIndexName)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static void addIndex(String strTableName, String strColName, String strIndexName) {
        readMetadata();
        ht.get(strTableName).get(strColName).put("indexName", strIndexName);
        ht.get(strTableName).get(strColName).put("indexType", "B+tree");
        saveMetadata();
    }

    public static Vector<BTree> getIndicesOnTable(String strTableName) {
        readMetadata();
        Vector<BTree> indices = new Vector<BTree>();
        for (Map.Entry<String, Hashtable<String, String>> entry : ht.get(strTableName).entrySet()) {
            String indexName = entry.getValue().get("indexName");
            if (!indexName.equals("null")) {
                indices.add(BTree.loadIndex(indexName));
            }
        }
        return indices;
    }

    public static String getAllTables() {
        readMetadata();
        String tablesString = "";

        for (String tableName : ht.keySet()) {
            String tableDetails = tableName + "(";

            for (String columnName : ht.get(tableName).keySet()) {
                tableDetails += columnName + ",";
            }

            tableDetails = tableDetails.substring(0, tableDetails.length() - 1);

            tablesString += tableDetails + ")" + "\n";
        }

        return tablesString;
    }

    public static void main(String[] args) throws DBAppException {

        // // System.out.println(tableHasColumn("Student", "name"));
        // // System.out.println(tableHasColumn("Student", "moski"));

        // System.out.println(tableHasIndexOnColumn("Student", "id"));
        // System.out.println(tableHasIndexOnColumn("Student", "age"));

        long startTime = System.nanoTime();

        // Call the method you want to time

        for (int i = 0; i < 10000; i++) {
            // readMetadata();
            saveMetadata();
        }

        long endTime = System.nanoTime();

        // Calculate the elapsed time in milliseconds
        long elapsedTimeInMillis = (endTime - startTime) / 1_000_000;

        System.out.println("Elapsed time: " + elapsedTimeInMillis + " milliseconds");
    }
}
