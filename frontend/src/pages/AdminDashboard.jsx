import { useState, useEffect } from 'react';
import { PollsService } from '../api/services';

const AdminDashboard = () => {
  const [poll, setPoll] = useState({ title: '', description: '' });
  const [myPolls, setMyPolls] = useState([]);

  useEffect(() => { loadPolls(); }, []);

  const loadPolls = async () => {
    const { data } = await PollsService.getMyPolls();
    setMyPolls(data);
  };

  const handleCreate = async (e) => {
    e.preventDefault();
    try {
      await PollsService.createPoll(poll);
      setPoll({ title: '', description: '' });
      loadPolls();
    } catch (err) { alert('Creation failed'); }
  };

  const handleUpdateStatus = async (id, status) => {
    try {
      await PollsService.updatePollStatus(id, status);
      loadPolls();
    } catch (err) { alert('Status update failed'); }
  };

  return (
    <div className="container">
      <div className="card">
        <h2>Create New Poll</h2>
        <form onSubmit={handleCreate}>
          <input className="input-field" placeholder="Poll Title" value={poll.title} 
                 onChange={e => setPoll({...poll, title: e.target.value})} required />
          <textarea className="input-field" placeholder="Description" value={poll.description} 
                    onChange={e => setPoll({...poll, description: e.target.value})} />
          <button type="submit" className="btn-primary">Create</button>
        </form>
      </div>

      <div className="card mt-2">
        <h2>Manage Polls</h2>
        {myPolls.map(p => (
          <div key={p.id} className="poll-item">
            <span>{p.title} ({p.status})</span>
            <div>
              <button onClick={() => handleUpdateStatus(p.id, 'OPEN')}>Publish</button>
              <button onClick={() => handleUpdateStatus(p.id, 'CLOSED')}>Close</button>
              <button className="btn-logout" onClick={() => PollsService.deletePoll(p.id).then(loadPolls)}>Delete</button>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};

export default AdminDashboard;