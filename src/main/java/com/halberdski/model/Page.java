package com.halberdski.model;

import java.io.*;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import com.halberdski.exceptions.DBAppException;

@SuppressWarnings("unused")
public class Page implements Serializable {
    private String strTableName;
    private String pageName;
    private Vector<Tuple> tuples;
    private int tupleCount;
    private int maxTupleCount;
    private String primaryKeyName;
    private Hashtable<String, String> colNameType;
    private Tuple min;
    private Tuple max;

    private static String pathToPagesFolder = "./src/main/resources/Serialized_Pages/";

    public Page(String strTableName, String pageName, Hashtable<String, String> colNameType, String primaryKeyName,
            int maxTupleCount) {
        this.strTableName = strTableName;
        this.pageName = pageName;
        this.tuples = new Vector<Tuple>();
        this.tupleCount = 0;
        this.primaryKeyName = primaryKeyName;
        this.colNameType = colNameType;
        this.maxTupleCount = maxTupleCount;

        min = null;
        max = null;
    }

    public Tuple getMin() {
        return min;
    }

    public Tuple getMax() {
        return max;
    }

    public Tuple addTuple(Tuple tuple) throws DBAppException {
        // returns overflow tuple, null if no overflow

        Object primaryKeyValue = null;

        // To be moved to DBApp using metadata or table class
        if (tuple.getColNameVal().containsKey(this.primaryKeyName)) {
            primaryKeyValue = tuple.getColNameVal().get(this.primaryKeyName);
        }

        for (Tuple currentTuple : this.tuples) {
            if (currentTuple.getColNameVal().get(primaryKeyName).equals(primaryKeyValue)) {
                throw new DBAppException("Primary Key \"" + primaryKeyValue + "\" already in use");
            }
        }

        int index = locateCorrectPosition(tuple);
        tuples.insertElementAt(tuple, index);

        this.tupleCount += 1;

        Tuple overflow = null;

        tuple.addDataToAvailableIndices(this.strTableName, this.pageName);

        if (this.tupleCount > this.maxTupleCount) {
            this.tupleCount -= 1;
            overflow = tuples.remove(this.maxTupleCount);
            overflow.deleteDataFromAvailableIndices(this.strTableName, this.pageName);

            // System.out.println("Overflow: " + overflow.toString());
            // returns the last tuple
            // (not intended behavior, but it's the closest thing to an overflow tuple)
        }

        // System.out.println(this.tuples);

        min = this.tuples.firstElement();
        max = this.tuples.lastElement();

        return overflow;
    }

    private int locateCorrectPosition(Tuple tuple) {
        // returns the correct index for the tuple in the page
        // using binary search

        int low = 0;
        int high = this.tuples.size() - 1;

        while (low <= high) {
            int mid = (low + high) / 2;
            Tuple currentTuple = this.tuples.get(mid);

            if (currentTuple.compareTo(tuple, primaryKeyName) < 0) {
                low = mid + 1;
            } else if (currentTuple.compareTo(tuple, primaryKeyName) > 0) {
                high = mid - 1;
            } else {
                return mid;
            }
        }

        return low;
    }

    public Vector<Tuple> delete(Hashtable<String, Object> x) {
        Vector<Tuple> pageRange = new Vector<>();
        for (int i = 0; i < this.tuples.size(); i++) {
            Tuple currentTuple = this.tuples.get(i);
            if (currentTuple.matchesCriteria(x)) {
                this.tuples.remove(i);
                currentTuple.deleteDataFromAvailableIndices(this.strTableName, this.pageName);
                this.tupleCount -= 1;
                i--;
            }
        }
        if (!this.tuples.isEmpty()) {
            this.min = this.tuples.firstElement();
            this.max = this.tuples.lastElement();
            pageRange.add(min);
            pageRange.add(max);
            return pageRange;
        }

        return null;
    }

    public Vector<Tuple> selectTuple(String columnName, String operator, Object value) throws DBAppException {
        Vector<Tuple> selectedTuples = new Vector<Tuple>();

        Hashtable<String, Object> colNameVal = new Hashtable<String, Object>();
        colNameVal.put(columnName, value);
        for (Tuple currentTuple : this.tuples) {
            if (operator.equals("=")) {
                if (currentTuple.compareWithValue(columnName, value) == 0) {
                    selectedTuples.add(currentTuple);
                }
            } else if (operator.equals("!=")) {
                if (currentTuple.compareWithValue(columnName, value) != 0) {
                    selectedTuples.add(currentTuple);
                }
            } else if (operator.equals("<")) {
                if (currentTuple.compareWithValue(columnName, value) < 0) {
                    selectedTuples.add(currentTuple);
                }
            } else if (operator.equals("<=")) {
                if (currentTuple.compareWithValue(columnName, value) <= 0) {
                    selectedTuples.add(currentTuple);
                }
            } else if (operator.equals(">")) {
                if (currentTuple.compareWithValue(columnName, value) > 0) {
                    selectedTuples.add(currentTuple);
                }
            } else if (operator.equals(">=")) {
                if (currentTuple.compareWithValue(columnName, value) >= 0) {
                    selectedTuples.add(currentTuple);
                }
            }

        }
        return selectedTuples;
    }

    public void updateTuple(Object oldPrimaryKeyValue, Hashtable<String, Object> newValues)
            throws DBAppException {
        // System.out.println("Primary key to update: " + oldPrimaryKeyValue);
        for (int i = 0; i < tuples.size(); i++) {
            Hashtable<String, Object> ht = this.tuples.get(i).getColNameVal();
            // System.out.println("Current Primary Key: " + ht.get(this.primaryKeyName) + ",
            // ");
            if (ht.get(this.primaryKeyName).equals(oldPrimaryKeyValue)) {
                this.tuples.get(i).deleteDataFromAvailableIndices(this.strTableName, this.pageName);
                for (String colName : newValues.keySet()) {
                    // System.out.println("changed value from " + ht.get(colName) + "to " +
                    // newValues.get(colName));
                    ht.put(colName, newValues.get(colName));
                }
                this.tuples.get(i).addDataToAvailableIndices(this.strTableName, this.pageName);
                return;
            }
        }
        throw new DBAppException("Tuple not found in the page.");
    }

    public boolean isFull() {
        return this.tupleCount == this.maxTupleCount;
    }

    public boolean isEmpty() {
        return this.tupleCount == 0;
    }

    public int size() {
        return this.tuples.size();
    }

    @Override
    public String toString() {
        String result = "  ";

        result += primaryKeyName;

        for (Map.Entry<String, String> entry : colNameType.entrySet()) {
            String key = entry.getKey();
            // Object value = entry.getValue();

            if (!key.equals(primaryKeyName)) {
                result += "  " + key;
            }
        }

        result += "\n";

        for (int i = 0; i < this.tuples.size(); i++) {
            Tuple currentTuple = this.tuples.get(i);
            // result += (i + 1) + " " + currentTuple.toString();
            result += currentTuple.toString();
            result += "\n";
        }

        return result;
    }

    public void savePage() throws DBAppException {
        try {
            FileOutputStream fileOut = new FileOutputStream(Page.pathToPagesFolder +
                    this.pageName + ".class");
            // FileOutputStream fileOut = new FileOutputStream(
            // ".//src//resources//Serialized_Pages//" + this.pageName + ".class");
            ObjectOutputStream ObjectOut = new ObjectOutputStream(fileOut);
            ObjectOut.writeObject(this);
            ObjectOut.close();
            fileOut.close();
            // System.out.println("Page Serialized");
        } catch (FileNotFoundException e) {
            System.out.println(Page.pathToPagesFolder + this.pageName + ".class");
            throw new DBAppException("File not found");
        } catch (IOException e) {
            throw new DBAppException("Error occured while saving page");
        }
    }

    public static Page loadPage(String pageName) throws DBAppException {
        try {
            FileInputStream fileIn = new FileInputStream(Page.pathToPagesFolder + pageName + ".class");
            ObjectInputStream ObjectIn = new ObjectInputStream(fileIn);
            Page page = null;
            page = (Page) ObjectIn.readObject();
            ObjectIn.close();
            fileIn.close();
            // System.out.println("Page Deserialied, tupleCount =" + page.tupleCount);
            return page;
        } catch (FileNotFoundException e) {
            throw new DBAppException("File not found");
        } catch (IOException e) {
            throw new DBAppException("Error occured while saving page");
        } catch (ClassNotFoundException e) {
            throw new DBAppException("Class \"Page\" not found");
        }
    }

    @SuppressWarnings("rawtypes")
    public void addAllToIndex(BTree index, String colName) {
        for (Tuple tuple : this.tuples) {
            index.insert((Comparable) tuple.getColNameVal().get(colName), this.pageName);
        }

    }

    public static void main(String[] args) throws DBAppException {
        // Hashtable<Integer, String> ht = new Hashtable<>();
        // ht.put(1, "Mohamed");
        // System.out.println(ht.get(2));

        // int[] array = { 1, 2, 3, 7, 8, 9 };

        // System.out.println(binarySearch(array, 5));
        // System.out.println(binarySearch(array, 0));
        // System.out.println(binarySearch(array, -1));
        // System.out.println(binarySearch(array, 20));
        // Page page = new Page(new Hashtable<String, String>(), "id");

        Hashtable<String, String> htblColNameType = new Hashtable<>();
        htblColNameType.put("id", "java.lang.Integer");
        htblColNameType.put("name", "java.lang.String");
        htblColNameType.put("gpa", "java.lang.Double");

        Hashtable<String, Object> htblColNameValue = new Hashtable<>();
        htblColNameValue.put("id", 2343432);
        htblColNameValue.put("name", "Ahmed Noor");
        htblColNameValue.put("gpa", 0.95);

        Page page = new Page("Student", "page1", htblColNameType, "id", 5);
        Tuple tuple = new Tuple(htblColNameValue, "id");
        page.addTuple(tuple);
        // System.out.println(page);
        // System.out.println(page.size());

        // System.out.println(page.tupleCount);
        page.savePage();

        Page page1 = null;
        page1 = Page.loadPage("page1");
        // System.out.println(page1.maxTupleCount);
        // System.out.println(page.maxTupleCount);

    }

}