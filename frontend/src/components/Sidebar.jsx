import { NavLink } from 'react-router-dom';
import { LayoutDashboard, Book, Users, Repeat, FileText, Library } from 'lucide-react';

export default function Sidebar() {
  const links = [
    { to: '/', icon: <LayoutDashboard size={20} />, label: 'Dashboard' },
    { to: '/books', icon: <Book size={20} />, label: 'Books' },
    { to: '/users', icon: <Users size={20} />, label: 'Users' },
    { to: '/library', icon: <Repeat size={20} />, label: 'Borrow / Return' },
    { to: '/transactions', icon: <FileText size={20} />, label: 'Transactions' },
  ];

  return (
    <aside className="sidebar">
      <div className="sidebar-header">
        <Library size={28} color="#818cf8" />
        LMS Admin
      </div>
      <nav className="nav-links">
        {links.map((link) => (
          <NavLink
            key={link.to}
            to={link.to}
            className={({ isActive }) => (isActive ? 'nav-link active' : 'nav-link')}
          >
            {link.icon}
            {link.label}
          </NavLink>
        ))}
      </nav>
    </aside>
  );
}
