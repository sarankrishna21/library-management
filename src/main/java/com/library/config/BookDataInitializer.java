package com.library.config;

import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;

/**
 * BookDataInitializer inserts a single sample book record (BOOK001) into
 * the library_books HBase table and reads it back to verify insertion.
 */
public class BookDataInitializer {

    private static final TableName TABLE_NAME = TableName.valueOf("library_books");
    private static final byte[] CF_INFO = Bytes.toBytes("info");

    public static void main(String[] args) {
        System.out.println("==============================================");
        System.out.println(" Book Data Initializer — Sample Book Insertion");
        System.out.println(" Table  : library_books");
        System.out.println(" Row Key: BOOK001");
        System.out.println("==============================================");

        try (Connection connection = HBaseConnection.getConnection();
             Table table = connection.getTable(TABLE_NAME)) {

            String rowKey = "BOOK001";
            byte[] rowBytes = Bytes.toBytes(rowKey);

            // 1. Prepare Put object
            Put put = new Put(rowBytes);
            put.addColumn(CF_INFO, Bytes.toBytes("title"), Bytes.toBytes("The Alchemist"));
            put.addColumn(CF_INFO, Bytes.toBytes("author"), Bytes.toBytes("Paulo Coelho"));
            put.addColumn(CF_INFO, Bytes.toBytes("category"), Bytes.toBytes("Fiction"));
            put.addColumn(CF_INFO, Bytes.toBytes("price"), Bytes.toBytes("299"));
            put.addColumn(CF_INFO, Bytes.toBytes("available"), Bytes.toBytes("true"));

            // 2. Insert record into HBase
            System.out.println("\n[STEP 1] Inserting sample book record (BOOK001)...");
            table.put(put);
            System.out.println("[SUCCESS] BOOK001 inserted successfully into HBase.");

            // 3. Read the record back to verify
            System.out.println("\n[STEP 2] Reading book record (BOOK001) back from HBase to verify...");
            Get get = new Get(rowBytes);
            Result result = table.get(get);

            if (result.isEmpty()) {
                System.err.println("[ERROR] Record BOOK001 was not found!");
            } else {
                System.out.println("[SUCCESS] Record retrieved successfully!");
                System.out.println("----------------------------------------------");
                System.out.println(" Row Key    : " + Bytes.toString(result.getRow()));
                System.out.println(" Title      : " + Bytes.toString(result.getValue(CF_INFO, Bytes.toBytes("title"))));
                System.out.println(" Author     : " + Bytes.toString(result.getValue(CF_INFO, Bytes.toBytes("author"))));
                System.out.println(" Category   : " + Bytes.toString(result.getValue(CF_INFO, Bytes.toBytes("category"))));
                System.out.println(" Price      : " + Bytes.toString(result.getValue(CF_INFO, Bytes.toBytes("price"))));
                System.out.println(" Available  : " + Bytes.toString(result.getValue(CF_INFO, Bytes.toBytes("available"))));
                System.out.println("----------------------------------------------");
            }

        } catch (IOException e) {
            System.err.println("\n[FAILED] Failed to insert or read sample book.");
            System.err.println("[ERROR]  " + e.getClass().getName() + ": " + e.getMessage());
            e.printStackTrace(System.err);
            System.exit(1);
        }
    }
}
