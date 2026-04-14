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
    if (window.confirm("Delete this poll?")) {
      await PollsService.deletePoll(id);
      loadPolls();
    }
  };

  const handleAddOption = async (pollId) => {
    await PollsService.addOption(pollId, newOption);
    setNewOption({ name: '', description: '' });
    loadPolls();
  };

  return (
    <div className="admin-container">
      <div className="card">
        <h2>Create New Poll</h2>
        <form onSubmit={handleCreate} className="create-poll-form">
          <input className="input-field" placeholder="Title" value={newPoll.title} 
                 onChange={e => setNewPoll({...newPoll, title: e.target.value})} required />
          <textarea className="input-field" placeholder="Description" value={newPoll.description} 
                    onChange={e => setNewPoll({...newPoll, description: e.target.value})} />
          <label className="checkbox-label">
            <input type="checkbox" checked={newPoll.multipleChoice} 
                   onChange={e => setNewPoll({...newPoll, multipleChoice: e.target.checked})} />
            Allow Multiple Choice
          </label>
          <button type="submit" className="btn-primary">Create Poll</button>
        </form>
      </div>

      <div className="mt-2">
        <h2>My Polls</h2>
        {myPolls.map(p => (
          <div key={p.id} className="card mt-1 poll-management-card">
            <div className="poll-info">
              <h3>{p.title} <span className="badge">{p.status}</span></h3>
              <div className="poll-admin-actions">
                <button onClick={() => PollsService.updatePollStatus(p.id, 'OPEN').then(loadPolls)}>Open</button>
                <button onClick={() => PollsService.updatePollStatus(p.id, 'CLOSED').then(loadPolls)}>Close</button>
                <button onClick={() => PollsService.updatePollStatus(p.id, 'DRAFT').then(loadPolls)}>Draft</button>
                <Link to={`/polls/${p.id}`} className="btn-small">View Results</Link>
                <button className="btn-danger" onClick={() => handleDeletePoll(p.id)}>Delete</button>
              </div>
            </div>

            <div className="options-management mt-1">
              <h4>Options:</h4>
              <ul>
                {p.options.map(o => (
                  <li key={o.id}>
                    {o.name} <button className="btn-link-danger" onClick={() => PollsService.deleteOption(o.id).then(loadPolls)}>remove</button>
                  </li>
                ))}
              </ul>
              <div className="add-option-inline">
                <input placeholder="Option name" value={editingPoll === p.id ? newOption.name : ''} 
                       onFocus={() => setEditingPoll(p.id)}
                       onChange={e => setNewOption({...newOption, name: e.target.value})} />
                <button className="btn-small" onClick={() => handleAddOption(p.id)}>Add</button>
              </div>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};

export default AdminDashboard;