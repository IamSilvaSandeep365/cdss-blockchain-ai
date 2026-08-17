import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider, useAuth } from './context/AuthContext';
import Login from './pages/Login';
import Patients from './pages/Patients';
import Diagnose from './pages/Diagnose';
import Verify from './pages/Verify';
import Dashboard from './pages/Dashboard';

// Protects routes — redirects to login if not authenticated
function ProtectedRoute({ children }) {
  const { user } = useAuth();
  return user ? children : <Navigate to="/login" replace />;
}

export default function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <Routes>
          <Route path="/login" element={<Login />} />
          <Route
            path="/patients"
            element={
              <ProtectedRoute>
                <Patients />
              </ProtectedRoute>
            }
          />
	 <Route path="/diagnose/:patientId"
  	   element={
    	   <ProtectedRoute>
      	   <Diagnose />
    	      </ProtectedRoute>
  	   }
	  />
	  <Route
  path="/verify/:prescriptionId"
  element={
    <ProtectedRoute>
      <Verify />
    </ProtectedRoute>
  }
/>
	<Route
  path="/dashboard"
  element={
    <ProtectedRoute>
      <Dashboard />
    </ProtectedRoute>
  }
/>

          <Route path="*" element={<Navigate to="/login" replace />} />
        </Routes>
      </BrowserRouter>
    </AuthProvider>
  );
}