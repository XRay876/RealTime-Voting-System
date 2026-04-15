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
  const [notification, setNotification] = useState(null);

  const stompClient = useRef(null);

  useEffect(() => {
    loadData();
    connectWebSocket();
    return () => disconnectWebSocket();
  }, [id]);

  const showNotification = (message, type = 'success') => {
    setNotification({ message, type });
    setTimeout(() => setNotification(null), 3000);
  };

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
      showNotification("Failed to load poll data", "error");
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
            .catch(err => null)
        )
        );
        setParticipants(users.filter(u => u !== null));
    } catch (err) {
        console.error(err);
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
      showNotification("Vote submitted successfully!");
    } catch (err) {
      showNotification(err.response?.data?.message || "Voting failed", "error");
    }
  };

  const cancelVote = async () => {
    try {
      await PollsService.cancelVote(id);
      setMyVote([]);
      setSelectedOptions([]);
      showNotification("Vote cancelled", "info");
    } catch (err) {
      showNotification("Cancellation failed", "error");
    }
  };

  const removeParticipant = async (userId) => {
    if (window.confirm("Remove this user's vote?")) {
      await PollsService.removeParticipant(id, userId);
      showNotification("User vote removed", "info");
    }
  };

  if (!poll || !results) return <div className="loader-container"><div className="spinner"></div></div>;

  const isAdminOfThisPoll = user.id === poll.createdBy;

  return (
    <div className="poll-detail-wrapper">
      {notification && (
        <div className={`notification-toast ${notification.type}`}>
          {notification.message}
        </div>
      )}

      <div className="poll-content-layout">
        <div className="poll-main-card">
          <div className="poll-header-block">
            <span className={`status-pill ${poll.status.toLowerCase()}`}>{poll.status}</span>
            <h1>{poll.title}</h1>
            <p>{poll.description}</p>
          </div>

          <div className="voting-area">
            <h3>Choose your option(s):</h3>
            <div className="options-list-detailed">
              {poll.options.map(opt => {
                const optionResults = results?.results?.find(r => r.candidateId === opt.id);
                const voterIds = optionResults?.voterIds || [];
                const optionVoters = participants.filter(p => voterIds.includes(p.id));
                const isExpanded = expandedOptions[opt.id];
                const isSelected = selectedOptions.includes(opt.id);

                return (
                  <div key={opt.id} className="option-item-container">
                    <div 
                      className={`modern-vote-option ${isSelected ? 'is-selected' : ''}`}
                      onClick={() => poll.status === 'OPEN' && handleOptionToggle(opt.id)}
                    >
                      <div className="option-main">
                        <div className={`custom-checkbox ${poll.multipleChoice ? 'square' : 'circle'} ${isSelected ? 'checked' : ''}`}></div>
                        <div className="option-info">
                          <span className="option-name">{opt.name}</span>
                          <span className="option-desc">{opt.description}</span>
                        </div>
                      </div>
                      
                      <button 
                        type="button"
                        className="voter-count-toggle" 
                        onClick={(e) => { e.stopPropagation(); toggleOptionVoters(opt.id); }}
                      >
                        {voterIds.length} votes {isExpanded ? '▲' : '▼'}
                      </button>
                    </div>

                    {isExpanded && (
                      <div className="voter-reveal-panel">
                        {optionVoters.length > 0 ? (
                          optionVoters.map(voter => (
                            <div key={voter.id} className="voter-row">
                              <div className="voter-avatar">{voter.firstName[0]}</div>
                              <span>{voter.firstName} {voter.lastName} {voter.id === user.id && <b className="you-tag">(You)</b>}</span>
                            </div>
                          ))
                        ) : (
                          <div className="no-votes">No votes recorded for this choice</div>
                        )}
                      </div>
                    )}
                  </div>
                );
              })}
            </div>

            {poll.status === 'OPEN' && (
              <div className="poll-action-footer">
                <button className="btn-vote-submit" onClick={castVote} disabled={selectedOptions.length === 0}>
                  {myVote.length > 0 ? "Change My Vote" : "Confirm Vote"}
                </button>
                {myVote.length > 0 && (
                  <button className="btn-vote-cancel" onClick={cancelVote}>Withdraw My Vote</button>
                )}
              </div>
            )}
          </div>
        </div>

        <aside className="poll-stats-sidebar">
          <div className="sidebar-card">
            <h2>Live Results</h2>
            <PollChart results={results.results} totalVotes={results.totalVotes} />
          </div>

          <div className="sidebar-card">
            <h2>Participants ({participants.length})</h2>
            <div className="participants-scroll">
              {participants.length === 0 ? (
                <p className="empty-text">No one has voted yet</p>
              ) : (
                participants.map(p => (
                  <div key={p.id} className="participant-sidebar-item">
                    <span>{p.firstName} {p.lastName} {p.id === user.id && <b className="you-tag">(You)</b>}</span>
                    {isAdminOfThisPoll && (
                      <button className="btn-kick-user" onClick={() => removeParticipant(p.id)}>Remove</button>
                    )}
                  </div>
                ))
              )}
            </div>
          </div>
        </aside>
      </div>
    </div>
  );
};

export default PollDetails;