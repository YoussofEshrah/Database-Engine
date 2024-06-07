package com.halberdski.model;

import java.io.Serializable;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;

public class Tuple implements Serializable {
    private Hashtable<String, Object> colNameVal;
    private String primaryKeyName;
    private Object primaryKey;

    public String getPrimaryKeyName() {
        return primaryKeyName;
    }

    public Tuple(Hashtable<String, Object> colNameVal, String primaryKeyName) {
        this.colNameVal = colNameVal;

        this.primaryKeyName = primaryKeyName;

        for (Map.Entry<String, Object> entry : colNameVal.entrySet()) {

            String key = entry.getKey();

            if (key.equals(primaryKeyName)) {
                this.primaryKey = entry.getValue();
            }
        }
    }

    public Object getPrimaryKey() {
        return this.primaryKey;
    }

    public Hashtable<String, Object> getColNameVal() {
        return this.colNameVal;
    }

    public int compareTo(Tuple o, String columnName) {
        if (colNameVal.get(columnName) instanceof String) {
            return ((String) this.colNameVal.get(columnName)).compareTo(((String) o.colNameVal.get(columnName)));
        }
        if (colNameVal.get(columnName) instanceof Integer) {
            return ((Integer) this.colNameVal.get(columnName)).compareTo((Integer) o.colNameVal.get(columnName));

        }
        if (colNameVal.get(columnName) instanceof Double) {
            return ((Double) this.colNameVal.get(columnName)).compareTo((Double) o.primaryKey);
        }

        return 0;
    }

    public boolean equals(Tuple t) {
        for (String key : t.colNameVal.keySet()) {
            if (!this.colNameVal.containsKey(key) || !t.colNameVal.get(key).equals(this.colNameVal.get(key))) {
                return false;
            }
        }
        for (String key : this.colNameVal.keySet()) {
            if (!t.colNameVal.containsKey(key) || !this.colNameVal.get(key).equals(t.colNameVal.get(key))) {
                return false;
            }
        }
        return t.primaryKey.equals(this.primaryKey) && this.primaryKeyName.equals(t.primaryKeyName);
    }

    public boolean matchesCriteria(Hashtable<String, Object> criteria) {
        for (String key : criteria.keySet()) {
            if (!this.colNameVal.containsKey(key) || !criteria.get(key).equals(this.colNameVal.get(key))) {
                return false;
            }
        }
        return true;
    }

    public int compareWithValue(String colName, Object value) {
        if (value instanceof String) {
            return ((String) this.colNameVal.get(colName)).compareTo((String) value);
        }
        if (value instanceof Integer) {
            return ((Integer) this.colNameVal.get(colName)).compareTo((Integer) value);
        }
        if (value instanceof Double) {
            return ((Double) this.colNameVal.get(colName)).compareTo((Double) value);
        }
        return 404;
    }

    @Override
    public String toString() {
        String result = "";
        result += this.primaryKey;

        for (Map.Entry<String, Object> entry : colNameVal.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            if (!key.equals(primaryKeyName)) {
                result += "," + value;
            }
        }

        return result;
    }

    @SuppressWarnings("rawtypes")
    public void addDataToAvailableIndices(String tableName, String pageName) {
        Vector<BTree> indices = Metadata.getIndicesOnTable(tableName);
        if (indices.isEmpty()) {
            return;
        }
        for (Map.Entry<String, Object> entry : this.colNameVal.entrySet()) {
            String columnName = entry.getKey();
            if (!Metadata.tableHasIndexOnColumn(tableName, columnName)) {
                continue;
            }
            Object value = entry.getValue();
            String indexName = Metadata.getIndexName(tableName, columnName);
            BTree index = BTree.loadIndex(indexName);
            index.insert((Comparable) value, pageName);
            index.saveIndex();
        }
    }

    @SuppressWarnings("rawtypes")
    public void deleteDataFromAvailableIndices(String tableName, String pageName) {
        Vector<BTree> indices = Metadata.getIndicesOnTable(tableName);
        if (indices.isEmpty()) {
            return;
        }
        for (Map.Entry<String, Object> entry : this.colNameVal.entrySet()) {
            String columnName = entry.getKey();
            if (!Metadata.tableHasIndexOnColumn(tableName, columnName)) {
                continue;
            }
            Object value = entry.getValue();
            String indexName = Metadata.getIndexName(tableName, columnName);
            BTree index = BTree.loadIndex(indexName);
            index.delete((Comparable) value, pageName);
            index.saveIndex();
        }
    }

    public static void main(String[] args) {
        Hashtable<String, Object> ht = new Hashtable<>();
        ht.put("id", 1);
        ht.put("age", 15);
        ht.put("name", "moski");
        String primaryKey = "id";

        Tuple tuple = new Tuple(ht, primaryKey);

        // System.out.println(tuple.primaryKey);

        System.out.println(tuple);
    }
}
