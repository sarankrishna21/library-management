package com.library.repository;

import com.library.config.HBaseConnection;
import com.library.model.LibraryTransaction;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Repository for library_transactions table in HBase.
 */
public class TransactionRepository {

    private static final TableName TABLE_NAME = TableName.valueOf("library_transactions");
    private static final byte[] CF_INFO = Bytes.toBytes("info");

    public boolean exists(String transactionId) throws IOException {
        try (Connection connection = HBaseConnection.getConnection();
             Table table = connection.getTable(TABLE_NAME)) {
            return table.exists(new Get(Bytes.toBytes(transactionId)));
        }
    }

    public void save(LibraryTransaction txn) throws IOException {
        try (Connection connection = HBaseConnection.getConnection();
             Table table = connection.getTable(TABLE_NAME)) {

            byte[] rowKey = Bytes.toBytes(txn.getTransactionId());
            Put put = new Put(rowKey);
            put.addColumn(CF_INFO, Bytes.toBytes("userId"), Bytes.toBytes(txn.getUserId()));
            put.addColumn(CF_INFO, Bytes.toBytes("bookId"), Bytes.toBytes(txn.getBookId()));
            put.addColumn(CF_INFO, Bytes.toBytes("borrowDate"), Bytes.toBytes(txn.getBorrowDate()));
            put.addColumn(CF_INFO, Bytes.toBytes("returnDate"), Bytes.toBytes(txn.getReturnDate()));
            put.addColumn(CF_INFO, Bytes.toBytes("status"), Bytes.toBytes(txn.getStatus()));

            table.put(put);
        }
    }

    public Optional<LibraryTransaction> findById(String transactionId) throws IOException {
        try (Connection connection = HBaseConnection.getConnection();
             Table table = connection.getTable(TABLE_NAME)) {

            Get get = new Get(Bytes.toBytes(transactionId));
            Result result = table.get(get);

            if (result.isEmpty()) {
                return Optional.empty();
            }

            return Optional.of(mapResultToTxn(result));
        }
    }

    public List<LibraryTransaction> findAll() throws IOException {
        List<LibraryTransaction> txns = new ArrayList<>();
        try (Connection connection = HBaseConnection.getConnection();
             Table table = connection.getTable(TABLE_NAME)) {

            Scan scan = new Scan();
            scan.addFamily(CF_INFO);

            try (ResultScanner scanner = table.getScanner(scan)) {
                for (Result result : scanner) {
                    txns.add(mapResultToTxn(result));
                }
            }
        }
        return txns;
    }

    private LibraryTransaction mapResultToTxn(Result result) {
        String txnId = Bytes.toString(result.getRow());
        String userId = Bytes.toString(result.getValue(CF_INFO, Bytes.toBytes("userId")));
        String bookId = Bytes.toString(result.getValue(CF_INFO, Bytes.toBytes("bookId")));
        String borrowDate = Bytes.toString(result.getValue(CF_INFO, Bytes.toBytes("borrowDate")));
        String returnDate = Bytes.toString(result.getValue(CF_INFO, Bytes.toBytes("returnDate")));
        String status = Bytes.toString(result.getValue(CF_INFO, Bytes.toBytes("status")));

        return new LibraryTransaction(txnId, userId, bookId, borrowDate, returnDate, status);
    }
}
