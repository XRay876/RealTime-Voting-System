import { Navigate, Outlet } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

export const ProtectedRoute = ({ requireAdmin = false }) => {
  const { user, loading } = useAuth();

  if (loading) return <div className="loader">Loading...</div>;
  if (!user) return <Navigate to="/login" replace />;
  if (requireAdmin && user.role !== 'ROLE_ADMIN') return <Navigate to="/403" replace />; 

  return <Outlet />;
};