import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext';
import { ProtectedRoute } from './components/ProtectedRoute';
import Navbar from './components/Navbar';
import Home from './pages/Home';
import Login from './pages/Login';
import Register from './pages/Register';
import ErrorPage from './pages/ErrorPage';
import Profile from './pages/Profile';
import AdminDashboard from './pages/AdminDashboard';

import './styles/main.css';

function App() {
  return (
    <AuthProvider>
      <Router>
        <Navbar />
        <main className="container">
          <Routes>
            <Route path="/login" element={<Login />} />
            <Route path="/register" element={<Register />} />
            
            <Route element={<ProtectedRoute />}>
              <Route path="/" element={<Home />} />
              <Route path="/polls/:id" element={<div>Poll's Details</div>} />
              <Route path="/profile" element={<Profile />} />
            </Route>

            <Route element={<ProtectedRoute requireAdmin={true} />}>
              <Route path="/admin" element={<AdminDashboard />} />
            </Route>

            <Route path="/403" element={<ErrorPage code={403} message="Access Restricted" />} />
            <Route path="/error" element={<ErrorPage code={500} message="Something went wrong" />} />
            <Route path="*" element={<ErrorPage code={404} message="Page not found" />} />
          </Routes>
        </main>
      </Router>
    </AuthProvider>
  );
}

export default App;