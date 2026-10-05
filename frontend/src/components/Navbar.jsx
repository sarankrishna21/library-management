import { useLocation } from 'react-router-dom';

export default function Navbar() {
  const location = useLocation();
  
  const getPageTitle = () => {
    switch (location.pathname) {
      case '/': return 'Dashboard';
      case '/books': return 'Manage Books';
      case '/users': return 'Manage Users';
      case '/library': return 'Borrow & Return';
      case '/transactions': return 'Transactions History';
      default: return 'Library Management System';
    }
  };

  return (
    <header className="navbar">
      <div className="navbar-title">{getPageTitle()}</div>
      <div className="user-profile">
        {/* Simple placeholder for aesthetics */}
        <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
          <div style={{ textAlign: 'right' }}>
            <div style={{ fontWeight: 600, fontSize: '0.875rem' }}>Admin User</div>
            <div style={{ color: 'var(--text-muted)', fontSize: '0.75rem' }}>Administrator</div>
          </div>
          <div style={{ width: '40px', height: '40px', borderRadius: '50%', backgroundColor: 'var(--primary-color)', color: 'white', display: 'flex', alignItems: 'center', justifyContent: 'center', fontWeight: 'bold' }}>
            A
          </div>
        </div>
      </div>
    </header>
  );
}
