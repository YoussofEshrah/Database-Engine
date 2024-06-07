package com.halberdski.model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.security.spec.ECGenParameterSpec;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.text.TabExpander;

import com.halberdski.engine.DBApp;
import com.halberdski.engine.SQLTerm;
import com.halberdski.engine.utility;
import com.halberdski.exceptions.DBAppException;

@SuppressWarnings("unused")
public class Table implements Serializable {
    private String strTableName;
    private String primaryKeyName;
    private Hashtable<String, String> htblColNameType;
    private Vector<String> pagesList;
    private Vector<Vector<Tuple>> pageRanges;
    private int currentPageID;
    private int maxTupleCount;

    private static String pathToTablesFolder = "./src/main/resources/Serialized_Tables/";
    private static String pathToConfig = "./src/main/resources/DBApp.config";

    public Table(String strTableName, String primaryKeyName, Hashtable<String, String> htblColNameType)
            throws DBAppException {
        this.strTableName = strTableName;
        this.primaryKeyName = primaryKeyName;
        this.htblColNameType = htblColNameType;
        this.pagesList = new Vector<String>();
        this.pageRanges = new Vector<>();
        this.currentPageID = 1;

        loadMaxTuplesCount();
    }

    public String getStrTableName() {
        return strTableName;
    }

    public void loadMaxTuplesCount() throws DBAppException {

        // System.out.println(this.getClass().getProtectionDomain().getCodeSource().getLocation());

        try {
            BufferedReader reader = new BufferedReader(new FileReader(Table.pathToConfig));
            String line;

            while ((line = reader.readLine()) != null) {
                if (line.startsWith("MaximumRowsCountinPage")) {
                    this.maxTupleCount = Integer.parseInt(line.split("=")[1].trim());
                    // System.out.println(this.maxTupleCount);
                    break;
                }
            }
            reader.close();
        } catch (IOException e) {
            throw new DBAppException(e.getMessage());
        }
    }

    public void insert(Hashtable<String, Object> htblColNameValue)
            throws DBAppException {
        // System.out.println("Attempting to insert: " + htblColNameValue);
        Tuple tuple = new Tuple(htblColNameValue, this.primaryKeyName);

        if (pagesList.isEmpty()) {
            String pageName = this.strTableName + "" + this.currentPageID;
            this.currentPageID++;

            Page page = new Page(this.strTableName, pageName, this.htblColNameType, this.primaryKeyName,
                    this.maxTupleCount);
            page.addTuple(tuple);
            Vector<Tuple> pageRange = new Vector<>();
            pageRange.add(page.getMin());
            pageRange.add(page.getMax());
            pageRanges.add(pageRange);
            // System.out.println("Succesfully inserted " + tuple.getPrimaryKey() + " into "
            // + this.strTableName
            // + " Page: " + pageName);
            page.savePage();

            pagesList.add(pageName);
            this.saveTable();
            return;
        }

        int low = 0;
        int high = pagesList.size() - 1;
        int insertedPage = -1;
        Tuple overflowTuple = null;
        Vector<Tuple> currentPageRanges = new Vector<>();

        while (low <= high) {
            int mid = (low + high) / 2;
            // int mid = low + (high - low) / 2;
            String currentPageName = pagesList.elementAt(mid);
            Tuple currentPageMin = pageRanges.get(mid).get(0);
            Tuple currentPageMax = pageRanges.get(mid).get(1);
            Page currentPage = Page.loadPage(currentPageName);
            Page nextPage = null;
            Page prevPage = null;
            Tuple nextPageMin = null;
            Tuple nextPageMax = null;
            Tuple prevPageMin = null;
            Tuple prevPageMax = null;
            if (mid > 0) {
                String prevPageName = pagesList.elementAt(mid - 1);
                prevPageMin = pageRanges.get(mid - 1).get(0);
                prevPageMax = pageRanges.get(mid - 1).get(1);
                prevPage = Page.loadPage(prevPageName);
            }
            if (mid < pagesList.size() - 1) {
                String nextPageName = pagesList.elementAt(mid + 1);
                nextPageMin = pageRanges.get(mid + 1).get(0);
                nextPageMax = pageRanges.get(mid + 1).get(1);
                nextPage = Page.loadPage(nextPageName);
            }
            // System.out.println("mid is :" + mid);
            // System.out.println("low is :" + low);
            // System.out.println("high is :" + high);
            // System.out.println("min is :" + currentPage.getMin());
            // System.out.println("max is :" + currentPage.getMax());
            // System.out.println("Looping");
            if ((tuple.compareTo(currentPageMin, primaryKeyName) > 0)
                    && (tuple.compareTo(currentPageMax, primaryKeyName) < 0)) {
                overflowTuple = currentPage.addTuple(tuple);
                // System.out.println("Succesfully inserted " + tuple.getPrimaryKey() + " into "
                // + this.strTableName
                // + " Page: " + currentPageName);
                pageRanges.remove(mid);
                currentPageRanges = new Vector<Tuple>();
                currentPageRanges.add(currentPage.getMin());
                currentPageRanges.add(currentPage.getMax());
                pageRanges.add(mid, currentPageRanges);
                currentPage.savePage();
                insertedPage = mid;
                // System.out.println("1, inserted @ " + mid);
                break;
            } else if (tuple.compareTo(currentPageMax, primaryKeyName) > 0) {

                if (mid == pagesList.size() - 1) {
                    overflowTuple = currentPage.addTuple(tuple);
                    // System.out.println("Succesfully inserted " + tuple.getPrimaryKey() + " into "
                    // + this.strTableName
                    // + " Page: " + currentPageName);
                    pageRanges.remove(mid);
                    currentPageRanges = new Vector<Tuple>();
                    currentPageRanges.add(currentPage.getMin());
                    currentPageRanges.add(currentPage.getMax());
                    pageRanges.add(mid, currentPageRanges);
                    currentPage.savePage();
                    insertedPage = mid;
                    // System.out.println("2, inserted @ " + mid);
                    break;
                }

                if (tuple.compareTo(currentPageMin, primaryKeyName) < 0) {
                    overflowTuple = currentPage.addTuple(tuple);
                    pageRanges.remove(mid);
                    currentPageRanges = new Vector<Tuple>();
                    currentPageRanges.add(currentPage.getMin());
                    currentPageRanges.add(currentPage.getMax());
                    pageRanges.add(mid, currentPageRanges);
                    currentPage.savePage();
                    insertedPage = mid;
                    break;
                }
                low = mid + 1;
                // System.out.println("3rd");
            } else if (tuple.compareTo(currentPageMin, primaryKeyName) < 0) {

                if (mid == 0) {
                    overflowTuple = currentPage.addTuple(tuple);
                    // System.out.println("Succesfully inserted " + tuple.getPrimaryKey() + " into "
                    // + this.strTableName
                    // + " Page: " + currentPageName);
                    pageRanges.remove(mid);
                    currentPageRanges = new Vector<Tuple>();
                    currentPageRanges.add(currentPage.getMin());
                    currentPageRanges.add(currentPage.getMax());
                    pageRanges.add(mid, currentPageRanges);
                    currentPage.savePage();
                    insertedPage = mid;
                    // System.out.println("3, inserted @ " + mid);
                    break;
                }

                if (tuple.compareTo(prevPageMax, primaryKeyName) > 0) {
                    overflowTuple = currentPage.addTuple(tuple);
                    pageRanges.remove(mid);
                    currentPageRanges = new Vector<Tuple>();
                    currentPageRanges.add(currentPage.getMin());
                    currentPageRanges.add(currentPage.getMax());
                    pageRanges.add(mid, currentPageRanges);
                    currentPage.savePage();
                    insertedPage = mid;
                    break;
                }
                high = mid - 1;
                // System.out.println("4th");
            } else {
                throw new DBAppException("Primary Key \"" + tuple.getPrimaryKey() + "\" already in use");
            }
        }
        // System.out.println("exited loop");

        for (int i = insertedPage; i < pagesList.size(); i++) {
            // System.out.println("entred Overflow");
            if (overflowTuple == null) {
                break;
            }
            String currentPageName = pagesList.elementAt(i);
            Page currentPage = Page.loadPage(currentPageName);
            overflowTuple = currentPage.addTuple(overflowTuple);
            pageRanges.remove(i);
            currentPageRanges = new Vector<Tuple>();
            currentPageRanges.add(currentPage.getMin());
            currentPageRanges.add(currentPage.getMax());
            pageRanges.add(i, currentPageRanges);
            // System.out.println("Overflow inserted " + tuple.getPrimaryKey() + " into " +
            // this.strTableName
            // + " Page: " + currentPageName);
            currentPage.savePage();
            if ((overflowTuple != null) && (i == pagesList.size() - 1)) {
                String pageName = this.strTableName + "" + this.currentPageID;
                this.currentPageID++;
                Page page = new Page(strTableName, pageName, this.htblColNameType, this.primaryKeyName,
                        this.maxTupleCount);
                page.addTuple(overflowTuple);
                currentPageRanges = new Vector<Tuple>();
                currentPageRanges.add(page.getMin());
                currentPageRanges.add(page.getMax());
                pageRanges.add(currentPageRanges);
                // System.out.println("Overflow inserted " + tuple.getPrimaryKey() + " into " +
                // this.strTableName
                // + " Page: " + pageName);
                page.savePage();
                pagesList.add(pageName);
                break;
            }
        }
        // System.out.println("page Ranges: " + pageRanges);
        this.saveTable();
    }

    @SuppressWarnings("rawtypes")
    public void delete(String strTableName, Hashtable<String, Object> htblColNameValue)
            throws DBAppException {

        // System.out.println("Page ranges: " + pageRanges);
        Vector<String> pagesToDelete = new Vector<>(pagesList);
        int x;

        for (String colName : htblColNameValue.keySet()) {
            if (Metadata.tableHasIndexOnColumn(strTableName, colName)) {
                BTree index = BTree.loadIndex(Metadata.getIndexName(strTableName, colName));
                Vector<String> pagesInIndex = index.search((Comparable) htblColNameValue.get(colName));
                pagesToDelete.retainAll(pagesInIndex);
            }
        }

        Vector<String> pagesToRemove = new Vector<>();
        Vector<Vector<Tuple>> rangesToRemove = new Vector<>();

        for (int i = 0; i < pagesToDelete.size(); i++) {
            String currentPageName = pagesToDelete.get(i);
            Page currentPage = Page.loadPage(currentPageName);
            // System.out.println("Page Loaded: " + currentPageName);
            Vector<Tuple> pageRange = currentPage.delete(htblColNameValue);
            x = pagesList.indexOf(currentPageName);
            pageRanges.remove(x);
            pageRanges.add(x, pageRange);
            // System.out.println("range to replace : " + pageRange + " index: " + i);
            // System.out.println("new range : " + pageRange + " index: " + i);

            if (pageRange == null) {
                // If the page is empty after deletion, mark it for removal instead of removing
                // it directly
                pagesToRemove.add(currentPageName);
                rangesToRemove.add(pageRange);

                // Delete the page from disk
                File currentPageFile = new File(Table.pathToTablesFolder + currentPageName + ".class");
                currentPageFile.delete();
                // System.out.println("Page removed: " + currentPageName);
                // System.out.println("Range removed: " + i);
            } else {
                currentPage.savePage();
            }
        }

        // Remove empty pages from pagesList
        // System.out.println("Page ranges: " + pageRanges);
        // System.out.println("Pages: " + pagesList);
        // System.out.println("rangesToRemove " + rangesToRemove);
        pagesList.removeAll(pagesToRemove);
        pageRanges.removeAll(rangesToRemove);
        // System.out.println("Pages: " + pagesList);
        // System.out.println("Page ranges: " + pageRanges);
        this.saveTable();
    }

    @SuppressWarnings("rawtypes")
    public Iterator selectTuple(SQLTerm[] arrSQLTerms, String[] strarrOperators)
            throws DBAppException {

        Vector<Vector<Tuple>> result = new Vector<Vector<Tuple>>();

        for (SQLTerm term : arrSQLTerms) {// iterat on sql terms
            String columnName = term._strColumnName;
            String operator = term._strOperator;
            Object value = term._objValue;
            Vector<Tuple> tuples = new Vector<Tuple>();
            Vector<Tuple> termResult = new Vector<Tuple>();

            Vector<String> pagesToSearch = new Vector<String>();

            if (Metadata.tableHasIndexOnColumn(strTableName, columnName)) {
                // get the pages from indexing
                BTree index = BTree.loadIndex(Metadata.getIndexName(strTableName, columnName));
                System.out.println("Used index (" + index.getIndexName() + ") on column (" + columnName + ")");
                switch (operator) {
                    case "=":
                        pagesToSearch.addAll(index.search((Comparable) value, (Comparable) value, true, true));
                        break;
                    case "!=":
                        pagesToSearch.addAll(this.pagesList);
                        break;
                    case "<":
                        pagesToSearch.addAll(index.search(index.getMin(), (Comparable) value, true, false));
                        break;
                    case "<=":
                        pagesToSearch.addAll(index.search(index.getMin(), (Comparable) value, true, true));
                        break;
                    case ">":
                        pagesToSearch.addAll(index.search((Comparable) value, index.getMax(), false, true));
                        break;
                    case ">=":
                        pagesToSearch.addAll(index.search((Comparable) value, index.getMax(), true, true));
                        break;
                }

            } else {
                pagesToSearch.addAll(this.pagesList);
            }

            for (int i = 0; i < pagesToSearch.size(); i++) {
                Page page = Page.loadPage(pagesToSearch.get(i));
                System.out.println("Page Loaded: " + pagesToSearch.get(i));
                tuples = page.selectTuple(columnName, operator, value);
                // -------> serialization not needed because the pages are unchanged
                termResult.addAll(tuples);
            }

            result.add(termResult);
        }
        // System.out.println(result.get(0).get(0).equals(result.get(1).get(0)));
        // System.out.println(result + " " + Arrays.toString(strarrOperators));
        Vector<Tuple> finalResult = combineResults(result, strarrOperators);

        finalResult.sort(new Comparator<Tuple>() {
            @Override
            public int compare(Tuple t1, Tuple t2) {
                return t1.compareTo(t2, primaryKeyName);
            }
        });

        // System.out.println(finalResult);

        return finalResult.iterator();
    }

    private Vector<Tuple> combineResults(Vector<Vector<Tuple>> termResults, String[] strarrOperators)
            throws DBAppException {// editd to combine results with operator presidence in mind
        Vector<Tuple> finalResult = new Vector<Tuple>();
        if (termResults.size() == 1) {
            return termResults.elementAt(0);
        }
        if (termResults.isEmpty()) {
            return finalResult;
        }

        Vector<String> strVOperators = new Vector<String>();
        for (int i = 0; i < strarrOperators.length; i++) {
            strVOperators.add(strarrOperators[i]);
        }

        finalResult.addAll(termResults.get(0));
        Vector<Tuple> operatorResult = new Vector<Tuple>();
        String operator;
        // System.out.println("termResults at beginning " + termResults);

        for (int i = 0; i < strVOperators.size(); i++) {
            operator = strVOperators.get(i);
            Vector<Tuple> currentTermResult = termResults.get(i);
            Vector<Tuple> nextTermResult = termResults.get(i + 1);
            // operatorResult.clear();
            // System.out.println("On loop: " + i + " Operator result is: " +
            // operatorResult);
            if (operator.equals("AND")) {
                // System.out.println("operatorResult Address: " +
                // System.identityHashCode(operatorResult)
                // + " currentTerm result Address: " + System
                // .identityHashCode(currentTermResult));
                // System.out.println("Current operator result is: " + operatorResult);
                // System.out.println("Current term result is: " + currentTermResult);
                for (int j = 0; j < nextTermResult.size(); j++) {
                    // System.out.println("entered operator loop, operator: " + operator + "
                    // currentTermRes: "
                    // + currentTermResult + " nextTermRes(j): " + nextTermResult.get(j));
                    // System.out.println("j= " + j);
                    // System.out.println(currentTermResult.contains(nextTermResult.get(j)));
                    // System.out.println(Contains(currentTermResult, nextTermResult.get(j)));
                    // System.out.println(currentTermResult.get(j));
                    // System.out.println(currentTermResult.get(0).equals(nextTermResult.get(0)));
                    if (Contains(currentTermResult, nextTermResult.get(j))
                            && !Contains(operatorResult, nextTermResult.get(j))) {
                        operatorResult.add(nextTermResult.get(j));

                        // System.out.println("added to operator result: " + nextTermResult.get(j));
                    }
                }
                // System.out.println("Operator Result " + operatorResult);
                // System.out.println("termResults before replace " + termResults + "i =" + i);
                termResults.remove(i);
                termResults.add(i, operatorResult);
                // System.out.println("termResults after replace,before delete " + termResults +
                // "i =" + i);
                termResults.remove(i + 1);
                // System.out.println("termResults after delete " + termResults + "i =" + i);
                strVOperators.remove(i);
                i -= 1;
            }
        }

        for (int i = 0; i < strVOperators.size(); i++) {
            operator = strVOperators.get(i);
            Vector<Tuple> currentTermResult = termResults.get(i);
            Vector<Tuple> nextTermResult = termResults.get(i + 1);
            if (operator.equals("XOR")) {
                // System.out.println("operatorResult Address: " +
                // System.identityHashCode(operatorResult)
                // + " currentTerm result Address: " + System
                // .identityHashCode(currentTermResult));
                // System.out.println("Current operator result is: " + operatorResult);
                operatorResult = new Vector<Tuple>();
                // System.out.println("Operator result cleared");
                // System.out.println("Current term result is: " + currentTermResult);
                for (int j = 0; j < currentTermResult.size(); j++) {
                    if (!Contains(nextTermResult, currentTermResult.get(j))
                            && !Contains(operatorResult, currentTermResult.get(j))) {
                        // System.out.println(currentTermResult.get(j) + " Added to operator result");
                        operatorResult.add(currentTermResult.get(j));

                    }
                }
                for (int k = 0; k < nextTermResult.size(); k++) {
                    if (!Contains(currentTermResult, nextTermResult.get(k))
                            && !Contains(operatorResult, nextTermResult.get(k))) {

                        // System.out.println(nextTermResult.get(k) + " Added to operator result");
                        operatorResult.add(nextTermResult.get(k));

                    }
                }
                // System.out.println("Operator Result " + operatorResult);
                // System.out.println("termResults before replace " + termResults + "i =" + i);
                termResults.remove(i);
                termResults.add(i, operatorResult);
                // System.out.println("termResults after replace,before delete " + termResults +
                // "i =" + i);
                termResults.remove(i + 1);
                // System.out.println("termResults after delete " + termResults + "i =" + i);
                strVOperators.remove(i);
                i -= 1;
            }
        }

        for (int i = 0; i < strVOperators.size(); i++) {
            operator = strVOperators.get(i);
            Vector<Tuple> currentTermResult = termResults.get(i);
            Vector<Tuple> nextTermResult = termResults.get(i + 1);
            if (operator.equals("OR")) {
                // System.out.println("operatorResult Address: " +
                // System.identityHashCode(operatorResult)
                // + " currentTerm result Address: " + System
                // .identityHashCode(currentTermResult));
                // System.out.println("Current operator result is: " + operatorResult);
                operatorResult = new Vector<Tuple>(); // this makes operator result and curreTermResult stop sharing the
                                                      // same address
                // System.out.println("Operator result cleared");
                // System.out.println("Current term result is: " + currentTermResult);
                operatorResult.addAll(currentTermResult);
                for (int j = 0; j < nextTermResult.size(); j++) {
                    // System.out.println("entered operator loop, operator: " + operator + "
                    // currentTermRes: "
                    // + currentTermResult + " nextTermRes(j): " + nextTermResult.get(j));
                    // System.out.println(!currentTermResult.contains(nextTermResult.get(j)));
                    if (!Contains(currentTermResult, nextTermResult.get(j))
                            && !Contains(operatorResult, nextTermResult.get(j))) {

                        operatorResult.add(nextTermResult.get(j));
                        // System.out.println("Added to operator result: " + nextTermResult.get(j));
                    }
                }
                // System.out.println("Operator Result " + operatorResult);
                // System.out.println("termResults before replace " + termResults + "i =" + i);
                termResults.remove(i);
                termResults.add(i, operatorResult);
                // System.out.println("termResults after replace,before delete " + termResults +
                // "i =" + i);
                termResults.remove(i + 1);
                // System.out.println("termResults after delete " + termResults + "i =" + i);
                strVOperators.remove(i);
                i -= 1;
            } else {
                throw new DBAppException("invalid Operator");
            }
        }
        finalResult = termResults.get(0);
        // finalResult.addAll(termResults.get(1));
        // System.out.println("Final term results " + termResults);
        return finalResult;
    }

    public static boolean Contains(Vector<Tuple> vec, Tuple tuple) {
        for (int i = 0; i < vec.size(); i++) {
            if (vec.get(i).equals(tuple)) {
                return true;
            }
        }
        return false;
    }

    public static String[] removeOperator(String[] array, String operatorToRemove) {
        int count = 0;
        for (String str : array) {
            if (str.equals(operatorToRemove)) {
                count++;
            }
        }
        String[] newArray = new String[array.length - count];
        int index = 0;
        for (String str : array) {
            if (!str.equals(operatorToRemove)) {
                newArray[index++] = str;
            }
        }
        return newArray;
    }

    public void updateTuple(Object strClusteringKeyValue, Hashtable<String, Object> htblColNameValue)
            throws DBAppException {
        Hashtable<String, Object> tempHashtable = new Hashtable<String, Object>();
        tempHashtable.put(this.primaryKeyName, strClusteringKeyValue);
        Tuple tuple = new Tuple(tempHashtable, this.primaryKeyName);

        int low = 0;
        int high = pagesList.size() - 1;
        while (low <= high) {
            int mid = low + (high - low) / 2;
            String currentPageName = pagesList.get(mid);
            Tuple currentPageMin = pageRanges.get(mid).get(0);
            Tuple currentPageMax = pageRanges.get(mid).get(1);
            Page currentPage = Page.loadPage(currentPageName);

            if (tuple.compareTo(currentPageMin, tuple.getPrimaryKeyName()) >= 0
                    && tuple.compareTo(currentPageMax, tuple.getPrimaryKeyName()) <= 0) {
                currentPage.updateTuple(strClusteringKeyValue, htblColNameValue);
                currentPage.savePage();
                return;
            } else if (tuple.compareTo(currentPageMin, tuple.getPrimaryKeyName()) < 0) {
                high = mid - 1;
            } else {
                low = mid + 1;
            }
        }
        throw new DBAppException("No record with primary key " + strClusteringKeyValue + " found in table");
    }

    public void createIndex(String strColName, String strIndexName) throws DBAppException {

        BTree index = new BTree(2, strIndexName);

        // Loop on table and add the values to the index

        for (String pageName : pagesList) {
            Page page = Page.loadPage(pageName);
            page.addAllToIndex(index, strColName);
        }

        index.saveIndex();
    }

    public String toString() {
        String result = "";
        result += strTableName + " Table \n\n\n";
        for (int i = 0; i < pagesList.size(); i++) {
            result += "Page " + (i + 1) + ":\n";
            result += "-------------------------------\n";
            Page currentPage = null;
            try {
                currentPage = Page.loadPage(pagesList.elementAt(i));
                result += currentPage.toString() + "\n";
            } catch (DBAppException e) {

                result += "Error reading page at index + " + i + "\n";
            }
            result += "-------------------------------\n";
        }

        return result;
    }

    public void saveTable() {
        // Store table on disk
        try {
            FileOutputStream fileOut = new FileOutputStream(
                    Table.pathToTablesFolder + this.getStrTableName() + ".class");
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(this);
            out.close();
            fileOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Table loadTable(String strTableName) {
        Table myTable = null;
        // Load table from disk
        try {
            FileInputStream fileIn = new FileInputStream(Table.pathToTablesFolder + strTableName + ".class");
            ObjectInputStream in = new ObjectInputStream(fileIn);
            myTable = (Table) in.readObject();
            in.close();
            fileIn.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return myTable;
    }

    // @SuppressWarnings("unchecked")
    public static void main(String[] args) throws DBAppException {
        // Integer x = 3;
        // Integer y = 5;
        // System.out.println(x.compareTo(y));

        String strTableName = "Student";
        Hashtable<String, String> htblColNameType = new Hashtable<String, String>();
        htblColNameType.put("id", "java.lang.Integer");
        htblColNameType.put("name", "java.lang.String");
        htblColNameType.put("gpa", "java.lang.Double");
        Table myTable = new Table(strTableName, "id", htblColNameType);
        myTable.saveTable();
        Metadata.addTable(strTableName, strTableName, htblColNameType);

        // for (int i = 1; i <= 10; i++) {

        // Hashtable<String, Object> htblColNameValue = new Hashtable<>();
        // htblColNameValue.put("id", i);
        // htblColNameValue.put("name", "Moski no " + i);
        // htblColNameValue.put("gpa", 3.5);
        // myTable.insert(htblColNameValue);

        // }
        // Hashtable<String, Object> htblColNameValue = new Hashtable<>();
        // htblColNameValue.put("name", "7aramy no " + 6);
        // htblColNameValue.put("gpa", 3.5);
        // System.out.println(myTable);
        // myTable.updateTuple(1, htblColNameValue);
        // System.out.println(myTable);
        // SQLTerm A = new SQLTerm();
        // SQLTerm B = new SQLTerm();
        // A._strTableName = "Student";
        // A._strColumnName = "id";
        // A._objValue = 6;
        // A._strOperator = ">";
        // B._strTableName = "Student";
        // B._strColumnName = "id";
        // B._objValue = 7;
        // B._strOperator = "=";
        // SQLTerm[] arr = { A, B };
        // String[] ops = { "AND" };
        // Page page = Page.loadPage("Student2");
        // System.out.println(page);
        // Vector<Tuple> Vec1 = page.selectTuple("id", "=", 7);
        // Vector<Tuple> Vec2 = page.selectTuple("id", ">", 6);
        // System.out.println("vec1 " + Vec1);
        // System.out.println("vec2 " + Vec2);
        // // System.out.println(Vec2.contains(Vec1.get(0)));
        // Vector<Vector<Tuple>> VecVec = new Vector<>();
        // VecVec.add(Vec2);
        // VecVec.add(Vec1);

        // System.out.println(Contains(Vec2, Vec1.get(0)));
        // System.out.println("vecvec " + VecVec);
        // System.out.println(myTable.combineResults(VecVec, ops));

        // System.out.println(myTable.selectTuple(arr, ops));

        // Iterator<Tuple> iterator = myTable.selectTuple(arr, ops);

        // while (iterator.hasNext()) {
        // System.out.println(iterator.next());
        // }

        System.out.println("-------------------------------------------------");

        // SQLTerm A2 = new SQLTerm();
        // SQLTerm B2 = new SQLTerm();
        // A2._strTableName = "Student";
        // A2._strColumnName = "id";
        // A2._objValue = 6;
        // A2._strOperator = ">=";
        // B2._strTableName = "Student";
        // B2._strColumnName = "gpa";
        // B2._objValue = 3.5;
        // B2._strOperator = ">";
        // SQLTerm[] arr2 = { A2, B2 };
        // String[] ops2 = { "OR" };

        // Iterator<Tuple> iterator2 = myTable.selectTuple(arr2, ops2);

        // while (iterator2.hasNext()) {
        // System.out.println(iterator2.next());
        // }

        // --------------WORK--------------
        // for (int i = 10; i >= 1; i--) {
        // Hashtable<String, Object> htblColNameValue = new Hashtable<>();
        // htblColNameValue.put("id", i);
        // htblColNameValue.put("name", "Moski no " + i);
        // htblColNameValue.put("gpa", 3.5);
        // myTable.insert(htblColNameValue);
        // }
        // System.out.println(myTable);

        int x = 20;
        Integer[] array = new Integer[x];

        for (int i = 0; i < x; i++) {
            array[i] = i + 1;
        }
        List<Integer> list = Arrays.asList(array);

        // Shuffle the list
        Collections.shuffle(list);

        // Convert back to array if necessary
        list.toArray(array);
        System.out.println(Arrays.toString(array));

        for (int i = 0; i < array.length; i++) {
            int num = array[i];
            Hashtable<String, Object> htblColNameValue = new Hashtable<>();
            htblColNameValue.put("id", num);
            htblColNameValue.put("name", "Moski no " + num);
            htblColNameValue.put("gpa", 3.5);
            System.out.println("inserting element no: " + i);
            myTable.insert(htblColNameValue);
        }
        System.out.println(myTable);

        // Page firstPage = myTable.pagesList.firstElement();

        // System.out.println(myTable.pagesList.firstElement().size());
        // System.out.println(firstPage);

    }
}
