import Sidebar from '../common/Sidebar';
import Navbar from '../common/Navbar';

export default function DashboardLayout({ children }) {
  return (
    <div className="app-layout">
      <Sidebar />
      <div className="main-content">
        <Navbar />
        <main className="page-container">
          {children}
        </main>
      </div>
    </div>
  );
}
