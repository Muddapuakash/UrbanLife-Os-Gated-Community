import { useEffect, useState, useCallback } from 'react';
import DashboardLayout from '../../../components/layout/DashboardLayout';
import PageHeader from '../../../components/common/PageHeader';
import Table from '../../../components/common/Table';
import Badge from '../../../components/common/Badge';
import LoadingSpinner from '../../../components/common/LoadingSpinner';
import FormInput from '../../../components/common/FormInput';
import { visitorApi } from '../../../api/visitorApi';

const REFRESH_INTERVAL = 10000;

export default function GateVisitorPage() {
  const [visitors, setVisitors] = useState([]);
  const [loading, setLoading] = useState(true);
  const [passCodeInput, setPassCodeInput] = useState('');
  const [verifiedVisitor, setVerifiedVisitor] = useState(null);
  const [lastUpdated, setLastUpdated] = useState(null);

  const securityUserId = Number(localStorage.getItem('userId')) || 1;

  const loadVisitors = useCallback((showLoader = false) => {
    if (showLoader) setLoading(true);
    visitorApi.getByCommunity(1)
      .then(res => {
        setVisitors(res.data);
        setLastUpdated(new Date());
      })
      .catch(err => console.error(err))
      .finally(() => setLoading(false));
  }, []);

  useEffect(() => {
    loadVisitors(true);
    const interval = setInterval(() => loadVisitors(false), REFRESH_INTERVAL);
    return () => clearInterval(interval);
  }, [loadVisitors]);

  const handleCheckIn = (passCode, visitorId) => {
    visitorApi.checkIn(passCode, securityUserId)
      .then(() => {
        alert('✅ Visitor Check-In Successful!');
        setVerifiedVisitor(null);
        loadVisitors(false);
      })
      .catch(err => alert(err.response?.data?.message || 'Check-In failed'));
  };

  const handleCheckOut = (visitorId) => {
    visitorApi.checkOut(visitorId, securityUserId)
      .then(() => {
        alert('✅ Visitor Checked Out!');
        setVerifiedVisitor(null);
        loadVisitors(false);
      })
      .catch(err => alert(err.response?.data?.message || 'Check-Out failed'));
  };

  const handleVerifyPassCode = (e) => {
    e.preventDefault();
    setVerifiedVisitor(null);
    visitorApi.getByPassCode(passCodeInput.trim())
      .then(res => {
        setVerifiedVisitor(res.data);
      })
      .catch(err => alert('Invalid Pass Code — visitor not found.'));
  };

  const columns = [
    { header: 'Pass Code', render: (r) => <strong style={{ color: 'var(--primary)', fontFamily: 'monospace' }}>{r.passCode}</strong> },
    { header: 'Visitor Name', render: (r) => r.visitorName || r.name },
    { header: 'Phone', render: (r) => r.phone || r.phoneNumber },
    { header: 'Type', accessorKey: 'visitorType' },
    { header: 'Status', render: (r) => <Badge text={r.status} /> },
    {
      header: 'Actions',
      render: (r) => {
        const id = r.visitorId || r.id;
        return (
          <div style={{ display: 'flex', gap: '8px' }}>
            {(r.status === 'EXPECTED' || r.status === 'APPROVED') && (
              <button className="btn btn-primary" style={{ padding: '4px 10px', fontSize: '0.75rem' }} onClick={() => handleCheckIn(r.passCode, id)}>Check In</button>
            )}
            {r.status === 'INSIDE' && (
              <button className="btn btn-secondary" style={{ padding: '4px 10px', fontSize: '0.75rem' }} onClick={() => handleCheckOut(id)}>Check Out</button>
            )}
          </div>
        );
      }
    }
  ];

  return (
    <DashboardLayout>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: '4px' }}>
        <PageHeader title="Gate Visitor Scanner" subtitle="Verify pass codes and process entry/exit for guests" />
        <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)', display: 'flex', alignItems: 'center', gap: '6px', paddingTop: '8px', whiteSpace: 'nowrap' }}>
          <span style={{ width: '8px', height: '8px', borderRadius: '50%', background: 'var(--success)', display: 'inline-block' }}></span>
          Live · {lastUpdated ? lastUpdated.toLocaleTimeString('en-IN') : '...'}
        </div>
      </div>
      
      {/* Verify Pass Code */}
      <div className="card" style={{ marginBottom: '24px' }}>
        <h3>Verify Access Pass Code</h3>
        <p style={{ fontSize: '0.85rem', color: 'var(--text-muted)', margin: '4px 0 12px' }}>
          Ask the visitor for the pass code shared by the resident (e.g. <code>VIS-727DDC48</code>)
        </p>
        <form onSubmit={handleVerifyPassCode} style={{ display: 'flex', gap: '12px' }}>
          <div style={{ flex: 1 }}>
            <FormInput 
              placeholder="Enter Pass Code (e.g. VIS-727DDC48)" 
              value={passCodeInput} 
              onChange={e => setPassCodeInput(e.target.value)} 
              required 
            />
          </div>
          <button type="submit" className="btn btn-primary" style={{ height: '42px' }}>Verify Pass Code</button>
        </form>

        {/* Verified Visitor Card */}
        {verifiedVisitor && (
          <div style={{ marginTop: '16px', padding: '16px', background: 'rgba(16,185,129,0.08)', border: '1px solid var(--success)', borderRadius: '10px' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', flexWrap: 'wrap', gap: '12px' }}>
              <div>
                <div style={{ fontSize: '1rem', fontWeight: 700, color: 'var(--success)' }}>
                  ✅ Valid Pass — {verifiedVisitor.passCode}
                </div>
                <div style={{ marginTop: '6px', color: 'var(--text-muted)', fontSize: '0.85rem', lineHeight: '1.7' }}>
                  <strong>Visitor:</strong> {verifiedVisitor.visitorName || verifiedVisitor.name}<br />
                  <strong>Phone:</strong> {verifiedVisitor.phone || verifiedVisitor.phoneNumber}<br />
                  <strong>Type:</strong> {verifiedVisitor.visitorType}<br />
                  <strong>Status:</strong> <Badge text={verifiedVisitor.status} />
                </div>
              </div>
              <div style={{ display: 'flex', gap: '10px' }}>
                {(verifiedVisitor.status === 'EXPECTED' || verifiedVisitor.status === 'APPROVED') && (
                  <button className="btn btn-primary" onClick={() => handleCheckIn(verifiedVisitor.passCode, verifiedVisitor.visitorId || verifiedVisitor.id)}>
                    ✅ Check In Visitor
                  </button>
                )}
                {verifiedVisitor.status === 'INSIDE' && (
                  <button className="btn btn-secondary" onClick={() => handleCheckOut(verifiedVisitor.visitorId || verifiedVisitor.id)}>
                    🔄 Check Out Visitor
                  </button>
                )}
              </div>
            </div>
          </div>
        )}
      </div>

      {/* Visitor Table */}
      {loading ? <LoadingSpinner /> : <Table columns={columns} data={visitors} emptyMessage="No visitors logged yet" />}
    </DashboardLayout>
  );
}
