package com.library.config;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;

import java.io.IOException;

/**
 * HBaseConnection provides a simple, reusable utility for creating and
 * managing connections to a local HBase instance.
 *
 * ZooKeeper quorum: localhost
 */
public class HBaseConnection {

    private static final String ZOOKEEPER_QUORUM = "localhost";
    private static final String ZOOKEEPER_PORT   = "2181";

    /**
     * Builds and returns an HBase Configuration pointing to the local instance.
     */
    public static Configuration createConfiguration() {
        Configuration config = HBaseConfiguration.create();
        config.set("hbase.zookeeper.quorum", ZOOKEEPER_QUORUM);
        config.set("hbase.zookeeper.property.clientPort", ZOOKEEPER_PORT);
        return config;
    }

    /**
     * Opens and returns a new HBase Connection.
     * Callers are responsible for closing the connection.
     */
    public static Connection getConnection() throws IOException {
        Configuration config = createConfiguration();
        return ConnectionFactory.createConnection(config);
    }

    /**
     * Connection test — verifies connectivity by listing all HBase tables.
     * Reports success or prints the exact exception on failure.
     */
    public static void main(String[] args) {
        System.out.println("==============================================");
        System.out.println(" HBase Connection Test");
        System.out.println(" ZooKeeper quorum : " + ZOOKEEPER_QUORUM);
        System.out.println(" ZooKeeper port   : " + ZOOKEEPER_PORT);
        System.out.println("==============================================");

        try (Connection connection = getConnection();
             Admin admin = connection.getAdmin()) {

            System.out.println("\n[SUCCESS] Connected to HBase successfully.");
            System.out.println("[INFO]    HBase version: " + admin.getClusterMetrics().getHBaseVersion());

            TableName[] tables = admin.listTableNames();
            if (tables.length == 0) {
                System.out.println("[INFO]    No tables found (cluster is empty).");
            } else {
                System.out.println("[INFO]    Tables found (" + tables.length + "):");
                for (TableName table : tables) {
                    System.out.println("            - " + table.getNameAsString());
                }
            }

        } catch (IOException e) {
            System.err.println("\n[FAILED] Could not connect to HBase.");
            System.err.println("[ERROR]  " + e.getClass().getName() + ": " + e.getMessage());
            e.printStackTrace(System.err);
        }
    }
}
