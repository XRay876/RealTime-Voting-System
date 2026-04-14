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
    <div className="auth-container">
      <form className="card auth-form" onSubmit={handleSubmit}>
        <h2>Log in to the system</h2>
        {errors.server && <div className="alert error">{errors.server}</div>}
        
        <div className="form-group">
          <label>Email</label>
          <input 
            type="email" 
            className={errors.email ? 'input-error' : ''}
            value={formData.email} 
            onChange={(e) => setFormData({...formData, email: e.target.value})} 
            placeholder="example@mail.com"
          />
          {errors.email && <span className="error-text">{errors.email}</span>}
        </div>
        
        <div className="form-group">
          <label>Password</label>
          <input 
            type="password" 
            className={errors.password ? 'input-error' : ''}
            value={formData.password} 
            onChange={(e) => setFormData({...formData, password: e.target.value})} 
            placeholder="Enter your password"
          />
          {errors.password && <span className="error-text">{errors.password}</span>}
        </div>
        
        <button type="submit" className="btn-primary full-width">Log in</button>
        <p className="auth-link">No account? <Link to="/register">Register</Link></p>
      </form>
    </div>
  );
};

export default Login;