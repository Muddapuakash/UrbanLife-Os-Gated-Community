import { useEffect, useState, useCallback } from 'react';
import DashboardLayout from '../../../components/layout/DashboardLayout';
import PageHeader from '../../../components/common/PageHeader';
import Table from '../../../components/common/Table';
import Badge from '../../../components/common/Badge';
import LoadingSpinner from '../../../components/common/LoadingSpinner';
import Modal from '../../../components/common/Modal';
import FormInput from '../../../components/common/FormInput';
import { pollApi } from '../../../api/pollApi';
import { voteApi } from '../../../api/voteApi';

export default function PollListPage() {
  const [polls, setPolls] = useState([]);
  const [userVotes, setUserVotes] = useState({}); // { [pollId]: optionId }
  const [loading, setLoading] = useState(true);
  const [isModalOpen, setIsModalOpen] = useState(false);

  const userRole = (localStorage.getItem('role') || '').replace('ROLE_', '');
  const isAdmin = userRole === 'ADMIN' || userRole === 'SUPER_ADMIN';
  const isResident = userRole === 'RESIDENT';
  const currentUserId = Number(localStorage.getItem('userId')) || 1;

  const [formData, setFormData] = useState({
    question: '',
    description: '',
    startTime: new Date().toISOString().slice(0, 16),
    endTime: new Date(Date.now() + 7 * 86400000).toISOString().slice(0, 16),
    options: ['Yes', 'No'],
    communityId: 1
  });

  const loadPolls = useCallback(() => {
    setLoading(true);

    Promise.allSettled([
      pollApi.getByCommunity(1),
      voteApi.getUserVotes(currentUserId)
    ]).then(([pollsRes, votesRes]) => {
      if (pollsRes.status === 'fulfilled') {
        setPolls(pollsRes.value.data || []);
      }
      if (votesRes.status === 'fulfilled') {
        const votesMap = {};
        (votesRes.value.data || []).forEach(v => {
          const pId = v.pollId || v.poll?.pollId;
          const oId = v.optionId || v.option?.optionId;
          if (pId) votesMap[pId] = oId;
        });
        setUserVotes(votesMap);
      }
    }).finally(() => setLoading(false));
  }, [currentUserId]);

  useEffect(() => {
    loadPolls();
  }, [loadPolls]);

  const handleCastVote = (pollId, optionId, optionText) => {
    voteApi.castVote(pollId, currentUserId, optionId)
      .then(() => {
        alert(`🎉 Your vote "${optionText}" has been recorded!`);
        loadPolls();
      })
      .catch(err => {
        console.error(err);
        alert(err.response?.data?.message || 'You have already voted in this poll.');
      });
  };

  const handleOptionChange = (index, value) => {
    const updated = [...formData.options];
    updated[index] = value;
    setFormData({ ...formData, options: updated });
  };

  const addOption = () => {
    if (formData.options.length < 5) {
      setFormData({ ...formData, options: [...formData.options, ''] });
    }
  };

  const removeOption = (index) => {
    if (formData.options.length > 2) {
      setFormData({ ...formData, options: formData.options.filter((_, i) => i !== index) });
    }
  };

  const handleCreatePoll = (e) => {
    e.preventDefault();
    const validOptions = formData.options.map(o => o.trim()).filter(Boolean);

    if (validOptions.length < 2) {
      alert('Please provide at least 2 voting options.');
      return;
    }

    const payload = {
      ...formData,
      options: validOptions,
      createdByUserId: currentUserId,
      communityId: Number(formData.communityId || 1)
    };

    pollApi.create(payload)
      .then((res) => {
        const pollId = res.data?.id || res.data?.pollId;
        if (pollId) {
          pollApi.activate(pollId).catch(() => {});
        }
        setIsModalOpen(false);
        alert('📊 Voting Poll Launched Successfully!');
        setFormData(prev => ({ ...prev, question: '', description: '', options: ['Yes', 'No'] }));
        loadPolls();
      })
      .catch(err => {
        console.error(err);
        alert(err.response?.data?.message || 'Failed to launch poll.');
      });
  };

  const columns = [
    {
      header: 'ID',
      render: (r) => (
        <strong style={{ color: 'var(--primary)', fontFamily: 'monospace' }}>
          #{r.pollId || r.id}
        </strong>
      )
    },
    {
      header: 'Question',
      render: (r) => (
        <div>
          <div style={{ fontWeight: 600, color: 'var(--text)' }}>{r.question}</div>
          {r.description && (
            <div style={{ fontSize: '0.78rem', color: 'var(--text-muted)', marginTop: '2px' }}>
              {r.description}
            </div>
          )}
        </div>
      )
    },
    {
      header: 'Voting Options & Action',
      render: (r) => {
        const pId = r.pollId || r.id;
        const votedOptionId = userVotes[pId];
        const optionsList = r.options || [];

        return (
          <div style={{ display: 'flex', gap: '8px', flexWrap: 'wrap', alignItems: 'center' }}>
            {optionsList.map((opt) => {
              const oId = opt.optionId || opt.id;
              const text = opt.optionText || opt.text || opt;
              const isSelected = votedOptionId === oId;
              const percentage = opt.percentage != null ? Math.round(opt.percentage) : null;

              return (
                <button
                  key={oId || text}
                  onClick={() => handleCastVote(pId, oId, text)}
                  disabled={Boolean(votedOptionId)}
                  style={{
                    padding: '6px 14px',
                    fontSize: '0.82rem',
                    fontWeight: isSelected ? 700 : 600,
                    borderRadius: '8px',
                    border: isSelected ? '2px solid var(--success)' : '1px solid var(--primary)',
                    background: isSelected
                      ? 'rgba(16,185,129,0.2)'
                      : votedOptionId
                        ? 'var(--surface)'
                        : 'var(--primary-light)',
                    color: isSelected ? 'var(--success)' : 'var(--primary)',
                    cursor: votedOptionId ? 'default' : 'pointer',
                    display: 'inline-flex',
                    alignItems: 'center',
                    gap: '6px',
                    transition: 'all 0.2s'
                  }}
                >
                  {isSelected && '✓ '}
                  {text}
                  {percentage != null && (
                    <span style={{ fontSize: '0.72rem', opacity: 0.8, marginLeft: '4px' }}>
                      ({percentage}%)
                    </span>
                  )}
                </button>
              );
            })}
            {votedOptionId && (
              <span style={{ fontSize: '0.75rem', color: 'var(--success)', fontWeight: 600 }}>
                ✓ Voted
              </span>
            )}
          </div>
        );
      }
    },
    {
      header: 'Total Votes',
      render: (r) => (
        <span style={{ fontSize: '0.88rem', fontWeight: 600 }}>
          {r.totalVotes != null ? r.totalVotes : (r.options || []).reduce((s, o) => s + (o.voteCount || 0), 0)}
        </span>
      )
    },
    { header: 'Status', render: (r) => <Badge text={r.status || 'ACTIVE'} /> }
  ];

  return (
    <DashboardLayout>
      <PageHeader
        title="Community Polls & Voting"
        subtitle="Cast your vote on society decision polls and view real-time voting results"
        action={
          isAdmin ? (
            <button className="btn btn-primary" onClick={() => setIsModalOpen(true)}>
              + Create Poll
            </button>
          ) : null
        }
      />
      {loading ? (
        <LoadingSpinner />
      ) : (
        <Table columns={columns} data={polls} emptyMessage="No decision polls launched yet." />
      )}

      {isAdmin && (
        <Modal isOpen={isModalOpen} onClose={() => setIsModalOpen(false)} title="Create Resident Decision Poll">
          <form onSubmit={handleCreatePoll}>
            <FormInput
              label="Poll Question"
              name="question"
              value={formData.question}
              onChange={e => setFormData({ ...formData, question: e.target.value })}
              placeholder="e.g. Should we upgrade the Gymnasium equipment?"
              required
            />
            <FormInput
              label="Description / Context"
              type="textarea"
              name="description"
              value={formData.description}
              onChange={e => setFormData({ ...formData, description: e.target.value })}
              placeholder="Explain background details for this vote..."
              required
            />
            <FormInput
              label="Voting Start Time"
              type="datetime-local"
              name="startTime"
              value={formData.startTime}
              onChange={e => setFormData({ ...formData, startTime: e.target.value })}
              required
            />
            <FormInput
              label="Voting End Time"
              type="datetime-local"
              name="endTime"
              value={formData.endTime}
              onChange={e => setFormData({ ...formData, endTime: e.target.value })}
              required
            />

            <div style={{ margin: '14px 0' }}>
              <label style={{ fontSize: '0.85rem', fontWeight: 600, display: 'block', marginBottom: '8px' }}>
                Voting Choice Options (2 to 5 options)
              </label>
              {formData.options.map((opt, idx) => (
                <div key={idx} style={{ display: 'flex', gap: '8px', marginBottom: '8px' }}>
                  <input
                    type="text"
                    className="form-control"
                    style={{ flex: 1, padding: '8px 12px', background: 'var(--surface)', border: '1px solid var(--border)', borderRadius: '6px', color: 'var(--text)' }}
                    value={opt}
                    onChange={e => handleOptionChange(idx, e.target.value)}
                    placeholder={`Option ${idx + 1}`}
                    required
                  />
                  {formData.options.length > 2 && (
                    <button
                      type="button"
                      onClick={() => removeOption(idx)}
                      style={{ background: 'rgba(239,68,68,0.15)', color: 'var(--danger)', border: 'none', borderRadius: '6px', padding: '0 12px', cursor: 'pointer' }}
                    >
                      ✕
                    </button>
                  )}
                </div>
              ))}
              {formData.options.length < 5 && (
                <button
                  type="button"
                  onClick={addOption}
                  style={{ fontSize: '0.8rem', color: 'var(--primary)', background: 'none', border: '1px dashed var(--primary)', padding: '6px 12px', borderRadius: '6px', cursor: 'pointer', marginTop: '4px' }}
                >
                  + Add Another Choice Option
                </button>
              )}
            </div>

            <button type="submit" className="btn btn-primary" style={{ width: '100%', marginTop: '12px' }}>
              Launch Poll to Residents
            </button>
          </form>
        </Modal>
      )}
    </DashboardLayout>
  );
}
