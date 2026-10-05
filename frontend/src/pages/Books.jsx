import { useState, useEffect } from 'react';
import { bookApi } from '../services/api';
import { Plus, Search, Edit2, Trash2, X } from 'lucide-react';

export default function Books() {
  const [books, setBooks] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  
  // Search
  const [searchQuery, setSearchQuery] = useState("");
  const [searchType, setSearchType] = useState("title");

  // Modal
  const [showModal, setShowModal] = useState(false);
  const [isEditing, setIsEditing] = useState(false);
  const [formData, setFormData] = useState({ bookId: '', title: '', author: '', category: '', price: 0 });

  const loadBooks = async () => {
    try {
      setLoading(true);
      const data = await bookApi.getAll();
      setBooks(data);
      setError(null);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { loadBooks(); }, []);

  const handleSearch = async (e) => {
    e.preventDefault();
    if (!searchQuery.trim()) {
      return loadBooks();
    }
    try {
      setLoading(true);
      let data = [];
      if (searchType === 'title') data = await bookApi.searchTitle(searchQuery);
      else if (searchType === 'author') data = await bookApi.searchAuthor(searchQuery);
      else if (searchType === 'category') data = await bookApi.searchCategory(searchQuery);
      setBooks(data);
      setError(null);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  const openModal = (book = null) => {
    if (book) {
      setFormData({ ...book });
      setIsEditing(true);
    } else {
      setFormData({ bookId: '', title: '', author: '', category: '', price: 0 });
      setIsEditing(false);
    }
    setShowModal(true);
  };

  const handleSave = async (e) => {
    e.preventDefault();
    try {
      if (isEditing) {
        await bookApi.update(formData.bookId, formData);
      } else {
        await bookApi.create(formData);
      }
      setShowModal(false);
      loadBooks();
    } catch (err) {
      alert(`Error saving book: ${err.message}`);
    }
  };

  const handleDelete = async (id) => {
    if (window.confirm('Are you sure you want to delete this book?')) {
      try {
        await bookApi.delete(id);
        loadBooks();
      } catch (err) {
        alert(`Error deleting book: ${err.message}`);
      }
    }
  };

  return (
    <div>
      <div className="page-header">
        <h1 className="page-title">Manage Books</h1>
        <button className="btn btn-primary" onClick={() => openModal()}>
          <Plus size={18} /> Add New Book
        </button>
      </div>

      <div className="card" style={{ marginBottom: '1.5rem' }}>
        <form onSubmit={handleSearch} className="flex gap-4 items-center">
          <div style={{ flex: 1, position: 'relative' }}>
            <Search size={18} style={{ position: 'absolute', left: '12px', top: '12px', color: 'var(--text-muted)' }} />
            <input 
              type="text" 
              className="form-input" 
              style={{ paddingLeft: '2.5rem' }}
              placeholder="Search books..."
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
            />
          </div>
          <select 
            className="form-input" 
            style={{ width: '150px' }}
            value={searchType}
            onChange={(e) => setSearchType(e.target.value)}
          >
            <option value="title">By Title</option>
            <option value="author">By Author</option>
            <option value="category">By Category</option>
          </select>
          <button type="submit" className="btn btn-primary">Search</button>
          <button type="button" className="btn btn-outline" onClick={() => {setSearchQuery(''); loadBooks();}}>Clear</button>
        </form>
      </div>

      {error && <div className="alert alert-error">{error}</div>}

      <div className="card table-container">
        {loading ? (
          <div className="empty-state"><div className="loading-spinner"></div></div>
        ) : (
          <table>
            <thead>
              <tr>
                <th>Book ID</th>
                <th>Details</th>
                <th>Category</th>
                <th>Price</th>
                <th>Status</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {books.length === 0 ? (
                <tr><td colSpan="6" className="text-center text-muted py-8">No books found.</td></tr>
              ) : (
                books.map(book => (
                  <tr key={book.bookId}>
                    <td style={{ fontWeight: 600 }}>{book.bookId}</td>
                    <td>
                      <div style={{ fontWeight: 500 }}>{book.title}</div>
                      <div style={{ fontSize: '0.875rem', color: 'var(--text-muted)' }}>{book.author}</div>
                    </td>
                    <td>{book.category}</td>
                    <td>${book.price.toFixed(2)}</td>
                    <td>
                      <span className={`badge ${book.available ? 'badge-success' : 'badge-warning'}`}>
                        {book.available ? 'AVAILABLE' : 'BORROWED'}
                      </span>
                    </td>
                    <td>
                      <div className="flex gap-2">
                        <button className="btn btn-outline" onClick={() => openModal(book)} style={{ padding: '0.4rem' }} title="Edit">
                          <Edit2 size={16} />
                        </button>
                        <button className="btn btn-outline" onClick={() => handleDelete(book.bookId)} style={{ padding: '0.4rem', color: 'var(--error)' }} title="Delete">
                          <Trash2 size={16} />
                        </button>
                      </div>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        )}
      </div>

      {showModal && (
        <div className="modal-overlay">
          <div className="modal-content">
            <div className="modal-header">
              <h2 style={{ fontSize: '1.25rem' }}>{isEditing ? 'Edit Book' : 'Add New Book'}</h2>
              <button onClick={() => setShowModal(false)} style={{ background: 'none', border: 'none', cursor: 'pointer' }}><X size={20} /></button>
            </div>
            <form onSubmit={handleSave}>
              <div className="modal-body">
                <div className="form-group">
                  <label className="form-label">Book ID (Required)</label>
                  <input type="text" className="form-input" required disabled={isEditing} value={formData.bookId} onChange={e => setFormData({...formData, bookId: e.target.value})} />
                </div>
                <div className="form-group">
                  <label className="form-label">Title (Required)</label>
                  <input type="text" className="form-input" required value={formData.title} onChange={e => setFormData({...formData, title: e.target.value})} />
                </div>
                <div className="form-group">
                  <label className="form-label">Author (Required)</label>
                  <input type="text" className="form-input" required value={formData.author} onChange={e => setFormData({...formData, author: e.target.value})} />
                </div>
                <div className="form-group">
                  <label className="form-label">Category (Required)</label>
                  <input type="text" className="form-input" required value={formData.category} onChange={e => setFormData({...formData, category: e.target.value})} />
                </div>
                <div className="form-group">
                  <label className="form-label">Price</label>
                  <input type="number" step="0.01" className="form-input" value={formData.price} onChange={e => setFormData({...formData, price: parseFloat(e.target.value)})} />
                </div>
              </div>
              <div className="modal-footer">
                <button type="button" className="btn btn-outline" onClick={() => setShowModal(false)}>Cancel</button>
                <button type="submit" className="btn btn-primary">{isEditing ? 'Save Changes' : 'Add Book'}</button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}
