import { useState, useEffect, useRef } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import SockJS from 'sockjs-client';
import Stomp from 'stompjs';
import { PollsService, UserService } from '../api/services';
import { useAuth } from '../context/AuthContext';
import PollChart from '../components/PollChart';

const PollDetails = () => {
  const { id } = useParams();
  const { user } = useAuth();
  const navigate = useNavigate();
  
  const [poll, setPoll] = useState(null);
  const [results, setResults] = useState(null);
  const [myVote, setMyVote] = useState([]);
  const [selectedOptions, setSelectedOptions] = useState([]);
  const [participants, setParticipants] = useState([]);
  
  const [expandedOptions, setExpandedOptions] = useState({});
  
  const stompClient = useRef(null);

  useEffect(() => {
    loadData();
    connectWebSocket();
    return () => disconnectWebSocket();
  }, [id]);

  const loadData = async () => {
    try {
      const [pRes, rRes] = await Promise.all([
        PollsService.getPollById(id),
        PollsService.getResults(id)
      ]);
      setPoll(pRes.data);
      setResults(rRes.data);

      if (rRes.data.participantIds) {
        loadParticipants(rRes.data.participantIds);
      }

      const vRes = await PollsService.getMyVote(id).catch(() => null);
      if (vRes) {
        setMyVote(vRes.data.optionIds);
        setSelectedOptions(vRes.data.optionIds);
      }
    } catch (err) {
      console.error("Failed to load poll data", err);
    }
  };

  const loadParticipants = async (ids) => {
    if (!ids || ids.length === 0) {
        setParticipants([]);
        return;
    }
    try {
        const users = await Promise.all(
        ids.map(uid => 
            UserService.getUserById(uid)
            .then(r => r.data)
            .catch(err => {
                console.warn(`Could not fetch user ${uid}`, err);
                return null;
            })
        )
        );
        setParticipants(users.filter(u => u !== null));
    } catch (err) {
        console.error("Error loading participants", err);
    }
  };

  const connectWebSocket = () => {
    const socket = new SockJS('/ws-voting');
    stompClient.current = Stomp.over(socket);
    stompClient.current.debug = null; 
    stompClient.current.connect({}, () => {
      stompClient.current.subscribe(`/topic/poll/${id}/results`, (message) => {
        const newResults = JSON.parse(message.body);
        setResults(newResults);
        if (newResults.participantIds) loadParticipants(newResults.participantIds);
      });
    });
  };

  const disconnectWebSocket = () => {
    if (stompClient.current) stompClient.current.disconnect();
  };

  const handleOptionToggle = (optionId) => {
    if (poll.multipleChoice) {
      setSelectedOptions(prev => 
        prev.includes(optionId) ? prev.filter(i => i !== optionId) : [...prev, optionId]
      );
    } else {
      setSelectedOptions([optionId]);
    }
  };

  const toggleOptionVoters = (optionId) => {
    setExpandedOptions(prev => ({
      ...prev,
      [optionId]: !prev[optionId]
    }));
  };

  const castVote = async () => {
    try {
      await PollsService.vote(id, selectedOptions);
      setMyVote(selectedOptions);
      alert("Vote submitted!");
    } catch (err) {
      alert(err.response?.data?.message || "Voting failed");
    }
  };

  const cancelVote = async () => {
    try {
      await PollsService.cancelVote(id);
      setMyVote([]);
      setSelectedOptions([]);
      alert("Vote cancelled");
    } catch (err) {
      alert("Cancellation failed");
    }
  };

  const removeParticipant = async (userId) => {
    if (window.confirm("Remove this user's vote?")) {
      await PollsService.removeParticipant(id, userId);
    }
  };

  if (!poll || !results) return <div className="loader">Loading...</div>;

  const isAdminOfThisPoll = user.id === poll.createdBy;

  return (
    <div className="poll-details-container">
      <div className="card">
        <h1>{poll.title}</h1>
        <p className="description">{poll.description}</p>
        <span className={`badge status-${poll.status.toLowerCase()}`}>{poll.status}</span>
        
        <div className="options-section mt-2">
          <h3>Choose your option(s):</h3>
          {poll.options.map(opt => {
            const optionResults = results?.results?.find(r => r.candidateId === opt.id);
            const voterIds = optionResults?.voterIds || [];
            const optionVoters = participants.filter(p => voterIds.includes(p.id));
            const isExpanded = expandedOptions[opt.id];

            return (
              <div key={opt.id} style={{ marginBottom: '15px' }}>
                <div 
                  className={`vote-option ${selectedOptions.includes(opt.id) ? 'selected' : ''}`}
                  onClick={() => poll.status === 'OPEN' && handleOptionToggle(opt.id)}
                  style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}
                >
                  <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
                    <input 
                      type={poll.multipleChoice ? "checkbox" : "radio"} 
                      checked={selectedOptions.includes(opt.id)}
                      readOnly
                    />
                    <div className="opt-text">
                      <strong>{opt.name}</strong>
                      <p style={{ margin: 0 }}>{opt.description}</p>
                    </div>
                  </div>
                  
                  <button 
                    type="button"
                    className="btn-small" 
                    onClick={(e) => { e.stopPropagation(); toggleOptionVoters(opt.id); }}
                    style={{ background: 'transparent', color: 'inherit', border: '1px solid #ccc' }}
                  >
                    {voterIds.length} {isExpanded ? '▲' : '▼'}
                  </button>
                </div>

                {isExpanded && (
                  <div style={{ padding: '10px 15px', backgroundColor: '#f5f5f5', borderRadius: '4px', marginTop: '5px', fontSize: '0.9em' }}>
                    {optionVoters.length > 0 ? (
                      optionVoters.map(voter => (
                        <div key={voter.id} style={{ padding: '2px 0' }}>
                          • {voter.firstName} {voter.lastName} {voter.id === user.id && <strong style={{color: '#4caf50'}}>(Вы)</strong>}
                        </div>
                      ))
                    ) : (
                      <div style={{ color: '#888' }}>No votes</div>
                    )}
                  </div>
                )}
              </div>
            );
          })}
        </div>

        {poll.status === 'OPEN' && (
          <div className="actions mt-2">
            <button className="btn-primary" onClick={castVote} disabled={selectedOptions.length === 0}>
              {myVote.length > 0 ? "Change Vote" : "Vote"}
            </button>
            {myVote.length > 0 && (
              <button className="btn-logout ml-1" onClick={cancelVote}>Cancel My Vote</button>
            )}
          </div>
        )}
      </div>

      <div className="card mt-2">
        <h2>Live Results</h2>
        <PollChart results={results.results} totalVotes={results.totalVotes} />
      </div>

      <div className="card mt-2">
        <h2>All Participants ({participants.length})</h2>
        <div className="participants-list">
          {participants.length === 0 ? <p>No votes yet.</p> : participants.map(p => (
            <div key={p.id} className="participant-item" style={{ display: 'flex', justifyContent: 'space-between', padding: '10px', borderBottom: '1px solid #eee' }}>
              <span>{p.firstName} {p.lastName} {p.id === user.id && <strong style={{color: '#4caf50'}}>(Вы)</strong>}</span>
              
              {isAdminOfThisPoll && (
                <button className="btn-small btn-danger" onClick={() => removeParticipant(p.id)}>
                  Remove Vote
                </button>
              )}
            </div>
          ))}
        </div>
      </div>
    </div>
  );
};

export default PollDetails;