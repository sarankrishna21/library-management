package com.library.repository;

import com.library.config.HBaseConnection;
import com.library.model.User;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.Delete;
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
 * Data Access Repository for users table in HBase.
 */
public class UserRepository {

    private static final TableName TABLE_NAME = TableName.valueOf("users");
    private static final byte[] CF_INFO = Bytes.toBytes("info");

    public boolean exists(String userId) throws IOException {
        try (Connection connection = HBaseConnection.getConnection();
             Table table = connection.getTable(TABLE_NAME)) {
            return table.exists(new Get(Bytes.toBytes(userId)));
        }
    }

    public void save(User user) throws IOException {
        try (Connection connection = HBaseConnection.getConnection();
             Table table = connection.getTable(TABLE_NAME)) {

            byte[] rowKey = Bytes.toBytes(user.getUserId());
            Put put = new Put(rowKey);
            put.addColumn(CF_INFO, Bytes.toBytes("name"), Bytes.toBytes(user.getName()));
            put.addColumn(CF_INFO, Bytes.toBytes("email"), Bytes.toBytes(user.getEmail()));
            put.addColumn(CF_INFO, Bytes.toBytes("phone"), Bytes.toBytes(user.getPhone()));
            put.addColumn(CF_INFO, Bytes.toBytes("role"), Bytes.toBytes(user.getRole()));

            table.put(put);
        }
    }

    public Optional<User> findById(String userId) throws IOException {
        try (Connection connection = HBaseConnection.getConnection();
             Table table = connection.getTable(TABLE_NAME)) {

            Get get = new Get(Bytes.toBytes(userId));
            Result result = table.get(get);

            if (result.isEmpty()) {
                return Optional.empty();
            }

            return Optional.of(mapResultToUser(result));
        }
    }

    public boolean deleteById(String userId) throws IOException {
        try (Connection connection = HBaseConnection.getConnection();
             Table table = connection.getTable(TABLE_NAME)) {

            byte[] rowKey = Bytes.toBytes(userId);
            if (!table.exists(new Get(rowKey))) {
                return false;
            }

            table.delete(new Delete(rowKey));
            return true;
        }
    }

    public List<User> findAll() throws IOException {
        List<User> users = new ArrayList<>();
        try (Connection connection = HBaseConnection.getConnection();
             Table table = connection.getTable(TABLE_NAME)) {

            Scan scan = new Scan();
            scan.addFamily(CF_INFO);

            try (ResultScanner scanner = table.getScanner(scan)) {
                for (Result result : scanner) {
                    users.add(mapResultToUser(result));
                }
            }
        }
        return users;
    }

    private User mapResultToUser(Result result) {
        String userId = Bytes.toString(result.getRow());
        String name = Bytes.toString(result.getValue(CF_INFO, Bytes.toBytes("name")));
        String email = Bytes.toString(result.getValue(CF_INFO, Bytes.toBytes("email")));
        String phone = Bytes.toString(result.getValue(CF_INFO, Bytes.toBytes("phone")));
        String role = Bytes.toString(result.getValue(CF_INFO, Bytes.toBytes("role")));

        return new User(userId, name, email, phone, role);
    }
}
