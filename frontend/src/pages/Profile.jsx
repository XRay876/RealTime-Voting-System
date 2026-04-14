import { useState } from 'react';
import { useAuth } from '../context/AuthContext';
import { UserService } from '../api/services';

const Profile = () => {
  const { user, fetchUser } = useAuth();
  const [profileData, setProfileData] = useState({ 
    firstName: user?.firstName || '', 
    lastName: user?.lastName || '' 
  });
  const [passwords, setPasswords] = useState({ oldPassword: '', newPassword: '' });
  const [msg, setMsg] = useState({ type: '', text: '' });
  const [fieldErrors, setFieldErrors] = useState({}); 
  const [status, setStatus] = useState('');

  const passwordRegex = /^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,}$/;


  const handleUpdateProfile = async (e) => {
    e.preventDefault();
    setFieldErrors({});
    setMsg({ type: '', text: '' });

    const errors = {};
    if (profileData.firstName.trim().length < 2 || profileData.firstName.trim().length > 50) {
      errors.firstName = 'First name must be between 2 and 50 characters';
    }
    if (profileData.lastName.trim().length < 2 || profileData.lastName.trim().length > 50) {
      errors.lastName = 'Last name must be between 2 and 50 characters';
    }

    if (Object.keys(errors).length > 0) {
      return setFieldErrors(errors);
    }

    try {
      await UserService.updateProfile(profileData);
      await fetchUser();
      setMsg({ type: 'success', text: 'Profile updated successfully!' });
    } catch (err) {
      setMsg({ type: 'error', text: 'Failed to update profile' });
    }
  };

  const handleAdminPromote = async () => {
    if (window.confirm("You will become an admin and will be logged out to refresh your token. Proceed?")) {
      await UserService.promoteToAdmin();
      logout();
    }
  };

  const handleChangePassword = async (e) => {
    e.preventDefault();
    setFieldErrors({});
    setMsg({ type: '', text: '' });

    const errors = {};
    if (!passwords.oldPassword) {
      errors.oldPassword = 'Current password is required';
    }
    
    if (!passwordRegex.test(passwords.newPassword)) {
      errors.newPassword = 'Password must have 8+ chars, 1 uppercase, 1 lowercase, 1 digit';
    }
    
    if (passwords.oldPassword === passwords.newPassword && passwords.newPassword !== '') {
      errors.newPassword = 'New password must be different from the old one';
    }

    if (Object.keys(errors).length > 0) {
      return setFieldErrors(errors);
    }

    try {
      await UserService.changePassword(passwords);
      setPasswords({ oldPassword: '', newPassword: '' });
      setMsg({ type: 'success', text: 'Password changed successfully!' });
    } catch (err) {
      setMsg({ type: 'error', text: err.response?.data?.message || 'Password change failed' });
    }
  };

  return (
    <div className="container">
      <div className="card">

        <h2>User Profile</h2>
        {status && <p className="alert success">{status}</p>}
        <div className="user-info-static">
          <p><strong>Email:</strong> {user?.email}</p>
          <p><strong>Current Role:</strong> {user?.role}</p>
        </div>

        {user?.role !== 'ROLE_ADMIN' && (
          <button className="btn-admin full-width mt-1" onClick={handleAdminPromote}>
            BECOME ADMIN
          </button>
        )}

        <form onSubmit={handleUpdateProfile} className="mt-2">
          <h3>Personal Details</h3>
          <div className="form-group">
            <label>First Name</label>
            <input value={profileData.firstName} onChange={e => setProfileData({...profileData, firstName: e.target.value})} />
          </div>
          <div className="form-group">
            <label>Last Name</label>
            <input value={profileData.lastName} onChange={e => setProfileData({...profileData, lastName: e.target.value})} />
          </div>
          <button type="submit" className="btn-primary">Save Changes</button>
        </form>
      

        <h2>Edit Profile</h2>
        {msg.text && <div className={`alert ${msg.type}`}>{msg.text}</div>}
        
        <form onSubmit={handleUpdateProfile} noValidate>
          <div className="form-group">
            <label>First Name</label>
            <input 
              type="text" 
              className={fieldErrors.firstName ? 'input-error' : ''}
              value={profileData.firstName} 
              onChange={e => setProfileData({...profileData, firstName: e.target.value})} 
            />
            {fieldErrors.firstName && <span className="error-text">{fieldErrors.firstName}</span>}
          </div>

          <div className="form-group">
            <label>Last Name</label>
            <input 
              type="text" 
              className={fieldErrors.lastName ? 'input-error' : ''}
              value={profileData.lastName} 
              onChange={e => setProfileData({...profileData, lastName: e.target.value})} 
            />
            {fieldErrors.lastName && <span className="error-text">{fieldErrors.lastName}</span>}
          </div>
          <button type="submit" className="btn-primary">Update Profile</button>
        </form>

        <hr style={{ margin: '2rem 0' }} />

        <h2>Change Password</h2>
        <form onSubmit={handleChangePassword} noValidate>
          <div className="form-group">
            <label>Current Password</label>
            <input 
              type="password" 
              className={fieldErrors.oldPassword ? 'input-error' : ''}
              value={passwords.oldPassword} 
              onChange={e => setPasswords({...passwords, oldPassword: e.target.value})} 
            />
            {fieldErrors.oldPassword && <span className="error-text">{fieldErrors.oldPassword}</span>}
          </div>

          <div className="form-group">
            <label>New Password</label>
            <input 
              type="password" 
              className={fieldErrors.newPassword ? 'input-error' : ''}
              value={passwords.newPassword} 
              onChange={e => setPasswords({...passwords, newPassword: e.target.value})} 
            />
            {fieldErrors.newPassword && <span className="error-text">{fieldErrors.newPassword}</span>}
          </div>
          <button type="submit" className="btn-secondary">Update Password</button>
        </form>
      </div>
    </div>
  );
};

export default Profile;