import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider } from './auth/AuthContext';
import ProtectedRoute from './auth/ProtectedRoute';

// Public Pages & Auth
import LandingPage from './pages/public/LandingPage';
import RegisterPage from './pages/auth/RegisterPage';
import LoginPage from './pages/auth/LoginPage';

// Super Admin
import SuperAdminDashboard from './pages/super-admin/SuperAdminDashboard';
import RoleListPage from './pages/super-admin/roles/RoleListPage';
import UserListPage from './pages/super-admin/users/UserListPage';
import CommunityListPage from './pages/super-admin/communities/CommunityListPage';

// Admin
import AdminDashboard from './pages/admin/AdminDashboard';
import BlockListPage from './pages/admin/blocks/BlockListPage';
import ResidentListPage from './pages/admin/residents/ResidentListPage';
import ComplaintListPage from './pages/admin/complaints/ComplaintListPage';
import NoticeListPage from './pages/admin/notices/NoticeListPage';
import EventListPage from './pages/admin/events/EventListPage';
import PollListPage from './pages/admin/polls/PollListPage';
import AmenityListPage from './pages/admin/amenities/AmenityListPage';
import BillListPage from './pages/admin/maintenance/BillListPage';
import ParkingPage from './pages/admin/parking/ParkingPage';

import DomesticStaffListPage from './pages/admin/staff/DomesticStaffListPage';

// Resident
import ResidentDashboard from './pages/resident/ResidentDashboard';
import MyVisitorsPage from './pages/resident/visitors/MyVisitorsPage';
import MyVehiclesPage from './pages/resident/vehicles/MyVehiclesPage';
import MyComplaintsPage from './pages/resident/complaints/MyComplaintsPage';
import BookAmenityPage from './pages/resident/amenity-booking/BookAmenityPage';
import MyBillsPage from './pages/resident/bills/MyBillsPage';
import RaiseEmergencyPage from './pages/resident/emergencies/RaiseEmergencyPage';
import MyParcelsPage from './pages/resident/parcels/MyParcelsPage';
import LostFoundPage from './pages/common/LostFoundPage';
import MyStaffPage from './pages/resident/staff/MyStaffPage';

// Security
import SecurityDashboard from './pages/security/SecurityDashboard';
import GateVisitorPage from './pages/security/visitors/GateVisitorPage';
import SecurityParcelListPage from './pages/security/parcels/ParcelListPage';
import StaffEntryPage from './pages/security/domestic-staff/StaffEntryPage';
import SecurityEmergencyPage from './pages/security/emergencies/EmergencyListPage';

// Staff
import StaffDashboard from './pages/staff/StaffDashboard';
import AssignedComplaintsPage from './pages/staff/complaints/AssignedComplaintsPage';
import AssignedEmergencyPage from './pages/staff/emergency/AssignedEmergencyPage';

// Unauthorized page
function UnauthorizedPage() {
  return (
    <div style={{ minHeight: '100vh', display: 'flex', alignItems: 'center', justifyContent: 'center', backgroundColor: 'var(--bg-main)' }}>
      <div style={{ textAlign: 'center' }}>
        <div style={{ fontSize: '3rem', marginBottom: '12px' }}>🚫</div>
        <h2 style={{ fontSize: '1.5rem', fontWeight: 800, color: 'var(--text-main)' }}>Access Denied</h2>
        <p style={{ color: 'var(--text-muted)', marginTop: '8px' }}>
          You do not have permission to view this page.
        </p>
        <a href="/login" style={{ display: 'inline-block', marginTop: '16px', padding: '10px 24px', background: 'var(--primary)', color: '#000', borderRadius: '8px', fontWeight: 600 }}>
          Back to Login
        </a>
      </div>
    </div>
  );
}

export default function App() {
  return (
    <AuthProvider>
      <Router future={{ v7_startTransition: true, v7_relativeSplatPath: true }}>
        <Routes>

          {/* Public Routes */}
          <Route path="/" element={<LandingPage />} />
          <Route path="/register" element={<RegisterPage />} />
          <Route path="/login" element={<LoginPage />} />
          <Route path="/unauthorized" element={<UnauthorizedPage />} />

          {/* ===== SUPER ADMIN ===== */}
          <Route path="/super-admin/dashboard" element={
            <ProtectedRoute allowedRoles={['SUPER_ADMIN']}>
              <SuperAdminDashboard />
            </ProtectedRoute>
          } />
          <Route path="/super-admin/roles" element={
            <ProtectedRoute allowedRoles={['SUPER_ADMIN']}>
              <RoleListPage />
            </ProtectedRoute>
          } />
          <Route path="/super-admin/users" element={
            <ProtectedRoute allowedRoles={['SUPER_ADMIN']}>
              <UserListPage />
            </ProtectedRoute>
          } />
          <Route path="/super-admin/communities" element={
            <ProtectedRoute allowedRoles={['SUPER_ADMIN']}>
              <CommunityListPage />
            </ProtectedRoute>
          } />

          {/* ===== ADMIN ===== */}
          <Route path="/admin/dashboard" element={
            <ProtectedRoute allowedRoles={['ADMIN']}>
              <AdminDashboard />
            </ProtectedRoute>
          } />
          <Route path="/admin/blocks" element={
            <ProtectedRoute allowedRoles={['ADMIN']}>
              <BlockListPage />
            </ProtectedRoute>
          } />
          <Route path="/admin/residents" element={
            <ProtectedRoute allowedRoles={['ADMIN']}>
              <ResidentListPage />
            </ProtectedRoute>
          } />
          <Route path="/admin/parking" element={
            <ProtectedRoute allowedRoles={['ADMIN']}>
              <ParkingPage />
            </ProtectedRoute>
          } />
          <Route path="/admin/complaints" element={
            <ProtectedRoute allowedRoles={['ADMIN']}>
              <ComplaintListPage />
            </ProtectedRoute>
          } />
          <Route path="/admin/notices" element={
            <ProtectedRoute allowedRoles={['ADMIN']}>
              <NoticeListPage />
            </ProtectedRoute>
          } />
          <Route path="/admin/events" element={
            <ProtectedRoute allowedRoles={['ADMIN']}>
              <EventListPage />
            </ProtectedRoute>
          } />
          <Route path="/admin/polls" element={
            <ProtectedRoute allowedRoles={['ADMIN']}>
              <PollListPage />
            </ProtectedRoute>
          } />
          <Route path="/admin/amenities" element={
            <ProtectedRoute allowedRoles={['ADMIN']}>
              <AmenityListPage />
            </ProtectedRoute>
          } />
          <Route path="/admin/maintenance" element={
            <ProtectedRoute allowedRoles={['ADMIN']}>
              <BillListPage />
            </ProtectedRoute>
          } />
          <Route path="/admin/lost-found" element={
            <ProtectedRoute allowedRoles={['ADMIN']}>
              <LostFoundPage />
            </ProtectedRoute>
          } />
          <Route path="/admin/staff" element={
            <ProtectedRoute allowedRoles={['ADMIN']}>
              <DomesticStaffListPage />
            </ProtectedRoute>
          } />

          {/* ===== RESIDENT ===== */}
          <Route path="/resident/dashboard" element={
            <ProtectedRoute allowedRoles={['RESIDENT']}>
              <ResidentDashboard />
            </ProtectedRoute>
          } />
          <Route path="/resident/staff" element={
            <ProtectedRoute allowedRoles={['RESIDENT']}>
              <MyStaffPage />
            </ProtectedRoute>
          } />

          <Route path="/resident/notices" element={
            <ProtectedRoute allowedRoles={['RESIDENT']}>
              <NoticeListPage />
            </ProtectedRoute>
          } />
          <Route path="/resident/events" element={
            <ProtectedRoute allowedRoles={['RESIDENT']}>
              <EventListPage />
            </ProtectedRoute>
          } />
          <Route path="/resident/polls" element={
            <ProtectedRoute allowedRoles={['RESIDENT']}>
              <PollListPage />
            </ProtectedRoute>
          } />
          <Route path="/resident/visitors" element={
            <ProtectedRoute allowedRoles={['RESIDENT']}>
              <MyVisitorsPage />
            </ProtectedRoute>
          } />
          <Route path="/resident/vehicles" element={
            <ProtectedRoute allowedRoles={['RESIDENT']}>
              <MyVehiclesPage />
            </ProtectedRoute>
          } />
          <Route path="/resident/lost-found" element={
            <ProtectedRoute allowedRoles={['RESIDENT']}>
              <LostFoundPage />
            </ProtectedRoute>
          } />
          <Route path="/resident/complaints" element={
            <ProtectedRoute allowedRoles={['RESIDENT']}>
              <MyComplaintsPage />
            </ProtectedRoute>
          } />
          <Route path="/resident/amenities" element={
            <ProtectedRoute allowedRoles={['RESIDENT']}>
              <BookAmenityPage />
            </ProtectedRoute>
          } />
          <Route path="/resident/bills" element={
            <ProtectedRoute allowedRoles={['RESIDENT']}>
              <MyBillsPage />
            </ProtectedRoute>
          } />
          <Route path="/resident/emergencies" element={
            <ProtectedRoute allowedRoles={['RESIDENT']}>
              <RaiseEmergencyPage />
            </ProtectedRoute>
          } />
          <Route path="/resident/parcels" element={
            <ProtectedRoute allowedRoles={['RESIDENT']}>
              <MyParcelsPage />
            </ProtectedRoute>
          } />

          {/* ===== SECURITY ===== */}
          <Route path="/security/dashboard" element={
            <ProtectedRoute allowedRoles={['SECURITY']}>
              <SecurityDashboard />
            </ProtectedRoute>
          } />
          <Route path="/security/visitors" element={
            <ProtectedRoute allowedRoles={['SECURITY']}>
              <GateVisitorPage />
            </ProtectedRoute>
          } />
          <Route path="/security/parcels" element={
            <ProtectedRoute allowedRoles={['SECURITY']}>
              <SecurityParcelListPage />
            </ProtectedRoute>
          } />
          <Route path="/security/staff-entry" element={
            <ProtectedRoute allowedRoles={['SECURITY']}>
              <StaffEntryPage />
            </ProtectedRoute>
          } />
          <Route path="/security/lost-found" element={
            <ProtectedRoute allowedRoles={['SECURITY']}>
              <LostFoundPage />
            </ProtectedRoute>
          } />
          <Route path="/security/emergencies" element={
            <ProtectedRoute allowedRoles={['SECURITY']}>
              <SecurityEmergencyPage />
            </ProtectedRoute>
          } />

          {/* ===== STAFF ===== */}
          <Route path="/staff/dashboard" element={
            <ProtectedRoute allowedRoles={['STAFF']}>
              <StaffDashboard />
            </ProtectedRoute>
          } />
          <Route path="/staff/complaints" element={
            <ProtectedRoute allowedRoles={['STAFF']}>
              <AssignedComplaintsPage />
            </ProtectedRoute>
          } />
          <Route path="/staff/emergency" element={
            <ProtectedRoute allowedRoles={['STAFF']}>
              <AssignedEmergencyPage />
            </ProtectedRoute>
          } />

          {/* Fallback Redirect */}
          <Route path="*" element={<Navigate to="/" replace />} />

        </Routes>
      </Router>
    </AuthProvider>
  );
}
