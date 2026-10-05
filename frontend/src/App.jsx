import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import Sidebar from './components/Sidebar';
import Navbar from './components/Navbar';
import Dashboard from './pages/Dashboard';
import Books from './pages/Books';
import Users from './pages/Users';
import Library from './pages/Library';
import Transactions from './pages/Transactions';

function App() {
  return (
    <Router>
      <div className="app-container">
        <Sidebar />
        <main className="main-content">
          <Navbar />
          <div className="content-area">
            <Routes>
              <Route path="/" element={<Dashboard />} />
              <Route path="/dashboard" element={<Dashboard />} />
              <Route path="/books" element={<Books />} />
              <Route path="/users" element={<Users />} />
              <Route path="/library" element={<Library />} />
              <Route path="/transactions" element={<Transactions />} />
            </Routes>
          </div>
        </main>
      </div>
    </Router>
  );
}

export default App;
