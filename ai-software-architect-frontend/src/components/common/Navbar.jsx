import React from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../../hooks/useAuth';
import { LogOut, LayoutDashboard, Terminal } from 'lucide-react';

/**
 * Top navigation navbar. Renders the logo, dashboard links,
 * and logged-in user profile controls with a logout button.
 */
const Navbar = () => {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  return (
    <nav className="glass-panel" style={{ margin: '20px', padding: '15px 30px', display: 'flex', justifyContent: 'space-between', alignItems: 'center', borderRadius: '16px' }}>
      <Link to="/" style={{ textDecoration: 'none', color: 'inherit', display: 'flex', alignItems: 'center', gap: '10px' }}>
        <Terminal size={22} color="#3b82f6" />
        <span style={{ fontSize: '1.25rem', fontWeight: '800' }} className="gradient-text">
          AI Software Architect
        </span>
      </Link>
      {user && (
        <div style={{ display: 'flex', alignItems: 'center', gap: '20px' }}>
          <Link to="/" style={{ color: 'var(--text-secondary)', display: 'flex', alignItems: 'center', gap: '6px', textDecoration: 'none', fontWeight: '500' }}>
            <LayoutDashboard size={18} />
            <span>Dashboard</span>
          </Link>
          <span style={{ color: 'var(--border-color)' }}>|</span>
          <span style={{ color: 'var(--text-secondary)', fontSize: '0.9rem' }}>
            User: <strong>{user.username}</strong>
          </span>
          <button 
            onClick={handleLogout} 
            className="btn-primary" 
            style={{ 
              padding: '8px 16px', 
              background: 'transparent', 
              border: '1px solid var(--border-color)', 
              boxShadow: 'none',
              borderRadius: '8px'
            }}
          >
            <LogOut size={16} />
            <span>Logout</span>
          </button>
        </div>
      )}
    </nav>
  );
};

export default Navbar;
// 
