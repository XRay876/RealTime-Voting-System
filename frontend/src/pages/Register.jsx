import { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

const Register = () => {
  const [formData, setFormData] = useState({ firstName: '', lastName: '', email: '', password: '' });
  const [errors, setErrors] = useState({});
  const navigate = useNavigate();
  const { register } = useAuth();

  const validate = () => {
    const newErrors = {};
    
    if (!formData.firstName.trim()) {
      newErrors.firstName = 'First name is required';
    } else if (formData.firstName.length < 2 || formData.firstName.length > 50) {
      newErrors.firstName = 'First name must be between 2 and 50 characters';
    }

    if (!formData.lastName.trim()) {
      newErrors.lastName = 'Last name is required';
    } else if (formData.lastName.length < 2 || formData.lastName.length > 50) {
      newErrors.lastName = 'Last name must be between 2 and 50 characters';
    }

    if (!formData.email.trim()) {
      newErrors.email = 'Email is required';
    } else if (!/\S+@\S+\.\S+/.test(formData.email)) {
      newErrors.email = 'Invalid email format';
    }

    const passwordRegex = /^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,}$/;
    if (!formData.password) {
      newErrors.password = 'Password is required';
    } else if (!passwordRegex.test(formData.password)) {
      newErrors.password = 'Password must contain at least one digit, one uppercase, one lowercase letter and be at least 8 characters long';
    }
    return newErrors;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    const validationErrors = validate();
    if (Object.keys(validationErrors).length > 0) return setErrors(validationErrors);

    try {
      await register(formData);
      navigate('/');
    } catch (err) {
      setErrors({ server: err.response?.data?.message || 'Registration failed' });
    }
  };

  return (
    <div className="auth-page">
      <div className="auth-card">
        <header className="auth-header">
          <h1>Create Account</h1>
          <p>Join our community and start polling today.</p>
        </header>

        {errors.server && <div className="error-alert">{errors.server}</div>}

        <form onSubmit={handleSubmit} className="auth-form">
          <div className="form-grid">
            <div className="form-group">
              <label>First Name</label>
              <input 
                type="text" 
                className={`modern-input ${errors.firstName ? 'has-error' : ''}`}
                placeholder="First Name"
                value={formData.firstName} 
                onChange={(e) => setFormData({...formData, firstName: e.target.value})} 
              />
              {errors.firstName && <span className="error-hint">{errors.firstName}</span>}
            </div>

            <div className="form-group">
              <label>Last Name</label>
              <input 
                type="text" 
                className={`modern-input ${errors.lastName ? 'has-error' : ''}`}
                placeholder="Last Name"
                value={formData.lastName} 
                onChange={(e) => setFormData({...formData, lastName: e.target.value})} 
              />
              {errors.lastName && <span className="error-hint">{errors.lastName}</span>}
            </div>
          </div>

          <div className="form-group">
            <label>Email Address</label>
            <input 
              type="email" 
              className={`modern-input ${errors.email ? 'has-error' : ''}`}
              placeholder="john.doe@example.com"
              value={formData.email} 
              onChange={(e) => setFormData({...formData, email: e.target.value})} 
            />
            {errors.email && <span className="error-hint">{errors.email}</span>}
          </div>
          
          <div className="form-group">
            <label>Password</label>
            <input 
              type="password" 
              className={`modern-input ${errors.password ? 'has-error' : ''}`}
              placeholder="••••••••"
              value={formData.password} 
              onChange={(e) => setFormData({...formData, password: e.target.value})} 
            />
            {errors.password && <span className="error-hint">{errors.password}</span>}
          </div>
          
          <button type="submit" className="btn-auth-primary">Sign Up</button>
        </form>

        <footer className="auth-footer">
          <p>Already have an account? <Link to="/login" className="accent-link">Log in</Link></p>
        </footer>
      </div>
    </div>
  );
};

export default Register;