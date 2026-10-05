package com.library.config;

import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.ColumnFamilyDescriptor;
import org.apache.hadoop.hbase.client.ColumnFamilyDescriptorBuilder;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.TableDescriptor;
import org.apache.hadoop.hbase.client.TableDescriptorBuilder;

import java.io.IOException;

/**
 * LibraryTableInitializer creates the HBase tables required by the
 * Library Management System.
 *
 * Tables managed by this initializer:
 *   - library_books  (column family: info)
 *
 * Safe to run multiple times: tables are only created if they do not
 * already exist.
 */
public class LibraryTableInitializer {

    /** HBase table name */
    public static final TableName TABLE_LIBRARY_BOOKS =
            TableName.valueOf("library_books");

    /** Single column family */
    public static final String CF_INFO = "info";

    /**
     * Creates the library_books table if it does not already exist.
     *
     * @param admin an open HBase Admin instance
     * @throws IOException if the HBase operation fails
     */
    public static void initializeTables(Admin admin) throws IOException {

        if (admin.tableExists(TABLE_LIBRARY_BOOKS)) {
            System.out.println("[INFO]  Table already exists — skipping creation: "
                    + TABLE_LIBRARY_BOOKS.getNameAsString());
            return;
        }

        System.out.println("[INFO]  Table not found. Creating: "
                + TABLE_LIBRARY_BOOKS.getNameAsString());

        // Define column family: info
        ColumnFamilyDescriptor cfInfo = ColumnFamilyDescriptorBuilder
                .newBuilder(CF_INFO.getBytes())
                .build();

        // Define table descriptor
        TableDescriptor tableDescriptor = TableDescriptorBuilder
                .newBuilder(TABLE_LIBRARY_BOOKS)
                .setColumnFamily(cfInfo)
                .build();

        admin.createTable(tableDescriptor);

        System.out.println("[SUCCESS] Table created successfully: "
                + TABLE_LIBRARY_BOOKS.getNameAsString()
                + " | Column family: " + CF_INFO);
    }

    public static void main(String[] args) {
        System.out.println("==============================================");
        System.out.println(" Library Table Initializer");
        System.out.println(" Target table  : library_books");
        System.out.println(" Column family : info");
        System.out.println("==============================================");

        try {
            System.out.println("[STEP 1] Creating HBase Connection...");
            Connection connection = HBaseConnection.getConnection();
            System.out.println("[STEP 1 SUCCESS] Connection created.");

            System.out.println("[STEP 2] Getting Admin instance...");
            Admin admin = connection.getAdmin();
            System.out.println("[STEP 2 SUCCESS] Admin retrieved.");

            System.out.println("[STEP 3] Initializing tables...");
            initializeTables(admin);
            System.out.println("[STEP 3 SUCCESS] Tables initialized.");

            // Verify by listing tables
            System.out.println("\n[VERIFY] Tables currently in HBase:");
            for (TableName t : admin.listTableNames()) {
                System.out.println("         - " + t.getNameAsString());
            }

            System.out.println("[STEP 4] Closing Admin and Connection...");
            admin.close();
            connection.close();
            System.out.println("[STEP 4 SUCCESS] Cleanly closed.");

        } catch (Throwable e) {
            System.err.println("\n[FAILED] Table initialization failed.");
            System.err.println("[ERROR]  " + e.getClass().getName() + ": " + e.getMessage());
            e.printStackTrace(System.err);
            System.exit(1);
        }
    }
}
