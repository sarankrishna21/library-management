import { useState, useEffect } from 'react';
import { bookApi, userApi, libraryApi } from '../services/api';
import { BookOpen, Users, ArrowLeftRight, Activity } from 'lucide-react';

export default function Dashboard() {
  const [stats, setStats] = useState({
    totalBooks: 0,
    availableBooks: 0,
    totalUsers: 0,
    totalTransactions: 0
  });
  const [recentBooks, setRecentBooks] = useState([]);
  const [recentTransactions, setRecentTransactions] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    async function fetchDashboardData() {
      try {
        const [books, users, transactions] = await Promise.all([
          bookApi.getAll(),
          userApi.getAll(),
          libraryApi.getTransactions()
        ]);

        const available = books.filter(b => b.available).length;

        setStats({
          totalBooks: books.length,
          availableBooks: available,
          totalUsers: users.length,
          totalTransactions: transactions.length
        });

        // Use the last 5 added for 'recent'
        setRecentBooks(books.slice(-5).reverse());
        setRecentTransactions(transactions.slice(-5).reverse());
        setError(null);
      } catch (err) {
        setError(err.message);
      } finally {
        setLoading(false);
      }
    }
    
    fetchDashboardData();
  }, []);

  if (loading) return <div className="empty-state"><div className="loading-spinner"></div><p className="mt-4">Loading dashboard...</p></div>;
  if (error) return <div className="alert alert-error"><strong>Error:</strong> {error}</div>;

  return (
    <div>
      <div className="page-header">
        <h1 className="page-title">Dashboard Overview</h1>
      </div>

      <div className="stats-grid">
        <div className="stat-card">
          <div className="stat-icon" style={{ background: 'var(--info-bg)', color: 'var(--info)' }}>
            <BookOpen size={24} />
          </div>
          <div className="stat-info">
            <h3>Total Books</h3>
            <p>{stats.totalBooks}</p>
          </div>
        </div>
        <div className="stat-card">
          <div className="stat-icon" style={{ background: 'var(--success-bg)', color: 'var(--success)' }}>
            <BookOpen size={24} />
          </div>
          <div className="stat-info">
            <h3>Available Books</h3>
            <p>{stats.availableBooks}</p>
          </div>
        </div>
        <div className="stat-card">
          <div className="stat-icon" style={{ background: 'var(--warning-bg)', color: 'var(--warning)' }}>
            <Users size={24} />
          </div>
          <div className="stat-info">
            <h3>Registered Users</h3>
            <p>{stats.totalUsers}</p>
          </div>
        </div>
        <div className="stat-card">
          <div className="stat-icon" style={{ background: 'var(--primary-color)', color: 'white' }}>
            <Activity size={24} />
          </div>
          <div className="stat-info">
            <h3>Total Transactions</h3>
            <p>{stats.totalTransactions}</p>
          </div>
        </div>
      </div>

      <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '2rem' }}>
        <div className="card">
          <h2 style={{ marginBottom: '1.5rem', fontSize: '1.25rem' }}>Recently Added Books</h2>
          <div className="table-container">
            <table>
              <thead>
                <tr>
                  <th>Book</th>
                  <th>Category</th>
                  <th>Status</th>
                </tr>
              </thead>
              <tbody>
                {recentBooks.length === 0 ? (
                  <tr><td colSpan="3" className="text-center text-muted">No books found.</td></tr>
                ) : (
                  recentBooks.map(book => (
                    <tr key={book.bookId}>
                      <td>
                        <div style={{ fontWeight: 500 }}>{book.title}</div>
                        <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>{book.author}</div>
                      </td>
                      <td>{book.category}</td>
                      <td>
                        <span className={`badge ${book.available ? 'badge-success' : 'badge-warning'}`}>
                          {book.available ? 'AVAILABLE' : 'BORROWED'}
                        </span>
                      </td>
                    </tr>
                  ))
                )}
              </tbody>
            </table>
          </div>
        </div>

        <div className="card">
          <h2 style={{ marginBottom: '1.5rem', fontSize: '1.25rem' }}>Recent Transactions</h2>
          <div className="table-container">
            <table>
              <thead>
                <tr>
                  <th>Transaction ID</th>
                  <th>User / Book</th>
                  <th>Status</th>
                </tr>
              </thead>
              <tbody>
                {recentTransactions.length === 0 ? (
                  <tr><td colSpan="3" className="text-center text-muted">No transactions found.</td></tr>
                ) : (
                  recentTransactions.map(txn => (
                    <tr key={txn.transactionId}>
                      <td style={{ fontWeight: 500 }}>{txn.transactionId}</td>
                      <td>
                        <div style={{ fontSize: '0.875rem' }}>User: {txn.userId}</div>
                        <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>Book: {txn.bookId}</div>
                      </td>
                      <td>
                        <span className={`badge ${txn.status === 'BORROWED' ? 'badge-warning' : 'badge-success'}`}>
                          {txn.status}
                        </span>
                      </td>
                    </tr>
                  ))
                )}
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>
  );
}
