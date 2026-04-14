import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { UserService } from '../api/services';

const Navbar = () => {
  const { user, logout, fetchUser } = useAuth();
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
      alert('Error');
    }
  };

  return (
    <nav className="navbar">
      <div className="nav-brand">
        <Link to="/">Voting System</Link>
      </div>
      <div className="nav-links">
        {user ? (
          <>
            <span className="user-badge">{user.username} ({user.role})</span>
            {user.role !== 'ROLE_ADMIN' && (
              <button className="btn-small" onClick={handlePromote}>Become Admin</button>
            )}
            <button className="btn-logout" onClick={handleLogout}>Logout</button>

            <Link to="/profile">Profile</Link>
            {user.role === 'ROLE_ADMIN' && <Link to="/admin">Admin Panel</Link>}
          </>
        ) : (
          <>
            <Link to="/login">Log in</Link>
            <Link to="/register" className="btn-primary">Register</Link>
          </>
        )}
      </div>
    </nav>
  );
};

export default Navbar;