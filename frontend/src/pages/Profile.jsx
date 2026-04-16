import { useState } from 'react';
import { useAuth } from '../context/AuthContext';
import { UserService } from '../api/services';

const Profile = () => {
  const { user, fetchUser, refreshTokens } = useAuth();
  const [profileData, setProfileData] = useState({ 
    firstName: user?.firstName || '', 
    lastName: user?.lastName || '' 
  });
  const [passwords, setPasswords] = useState({ oldPassword: '', newPassword: '' });
  const [fieldErrors, setFieldErrors] = useState({}); 
  const [notification, setNotification] = useState(null);

  const passwordRegex = /^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,}$/;

  const notify = (text, type = 'success') => {
    setNotification({ text, type });
    setTimeout(() => setNotification(null), 3000);
  };

  const handleUpdateProfile = async (e) => {
    e.preventDefault();
    setFieldErrors({});

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
      notify('Profile updated successfully!');
    } catch (err) {
      notify('Failed to update profile', 'error');
    }
  };

  const handleAdminPromote = async () => {
    if (window.confirm("You will become an admin. Proceed?")) {
      try {
        await UserService.promoteToAdmin();
        await refreshTokens();
        notify('You are now an admin!');
      } catch (err) {
        notify('Promotion failed', 'error');
      }
    }
  };

  const handleChangePassword = async (e) => {
    e.preventDefault();
    setFieldErrors({});

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
      notify('Password changed successfully!');
    } catch (err) {
      notify(err.response?.data?.message || 'Password change failed', 'error');
    }
  };

  return (
    <div className="profile-container">
      {notification && (
        <div className={`notification-toast ${notification.type}`}>
          {notification.text}
        </div>
      )}

      <div className="profile-header-card">
        <div className="profile-avatar-section">
          <div className="avatar-circle">
            {user?.firstName?.[0]}{user?.lastName?.[0]}
          </div>
          <div className="avatar-text">
            <h1>{user?.firstName} {user?.lastName}</h1>
            <p className="profile-email">{user?.email}</p>
            <span className={`role-badge ${user?.role?.toLowerCase()}`}>
              {user?.role?.replace('ROLE_', '')}
            </span>
          </div>
        </div>
        
        {user?.role !== 'ROLE_ADMIN' && (
          <div className="promote-banner">
            <div className="promote-text">
              <h3>Unlock Admin Features</h3>
              <p>Create and manage your own polls by becoming an administrator.</p>
            </div>
            <button className="btn-promote-action" onClick={handleAdminPromote}>
              Become Admin
            </button>
          </div>
        )}
      </div>

      <div className="profile-grid">
        <div className="profile-card">
          <h2>Personal Details</h2>
          <form onSubmit={handleUpdateProfile} noValidate>
            <div className="form-group">
              <label>First Name</label>
              <input 
                type="text" 
                className={fieldErrors.firstName ? 'input-error' : ''}
                value={profileData.firstName} 
                onChange={e => setProfileData({...profileData, firstName: e.target.value})} 
              />
              {fieldErrors.firstName && <span className="error-hint">{fieldErrors.firstName}</span>}
            </div>

            <div className="form-group">
              <label>Last Name</label>
              <input 
                type="text" 
                className={fieldErrors.lastName ? 'input-error' : ''}
                value={profileData.lastName} 
                onChange={e => setProfileData({...profileData, lastName: e.target.value})} 
              />
              {fieldErrors.lastName && <span className="error-hint">{fieldErrors.lastName}</span>}
            </div>
            <button type="submit" className="btn-profile-save">Save Changes</button>
          </form>
        </div>

        <div className="profile-card">
          <h2>Security</h2>
          <form onSubmit={handleChangePassword} noValidate>
            <div className="form-group">
              <label>Current Password</label>
              <input 
                type="password" 
                placeholder="••••••••"
                className={fieldErrors.oldPassword ? 'input-error' : ''}
                value={passwords.oldPassword} 
                onChange={e => setPasswords({...passwords, oldPassword: e.target.value})} 
              />
              {fieldErrors.oldPassword && <span className="error-hint">{fieldErrors.oldPassword}</span>}
            </div>

            <div className="form-group">
              <label>New Password</label>
              <input 
                type="password" 
                placeholder="••••••••"
                className={fieldErrors.newPassword ? 'input-error' : ''}
                value={passwords.newPassword} 
                onChange={e => setPasswords({...passwords, newPassword: e.target.value})} 
              />
              {fieldErrors.newPassword && <span className="error-hint">{fieldErrors.newPassword}</span>}
            </div>
            <button type="submit" className="btn-profile-secondary">Update Password</button>
          </form>
        </div>
      </div>
    </div>
  );
};

export default Profile;