import { useState, useEffect } from 'react';
import { libraryApi } from '../services/api';
import { Search } from 'lucide-react';

export default function Transactions() {
  const [transactions, setTransactions] = useState([]);
  const [filteredTxns, setFilteredTxns] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  
  const [searchQuery, setSearchQuery] = useState("");
  const [statusFilter, setStatusFilter] = useState("ALL");

  useEffect(() => {
    loadTransactions();
  }, []);

  const loadTransactions = async () => {
    try {
      setLoading(true);
      const data = await libraryApi.getTransactions();
      // Most recent first
      const sorted = data.reverse();
      setTransactions(sorted);
      setFilteredTxns(sorted);
      setError(null);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    let result = transactions;
    
    if (statusFilter !== 'ALL') {
      result = result.filter(t => t.status === statusFilter);
    }
    
    if (searchQuery.trim()) {
      const q = searchQuery.toLowerCase();
      result = result.filter(t => 
        t.transactionId.toLowerCase().includes(q) ||
        t.userId.toLowerCase().includes(q) ||
        t.bookId.toLowerCase().includes(q)
      );
    }
    
    setFilteredTxns(result);
  }, [searchQuery, statusFilter, transactions]);

  return (
    <div>
      <div className="page-header">
        <h1 className="page-title">Transaction History</h1>
      </div>

      <div className="card" style={{ marginBottom: '1.5rem' }}>
        <div className="flex gap-4 items-center">
          <div style={{ flex: 1, position: 'relative' }}>
            <Search size={18} style={{ position: 'absolute', left: '12px', top: '12px', color: 'var(--text-muted)' }} />
            <input 
              type="text" 
              className="form-input" 
              style={{ paddingLeft: '2.5rem' }}
              placeholder="Search by Transaction ID, User ID, or Book ID..."
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
            />
          </div>
          <select 
            className="form-input" 
            style={{ width: '150px' }}
            value={statusFilter}
            onChange={(e) => setStatusFilter(e.target.value)}
          >
            <option value="ALL">All Status</option>
            <option value="BORROWED">Borrowed</option>
            <option value="RETURNED">Returned</option>
          </select>
        </div>
      </div>

      {error && <div className="alert alert-error">{error}</div>}

      <div className="card table-container">
        {loading ? (
          <div className="empty-state"><div className="loading-spinner"></div></div>
        ) : (
          <table>
            <thead>
              <tr>
                <th>Transaction ID</th>
                <th>User ID</th>
                <th>Book ID</th>
                <th>Borrow Date</th>
                <th>Return Date</th>
                <th>Status</th>
              </tr>
            </thead>
            <tbody>
              {filteredTxns.length === 0 ? (
                <tr><td colSpan="6" className="text-center text-muted py-8">No transactions found.</td></tr>
              ) : (
                filteredTxns.map(txn => (
                  <tr key={txn.transactionId}>
                    <td style={{ fontWeight: 600 }}>{txn.transactionId}</td>
                    <td>{txn.userId}</td>
                    <td>{txn.bookId}</td>
                    <td>{txn.borrowDate || '-'}</td>
                    <td>{txn.returnDate || '-'}</td>
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
        )}
      </div>
    </div>
  );
}
