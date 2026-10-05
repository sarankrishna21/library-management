import { useState, useEffect } from 'react';
import { userApi } from '../services/api';
import { Plus, Edit2, Trash2, X } from 'lucide-react';

export default function Users() {
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  // Modal
  const [showModal, setShowModal] = useState(false);
  const [isEditing, setIsEditing] = useState(false);
  const [formData, setFormData] = useState({ userId: '', name: '', email: '', phone: '', role: 'Student' });

  const loadUsers = async () => {
    try {
      setLoading(true);
      const data = await userApi.getAll();
      setUsers(data);
      setError(null);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { loadUsers(); }, []);

  const openModal = (user = null) => {
    if (user) {
      setFormData({ ...user });
      setIsEditing(true);
    } else {
      setFormData({ userId: '', name: '', email: '', phone: '', role: 'Student' });
      setIsEditing(false);
    }
    setShowModal(true);
  };

  const handleSave = async (e) => {
    e.preventDefault();
    try {
      if (isEditing) {
        await userApi.update(formData.userId, formData);
      } else {
        await userApi.create(formData);
      }
      setShowModal(false);
      loadUsers();
    } catch (err) {
      alert(`Error saving user: ${err.message}`);
    }
  };

  const handleDelete = async (id) => {
    if (window.confirm('Are you sure you want to delete this user?')) {
      try {
        await userApi.delete(id);
        loadUsers();
      } catch (err) {
        alert(`Error deleting user: ${err.message}`);
      }
    }
  };

  return (
    <div>
      <div className="page-header">
        <h1 className="page-title">Manage Users</h1>
        <button className="btn btn-primary" onClick={() => openModal()}>
          <Plus size={18} /> Add New User
        </button>
      </div>

      {error && <div className="alert alert-error">{error}</div>}

      <div className="card table-container">
        {loading ? (
          <div className="empty-state"><div className="loading-spinner"></div></div>
        ) : (
          <table>
            <thead>
              <tr>
                <th>User ID</th>
                <th>Name / Email</th>
                <th>Phone</th>
                <th>Role</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {users.length === 0 ? (
                <tr><td colSpan="5" className="text-center text-muted py-8">No users found.</td></tr>
              ) : (
                users.map(user => (
                  <tr key={user.userId}>
                    <td style={{ fontWeight: 600 }}>{user.userId}</td>
                    <td>
                      <div style={{ fontWeight: 500 }}>{user.name}</div>
                      <div style={{ fontSize: '0.875rem', color: 'var(--text-muted)' }}>{user.email || '-'}</div>
                    </td>
                    <td>{user.phone || '-'}</td>
                    <td>
                      <span className="badge badge-info">{user.role || 'Student'}</span>
                    </td>
                    <td>
                      <div className="flex gap-2">
                        <button className="btn btn-outline" onClick={() => openModal(user)} style={{ padding: '0.4rem' }} title="Edit">
                          <Edit2 size={16} />
                        </button>
                        <button className="btn btn-outline" onClick={() => handleDelete(user.userId)} style={{ padding: '0.4rem', color: 'var(--error)' }} title="Delete">
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
              <h2 style={{ fontSize: '1.25rem' }}>{isEditing ? 'Edit User' : 'Add New User'}</h2>
              <button onClick={() => setShowModal(false)} style={{ background: 'none', border: 'none', cursor: 'pointer' }}><X size={20} /></button>
            </div>
            <form onSubmit={handleSave}>
              <div className="modal-body">
                <div className="form-group">
                  <label className="form-label">User ID (Required)</label>
                  <input type="text" className="form-input" required disabled={isEditing} value={formData.userId} onChange={e => setFormData({...formData, userId: e.target.value})} />
                </div>
                <div className="form-group">
                  <label className="form-label">Name (Required)</label>
                  <input type="text" className="form-input" required value={formData.name} onChange={e => setFormData({...formData, name: e.target.value})} />
                </div>
                <div className="form-group">
                  <label className="form-label">Email</label>
                  <input type="email" className="form-input" value={formData.email} onChange={e => setFormData({...formData, email: e.target.value})} />
                </div>
                <div className="form-group">
                  <label className="form-label">Phone</label>
                  <input type="text" className="form-input" value={formData.phone} onChange={e => setFormData({...formData, phone: e.target.value})} />
                </div>
                <div className="form-group">
                  <label className="form-label">Role</label>
                  <select className="form-input" value={formData.role} onChange={e => setFormData({...formData, role: e.target.value})}>
                    <option value="Student">Student</option>
                    <option value="Faculty">Faculty</option>
                    <option value="Staff">Staff</option>
                  </select>
                </div>
              </div>
              <div className="modal-footer">
                <button type="button" className="btn btn-outline" onClick={() => setShowModal(false)}>Cancel</button>
                <button type="submit" className="btn btn-primary">{isEditing ? 'Save Changes' : 'Add User'}</button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}
