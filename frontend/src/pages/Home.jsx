import { useState, useEffect } from 'react';
import { PollsService } from '../api/services';
import { Link } from 'react-router-dom';

const Home = () => {
  const [polls, setPolls] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchPolls();
  }, []);

  const fetchPolls = async () => {
    try {
      const { data } = await PollsService.getAllPolls();
      setPolls(data);
    } catch (error) {
      console.error('Error loading the Polls', error);
    } finally {
      setLoading(false);
    }
  };

  if (loading) return <div className="loader">Loading Polls...</div>;

  return (
    <div className="home-page">
      <div className="page-header">
        <h1>New Polls</h1>
        <Link to="/admin" className="btn-primary">Create Poll (Admin Only)</Link>
      </div>

      <div className="polls-grid">
        {polls.length === 0 ? (
          <p>No active Polls.</p>
        ) : (
          polls.map(poll => (
            <div key={poll.id} className="card poll-card">
              <h3>{poll.title}</h3>
              <p>{poll.description}</p>
              <div className="poll-footer">
                <span className={`status ${poll.status?.toLowerCase()}`}>{poll.status}</span>
                <Link to={`/polls/${poll.id}`} className="btn-secondary">Participate</Link>
              </div>
            </div>
          ))
        )}
      </div>
    </div>
  );
};

export default Home;