import { useState, useEffect } from 'react';
import { PollsService } from '../api/services';
import { Link } from 'react-router-dom';

const AdminDashboard = () => {
  const [myPolls, setMyPolls] = useState([]);
  const [newPoll, setNewPoll] = useState({ title: '', description: '', multipleChoice: false });
  const [editingPoll, setEditingPoll] = useState(null);
  const [newOption, setNewOption] = useState({ name: '', description: '' });

  useEffect(() => { loadPolls(); }, []);

  const loadPolls = async () => {
    const { data } = await PollsService.getMyPolls();
    setMyPolls(data);
  };

  const handleCreate = async (e) => {
    e.preventDefault();
    await PollsService.createPoll(newPoll);
    setNewPoll({ title: '', description: '', multipleChoice: false });
    loadPolls();
  };

  const handleDeletePoll = async (id) => {
    if (window.confirm("Are you sure you want to delete this poll?")) {
      await PollsService.deletePoll(id);
      loadPolls();
    }
  };

  const handleAddOption = async (pollId) => {
    if (!newOption.name.trim()) return;
    await PollsService.addOption(pollId, newOption);
    setNewOption({ name: '', description: '' });
    setEditingPoll(null);
    loadPolls();
  };

  const updateStatus = async (id, status) => {
    await PollsService.updatePollStatus(id, status);
    loadPolls();
  };

  const deleteOption = async (optionId) => {
    await PollsService.deleteOption(optionId);
    loadPolls();
  };

  return (
    <div className="dashboard-wrapper">
      <header className="dashboard-header">
        <h1>Admin Dashboard</h1>
        <p>Manage your polls and view real-time engagement.</p>
      </header>

      <section className="creation-section">
        <div className="glass-card">
          <h2>Create New Poll</h2>
          <form onSubmit={handleCreate} className="poll-form">
            <div className="form-row">
              <input 
                className="modern-input" 
                placeholder="Poll Title" 
                value={newPoll.title} 
                onChange={e => setNewPoll({...newPoll, title: e.target.value})} 
                required 
              />
            </div>
            <div className="form-row">
              <textarea 
                className="modern-input" 
                placeholder="Poll Description" 
                value={newPoll.description} 
                onChange={e => setNewPoll({...newPoll, description: e.target.value})} 
              />
            </div>
            <div className="form-footer">
              <label className="toggle-label">
                <input 
                  type="checkbox" 
                  checked={newPoll.multipleChoice} 
                  onChange={e => setNewPoll({...newPoll, multipleChoice: e.target.checked})} 
                />
                <span>Allow multiple choices</span>
              </label>
              <button type="submit" className="btn-submit">Create Poll</button>
            </div>
          </form>
        </div>
      </section>

      <section className="polls-section">
        <div className="section-title-area">
          <h2>My Active Polls</h2>
          <span className="count-tag">{myPolls.length} Polls</span>
        </div>

        <div className="polls-grid">
          {myPolls.map(p => (
            <div key={p.id} className="poll-management-card">
              <div className="card-header">
                <div className="title-group">
                  <h3>{p.title}</h3>
                  <span className={`status-pill ${p.status.toLowerCase()}`}>{p.status}</span>
                </div>
                <div className="status-controls">
                  <button onClick={() => updateStatus(p.id, 'OPEN')} title="Open Poll" className="ctrl-btn">Open</button>
                  <button onClick={() => updateStatus(p.id, 'CLOSED')} title="Close Poll" className="ctrl-btn">Close</button>
                  <button onClick={() => updateStatus(p.id, 'DRAFT')} title="Set to Draft" className="ctrl-btn">Draft</button>
                </div>
              </div>

              <div className="card-body">
                <div className="options-manager">
                  <div className="options-header">
                    <h4>Options</h4>
                  </div>
                  <ul className="options-list">
                    {p.options.map(o => (
                      <li key={o.id}>
                        <span>{o.name}</span>
                        <button className="remove-opt" onClick={() => deleteOption(o.id)}>×</button>
                      </li>
                    ))}
                  </ul>
                  <div className="add-opt-box">
                    <input 
                      className="mini-input"
                      placeholder="Add choice..." 
                      value={editingPoll === p.id ? newOption.name : ''} 
                      onFocus={() => setEditingPoll(p.id)}
                      onChange={e => setNewOption({...newOption, name: e.target.value})} 
                    />
                    <button className="btn-add-mini" onClick={() => handleAddOption(p.id)}>Add</button>
                  </div>
                </div>
              </div>

              <div className="card-footer">
                <Link to={`/polls/${p.id}`} className="btn-view">View Results</Link>
                <button className="btn-delete-main" onClick={() => handleDeletePoll(p.id)}>Delete Poll</button>
              </div>
            </div>
          ))}
        </div>
      </section>
    </div>
  );
};

export default AdminDashboard;