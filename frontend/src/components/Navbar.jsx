import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { UserService } from '../api/services';

const Navbar = () => {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  const handlePromote = async () => {
    try {
      await UserService.promoteToAdmin();
      alert('Successful! Please relogin.');
      handleLogout();
    } catch (e) {
      alert('Error promoting user');
    }
  };

  return (
    <nav className="main-navbar">
      <div className="nav-container">
        <div className="nav-left">
          <Link to="/" className="brand-logo">
            <span className="logo-text">PollMaster</span>
          </Link>
          {user && (
            <div className="main-links">
              {user.role === 'ROLE_ADMIN' && (
                <Link to="/admin" className="nav-link admin-link">Management</Link>
              )}
            </div>
          )}
        </div>

        <div className="nav-right">
          {user ? (
            <div className="user-control">
              <div className="user-info">
                <span className="user-name">{user.username}</span>
                <span className={`role-badge ${user.role.toLowerCase()}`}>
                  {user.role.replace('ROLE_', '')}
                </span>
              </div>
              
              <div className="action-group">
                <Link to="/profile" className="icon-link" title="Profile">Profile</Link>
                {user.role !== 'ROLE_ADMIN' && (
                  <button className="btn-promote" onClick={handlePromote}>Go Pro</button>
                )}
                <button className="btn-nav-logout" onClick={handleLogout}>Logout</button>
              </div>
            </div>
          ) : (
            <div className="auth-btns">
              <Link to="/login" className="btn-login">Log in</Link>
              <Link to="/register" className="btn-signup-nav">Get Started</Link>
            </div>
          )}
        </div>
      </div>
    </nav>
  );
};

export default Navbar;