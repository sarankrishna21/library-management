package com.library.config;

import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.ColumnFamilyDescriptor;
import org.apache.hadoop.hbase.client.ColumnFamilyDescriptorBuilder;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.TableDescriptor;
import org.apache.hadoop.hbase.client.TableDescriptorBuilder;

import java.io.IOException;

/**
 * Initializer utility for creating HBase tables for the Library System:
 *   - library_books        (CF: info)
 *   - users                (CF: info)
 *   - library_transactions (CF: info)
 */
public class LibraryTableInitializer {

    public static final TableName TABLE_LIBRARY_BOOKS = TableName.valueOf("library_books");
    public static final TableName TABLE_USERS = TableName.valueOf("users");
    public static final TableName TABLE_TRANSACTIONS = TableName.valueOf("library_transactions");
    public static final String CF_INFO = "info";

    /**
     * Initializes all required HBase tables safely.
     */
    public static void initializeTables(Admin admin) throws IOException {
        createTableIfNotExists(admin, TABLE_LIBRARY_BOOKS);
        createTableIfNotExists(admin, TABLE_USERS);
        createTableIfNotExists(admin, TABLE_TRANSACTIONS);
    }

    private static void createTableIfNotExists(Admin admin, TableName tableName) throws IOException {
        if (admin.tableExists(tableName)) {
            System.out.println("[INFO] Table already exists — skipping creation: " + tableName.getNameAsString());
            return;
        }

        System.out.println("[INFO] Creating table: " + tableName.getNameAsString() + " with column family: " + CF_INFO);
        ColumnFamilyDescriptor cfInfo = ColumnFamilyDescriptorBuilder
                .newBuilder(CF_INFO.getBytes())
                .build();

        TableDescriptor tableDescriptor = TableDescriptorBuilder
                .newBuilder(tableName)
                .setColumnFamily(cfInfo)
                .build();

        admin.createTable(tableDescriptor);
        System.out.println("[SUCCESS] Table created: " + tableName.getNameAsString());
    }

    public static void main(String[] args) {
        System.out.println("==============================================");
        System.out.println(" PHASE 9 — TRANSACTION TABLE INITIALIZATION");
        System.out.println(" Target table: library_transactions");
        System.out.println(" Column family: info");
        System.out.println("==============================================");

        try (Connection connection = HBaseConnection.getConnection();
             Admin admin = connection.getAdmin()) {

            initializeTables(admin);

            System.out.println("\n[VERIFY] Current HBase tables:");
            for (TableName t : admin.listTableNames()) {
                System.out.println("         - " + t.getNameAsString());
            }

        } catch (IOException e) {
            System.err.println("\n[FAILED] Table initialization failed: " + e.getMessage());
            e.printStackTrace(System.err);
            System.exit(1);
        }
    }
}
