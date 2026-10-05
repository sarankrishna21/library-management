import { useState } from 'react';
import { libraryApi } from '../services/api';
import { Repeat, ArrowDownToLine, ArrowUpFromLine } from 'lucide-react';

export default function Library() {
  const [borrowForm, setBorrowForm] = useState({ transactionId: '', userId: '', bookId: '' });
  const [returnForm, setReturnForm] = useState({ transactionId: '' });
  
  const [borrowStatus, setBorrowStatus] = useState({ type: '', message: '' });
  const [returnStatus, setReturnStatus] = useState({ type: '', message: '' });

  const handleBorrow = async (e) => {
    e.preventDefault();
    setBorrowStatus({ type: 'loading', message: 'Processing borrow...' });
    try {
      await libraryApi.borrowBook(borrowForm.transactionId, borrowForm.userId, borrowForm.bookId);
      setBorrowStatus({ type: 'success', message: 'Book borrowed successfully!' });
      setBorrowForm({ transactionId: '', userId: '', bookId: '' });
    } catch (err) {
      setBorrowStatus({ type: 'error', message: err.message });
    }
  };

  const handleReturn = async (e) => {
    e.preventDefault();
    setReturnStatus({ type: 'loading', message: 'Processing return...' });
    try {
      await libraryApi.returnBook(returnForm.transactionId);
      setReturnStatus({ type: 'success', message: 'Book returned successfully!' });
      setReturnForm({ transactionId: '' });
    } catch (err) {
      setReturnStatus({ type: 'error', message: err.message });
    }
  };

  return (
    <div>
      <div className="page-header">
        <h1 className="page-title">Borrow & Return</h1>
      </div>

      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(400px, 1fr))', gap: '2rem' }}>
        
        {/* Borrow Section */}
        <div className="card">
          <div className="flex items-center gap-2 mb-6 text-primary">
            <ArrowUpFromLine size={24} style={{ color: 'var(--warning)' }} />
            <h2 style={{ fontSize: '1.25rem' }}>Borrow a Book</h2>
          </div>

          {borrowStatus.message && (
            <div className={`alert ${borrowStatus.type === 'error' ? 'alert-error' : borrowStatus.type === 'success' ? 'alert-success' : ''}`}>
              {borrowStatus.type === 'loading' && <div className="loading-spinner" style={{ width: 16, height: 16, borderWidth: 2 }}></div>}
              {borrowStatus.message}
            </div>
          )}

          <form onSubmit={handleBorrow}>
            <div className="form-group">
              <label className="form-label">Transaction ID (Required)</label>
              <input 
                type="text" 
                className="form-input" 
                required 
                placeholder="e.g., TXN002"
                value={borrowForm.transactionId} 
                onChange={e => setBorrowForm({...borrowForm, transactionId: e.target.value})} 
              />
            </div>
            <div className="form-group">
              <label className="form-label">User ID (Required)</label>
              <input 
                type="text" 
                className="form-input" 
                required 
                placeholder="e.g., USR001"
                value={borrowForm.userId} 
                onChange={e => setBorrowForm({...borrowForm, userId: e.target.value})} 
              />
            </div>
            <div className="form-group">
              <label className="form-label">Book ID (Required)</label>
              <input 
                type="text" 
                className="form-input" 
                required 
                placeholder="e.g., BOOK001"
                value={borrowForm.bookId} 
                onChange={e => setBorrowForm({...borrowForm, bookId: e.target.value})} 
              />
            </div>
            <button type="submit" className="btn btn-primary" style={{ width: '100%', marginTop: '1rem', padding: '0.75rem' }} disabled={borrowStatus.type === 'loading'}>
              Issue Book
            </button>
          </form>
        </div>

        {/* Return Section */}
        <div className="card">
          <div className="flex items-center gap-2 mb-6">
            <ArrowDownToLine size={24} style={{ color: 'var(--success)' }} />
            <h2 style={{ fontSize: '1.25rem' }}>Return a Book</h2>
          </div>

          {returnStatus.message && (
            <div className={`alert ${returnStatus.type === 'error' ? 'alert-error' : returnStatus.type === 'success' ? 'alert-success' : ''}`}>
              {returnStatus.type === 'loading' && <div className="loading-spinner" style={{ width: 16, height: 16, borderWidth: 2 }}></div>}
              {returnStatus.message}
            </div>
          )}

          <form onSubmit={handleReturn}>
            <div className="form-group">
              <label className="form-label">Transaction ID (Required)</label>
              <input 
                type="text" 
                className="form-input" 
                required 
                placeholder="Enter original transaction ID (e.g., TXN002)"
                value={returnForm.transactionId} 
                onChange={e => setReturnForm({...returnForm, transactionId: e.target.value})} 
              />
            </div>
            <p className="text-muted" style={{ fontSize: '0.875rem', marginBottom: '1.5rem' }}>
              Returning a book will automatically update its availability in the catalog.
            </p>
            <button type="submit" className="btn btn-outline" style={{ width: '100%', padding: '0.75rem', borderColor: 'var(--success)', color: 'var(--success)' }} disabled={returnStatus.type === 'loading'}>
              Return Book
            </button>
          </form>
        </div>

      </div>
    </div>
  );
}
