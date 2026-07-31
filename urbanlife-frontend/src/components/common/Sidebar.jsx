import { NavLink } from 'react-router-dom';
import { useAuth } from '../../auth/AuthContext';
import {
  Building2, Users, Shield, Grid, Home, FileText,
  UserCheck, AlertTriangle, Package, Calendar, Vote,
  Car, Wrench, ShieldAlert, Award, Clock, DollarSign, Search
} from 'lucide-react';

export default function Sidebar() {
  const { user } = useAuth();
  const role = user?.role?.replace('ROLE_', '');

  const renderRoleLinks = () => {
    switch (role) {
      case 'SUPER_ADMIN':
        return (
          <>
            <li className="sidebar-item"><NavLink to="/super-admin/dashboard"><Grid size={18} /> Overview</NavLink></li>
            <li className="sidebar-item"><NavLink to="/super-admin/communities"><Building2 size={18} /> Communities</NavLink></li>
            <li className="sidebar-item"><NavLink to="/super-admin/users"><Users size={18} /> User Management</NavLink></li>
            <li className="sidebar-item"><NavLink to="/super-admin/roles"><Shield size={18} /> Roles & Auth</NavLink></li>
          </>
        );
      case 'ADMIN':
        return (
          <>
            <li className="sidebar-item"><NavLink to="/admin/dashboard"><Grid size={18} /> Dashboard</NavLink></li>
            <li className="sidebar-item"><NavLink to="/admin/blocks"><Building2 size={18} /> Blocks & Flats</NavLink></li>
            <li className="sidebar-item"><NavLink to="/admin/residents"><UserCheck size={18} /> Resident Directory</NavLink></li>
            <li className="sidebar-item"><NavLink to="/admin/parking"><Car size={18} /> Parking Allocations</NavLink></li>
            <li className="sidebar-item"><NavLink to="/admin/complaints"><Wrench size={18} /> Complaints</NavLink></li>
            <li className="sidebar-item"><NavLink to="/admin/notices"><FileText size={18} /> Notices</NavLink></li>
            <li className="sidebar-item"><NavLink to="/admin/events"><Calendar size={18} /> Events & RSVP</NavLink></li>
            <li className="sidebar-item"><NavLink to="/admin/polls"><Vote size={18} /> Polls & Voting</NavLink></li>
            <li className="sidebar-item"><NavLink to="/admin/amenities"><Award size={18} /> Society Amenities</NavLink></li>
            <li className="sidebar-item"><NavLink to="/admin/maintenance"><DollarSign size={18} /> Billing & Finance</NavLink></li>
            <li className="sidebar-item"><NavLink to="/admin/staff"><Users size={18} /> Domestic Staff</NavLink></li>
            <li className="sidebar-item"><NavLink to="/admin/lost-found"><Search size={18} /> Lost & Found</NavLink></li>
          </>
        );
      case 'RESIDENT':
        return (
          <>
            <li className="sidebar-item"><NavLink to="/resident/dashboard"><Home size={18} /> My Portal</NavLink></li>
            <li className="sidebar-item"><NavLink to="/resident/notices"><FileText size={18} /> Notice Board</NavLink></li>
            <li className="sidebar-item"><NavLink to="/resident/events"><Calendar size={18} /> Events & RSVP</NavLink></li>
            <li className="sidebar-item"><NavLink to="/resident/polls"><Vote size={18} /> Polls & Voting</NavLink></li>
            <li className="sidebar-item"><NavLink to="/resident/visitors"><Users size={18} /> Visitor Passes</NavLink></li>
            <li className="sidebar-item"><NavLink to="/resident/vehicles"><Car size={18} /> My Vehicles</NavLink></li>
            <li className="sidebar-item"><NavLink to="/resident/staff"><Users size={18} /> Domestic Staff</NavLink></li>
            <li className="sidebar-item"><NavLink to="/resident/lost-found"><Search size={18} /> Lost & Found</NavLink></li>
            <li className="sidebar-item"><NavLink to="/resident/complaints"><Wrench size={18} /> Raise Complaint</NavLink></li>
            <li className="sidebar-item"><NavLink to="/resident/amenities"><Award size={18} /> Book Amenities</NavLink></li>
            <li className="sidebar-item"><NavLink to="/resident/bills"><DollarSign size={18} /> Pay Bills</NavLink></li>
            <li className="sidebar-item"><NavLink to="/resident/emergencies"><ShieldAlert size={18} style={{ color: 'var(--danger)' }} /> SOS Alert</NavLink></li>
            <li className="sidebar-item"><NavLink to="/resident/parcels"><Package size={18} /> Deliveries</NavLink></li>
          </>
        );
      case 'SECURITY':
        return (
          <>
            <li className="sidebar-item"><NavLink to="/security/dashboard"><Shield size={18} /> Gate Control</NavLink></li>
            <li className="sidebar-item"><NavLink to="/security/visitors"><UserCheck size={18} /> Visitor Check-In</NavLink></li>
            <li className="sidebar-item"><NavLink to="/security/parcels"><Package size={18} /> Log Deliveries</NavLink></li>
            <li className="sidebar-item"><NavLink to="/security/staff-entry"><Clock size={18} /> Domestic Staff Entry</NavLink></li>
            <li className="sidebar-item"><NavLink to="/security/lost-found"><Search size={18} /> Lost & Found Desk</NavLink></li>
            <li className="sidebar-item"><NavLink to="/security/emergencies"><ShieldAlert size={18} style={{ color: 'var(--danger)' }} /> Emergency Desk</NavLink></li>
          </>
        );
      case 'STAFF':
        return (
          <>
            <li className="sidebar-item"><NavLink to="/staff/dashboard"><Grid size={18} /> My Workbench</NavLink></li>
            <li className="sidebar-item"><NavLink to="/staff/complaints"><Wrench size={18} /> Assigned Tickets</NavLink></li>
            <li className="sidebar-item"><NavLink to="/staff/emergency"><ShieldAlert size={18} /> Emergency Response</NavLink></li>
          </>
        );
      default:
        return null;
    }
  };

  return (
    <aside className="sidebar">
      <div className="sidebar-logo">
        <div className="sidebar-logo-icon">U</div>
        <div className="sidebar-logo-text">UrbanLife</div>
      </div>
      <ul className="sidebar-menu">
        {renderRoleLinks()}
      </ul>
    </aside>
  );
}
