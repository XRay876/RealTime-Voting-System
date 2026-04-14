import { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { AuthService } from '../api/services';

const Register = () => {
  const [formData, setFormData] = useState({ firstName: '', lastName: '', email: '', password: '' });
  const [errors, setErrors] = useState({});
  const navigate = useNavigate();

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
      await AuthService.register(formData);
      navigate('/');
    } catch (err) {
      setErrors({ server: err.response?.data?.message || 'Registration failed' });
    }
  };

  return (
    <div className="auth-container">
      <form className="card auth-form" onSubmit={handleSubmit}>
        <h2>Create Account</h2>
        {errors.server && <div className="alert error">{errors.server}</div>}
        
        <div className="form-group">
          <label>First Name</label>
          <input 
            type="text" 
            value={formData.firstName} 
            onChange={(e) => setFormData({...formData, firstName: e.target.value})} 
          />
          {errors.firstName && <span className="error-text">{errors.firstName}</span>}
        </div>

        <div className="form-group">
          <label>Last Name</label>
          <input 
            type="text" 
            value={formData.lastName} 
            onChange={(e) => setFormData({...formData, lastName: e.target.value})} 
          />
          {errors.lastName && <span className="error-text">{errors.lastName}</span>}
        </div>

        <div className="form-group">
          <label>Email</label>
          <input 
            type="email" 
            value={formData.email} 
            onChange={(e) => setFormData({...formData, email: e.target.value})} 
          />
          {errors.email && <span className="error-text">{errors.email}</span>}
        </div>
        
        <div className="form-group">
          <label>Password</label>
          <input 
            type="password" 
            value={formData.password} 
            onChange={(e) => setFormData({...formData, password: e.target.value})} 
          />
          {errors.password && <span className="error-text">{errors.password}</span>}
        </div>
        
        <button type="submit" className="btn-primary full-width">Register</button>
        <p className="auth-link">Already have an account? <Link to="/login">Login</Link></p>
      </form>
    </div>
  );
};

export default Register;