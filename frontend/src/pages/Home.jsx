import { useState, useEffect } from 'react';
import { PollsService } from '../api/services';
import { Link } from 'react-router-dom';

const Home = () => {
  const [polls, setPolls] = useState([]);
  const [loading, setLoading] = useState(true);
  const [activeFilter, setActiveFilter] = useState('ALL');

  useEffect(() => {
    fetchPolls();
  }, []);

  const fetchPolls = async () => {
    try {
      const { data } = await PollsService.getAllPolls();
      setPolls(data.filter(p => p.status !== 'DRAFT'));
    } catch (error) {
      console.error('Error loading the Polls', error);
    } finally {
      setLoading(false);
    }
  };

  const filteredPolls = polls.filter(poll => {
    if (activeFilter === 'ALL') return true;
    return poll.status === activeFilter;
  });

  if (loading) {
    return (
      <div className="loading-state">
        <div className="spinner"></div>
        <p>Fetching latest polls...</p>
      </div>
    );
  }

  return (
    <div className="home-container">
      <section className="home-hero">
        <div className="hero-content">
          <h1>Active Communities, Real Voices</h1>
          <p>Explore trending polls or create your own to gather insights from the community.</p>
          <div className="hero-actions">
            <Link to="/admin" className="btn-hero-primary">Create a Poll</Link>
          </div>
        </div>
      </section>

      <section className="polls-section">
        <div className="section-header">
          <div className="header-left">
            <h2>Trending Polls</h2>
            <div className="filter-hint">Showing {filteredPolls.length} discussions</div>
          </div>
          
          <div className="filter-controls">
            <button 
              className={`filter-btn ${activeFilter === 'ALL' ? 'active' : ''}`}
              onClick={() => setActiveFilter('ALL')}
            >
              All
            </button>
            <button 
              className={`filter-btn ${activeFilter === 'OPEN' ? 'active' : ''}`}
              onClick={() => setActiveFilter('OPEN')}
            >
              Open
            </button>
            <button 
              className={`filter-btn ${activeFilter === 'CLOSED' ? 'active' : ''}`}
              onClick={() => setActiveFilter('CLOSED')}
            >
              Closed
            </button>
          </div>
        </div>

        <div className="polls-grid">
          {filteredPolls.length === 0 ? (
            <div className="empty-state">
              <p>No {activeFilter.toLowerCase()} polls available at the moment.</p>
            </div>
          ) : (
            filteredPolls.map(poll => (
              <div key={poll.id} className="poll-preview-card">
                <div className="card-top">
                  <span className={`status-dot ${poll.status?.toLowerCase()}`}></span>
                  <span className="status-text">{poll.status}</span>
                </div>
                
                <div className="card-body">
                  <h3>{poll.title}</h3>
                  <p>{poll.description || "No description provided for this poll."}</p>
                </div>

                <div className="card-footer">
                  <Link to={`/polls/${poll.id}`} className="btn-participate">
                    {poll.status === 'OPEN' ? 'Join Vote' : 'View Results'}
                    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                      <path d="M5 12h14M12 5l7 7-7 7"/>
                    </svg>
                  </Link>
                </div>
              </div>
            ))
          )}
        </div>
      </section>
    </div>
  );
};

export default Home;