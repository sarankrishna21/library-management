package com.library.repository;

import com.library.config.HBaseConnection;
import com.library.model.Book;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.filter.CompareFilter;
import org.apache.hadoop.hbase.filter.Filter;
import org.apache.hadoop.hbase.filter.SingleColumnValueFilter;
import org.apache.hadoop.hbase.filter.SubstringComparator;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Repository for library_books table in HBase.
 */
public class BookRepository {

    private static final TableName TABLE_NAME = TableName.valueOf("library_books");
    private static final byte[] CF_INFO = Bytes.toBytes("info");

    public boolean exists(String bookId) throws IOException {
        try (Connection connection = HBaseConnection.getConnection();
             Table table = connection.getTable(TABLE_NAME)) {
            return table.exists(new Get(Bytes.toBytes(bookId)));
        }
    }

    public void save(Book book) throws IOException {
        try (Connection connection = HBaseConnection.getConnection();
             Table table = connection.getTable(TABLE_NAME)) {

            byte[] rowKey = Bytes.toBytes(book.getBookId());
            Put put = new Put(rowKey);
            put.addColumn(CF_INFO, Bytes.toBytes("title"), Bytes.toBytes(book.getTitle()));
            put.addColumn(CF_INFO, Bytes.toBytes("author"), Bytes.toBytes(book.getAuthor()));
            put.addColumn(CF_INFO, Bytes.toBytes("category"), Bytes.toBytes(book.getCategory()));
            put.addColumn(CF_INFO, Bytes.toBytes("price"), Bytes.toBytes(String.valueOf(book.getPrice())));
            put.addColumn(CF_INFO, Bytes.toBytes("available"), Bytes.toBytes(String.valueOf(book.isAvailable())));

            table.put(put);
        }
    }

    public Optional<Book> findById(String bookId) throws IOException {
        try (Connection connection = HBaseConnection.getConnection();
             Table table = connection.getTable(TABLE_NAME)) {

            Get get = new Get(Bytes.toBytes(bookId));
            Result result = table.get(get);

            if (result.isEmpty()) {
                return Optional.empty();
            }

            return Optional.of(mapResultToBook(result));
        }
    }

    public boolean deleteById(String bookId) throws IOException {
        try (Connection connection = HBaseConnection.getConnection();
             Table table = connection.getTable(TABLE_NAME)) {

            byte[] rowKey = Bytes.toBytes(bookId);
            if (!table.exists(new Get(rowKey))) {
                return false;
            }

            table.delete(new Delete(rowKey));
            return true;
        }
    }

    public List<Book> findAll() throws IOException {
        List<Book> books = new ArrayList<>();
        try (Connection connection = HBaseConnection.getConnection();
             Table table = connection.getTable(TABLE_NAME)) {

            Scan scan = new Scan();
            scan.addFamily(CF_INFO);

            try (ResultScanner scanner = table.getScanner(scan)) {
                for (Result result : scanner) {
                    books.add(mapResultToBook(result));
                }
            }
        }
        return books;
    }

    public List<Book> search(String fieldName, String keyword) throws IOException {
        List<Book> books = new ArrayList<>();
        try (Connection connection = HBaseConnection.getConnection();
             Table table = connection.getTable(TABLE_NAME)) {

            Scan scan = new Scan();
            scan.addFamily(CF_INFO);

            Filter filter = new SingleColumnValueFilter(
                    CF_INFO,
                    Bytes.toBytes(fieldName.toLowerCase().trim()),
                    CompareFilter.CompareOp.EQUAL,
                    new SubstringComparator(keyword.trim())
            );
            ((SingleColumnValueFilter) filter).setFilterIfMissing(true);
            scan.setFilter(filter);

            try (ResultScanner scanner = table.getScanner(scan)) {
                for (Result result : scanner) {
                    books.add(mapResultToBook(result));
                }
            }
        }
        return books;
    }

    private Book mapResultToBook(Result result) {
        String bookId = Bytes.toString(result.getRow());
        String title = Bytes.toString(result.getValue(CF_INFO, Bytes.toBytes("title")));
        String author = Bytes.toString(result.getValue(CF_INFO, Bytes.toBytes("author")));
        String category = Bytes.toString(result.getValue(CF_INFO, Bytes.toBytes("category")));
        String priceStr = Bytes.toString(result.getValue(CF_INFO, Bytes.toBytes("price")));
        String availStr = Bytes.toString(result.getValue(CF_INFO, Bytes.toBytes("available")));

        double price = priceStr != null ? Double.parseDouble(priceStr) : 0.0;
        boolean available = "true".equalsIgnoreCase(availStr);

        return new Book(bookId, title, author, category, price, available);
    }
}
