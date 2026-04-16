import { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

const Login = () => {
  const [formData, setFormData] = useState({ email: '', password: '' });
  const [errors, setErrors] = useState({});
  const { login } = useAuth();
  const navigate = useNavigate();

  const validate = () => {
    const newErrors = {};

    if (!formData.email.trim()) {
      newErrors.email = 'Email is required';
    } else if (!/\S+@\S+\.\S+/.test(formData.email)) {
      newErrors.email = 'Invalid email format';
    }

    if (!formData.password) {
      newErrors.password = 'Password is required';
    }

    return newErrors;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setErrors({}); 

    const validationErrors = validate();
    if (Object.keys(validationErrors).length > 0) {
      setErrors(validationErrors);
      return;
    }

    try {
      await login({
        email: formData.email.trim(),
        password: formData.password
      });
      navigate('/');
    } catch (err) {
      setErrors({ server: err.response?.data?.message || 'Invalid email or password' });
    }
  };

  return (
    <div className="auth-page">
      <div className="auth-card smaller">
        <header className="auth-header">
          <h1>Welcome Back</h1>
          <p>Please enter your details to sign in.</p>
        </header>

        {errors.server && <div className="error-alert">{errors.server}</div>}
        
        <form onSubmit={handleSubmit} className="auth-form">
          <div className="form-group">
            <label>Email Address</label>
            <input 
              type="email" 
              className={`modern-input ${errors.email ? 'has-error' : ''}`}
              value={formData.email} 
              onChange={(e) => setFormData({...formData, email: e.target.value})} 
              placeholder="name@company.com"
            />
            {errors.email && <span className="error-hint">{errors.email}</span>}
          </div>
          
          <div className="form-group">
            <label>Password</label>
            <input 
              type="password" 
              className={`modern-input ${errors.password ? 'has-error' : ''}`}
              value={formData.password} 
              onChange={(e) => setFormData({...formData, password: e.target.value})} 
              placeholder="••••••••"
            />
            {errors.password && <span className="error-hint">{errors.password}</span>}
          </div>
          
          <button type="submit" className="btn-auth-primary">Sign In</button>
        </form>

        <footer className="auth-footer">
          <p>Don't have an account? <Link to="/register" className="accent-link">Register</Link></p>
        </footer>
      </div>
    </div>
  );
};

export default Login;