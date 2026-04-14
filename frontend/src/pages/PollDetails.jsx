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
    stompClient.current.debug = null; // Disable logging
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
          {poll.options.map(opt => (
            <div 
              key={opt.id} 
              className={`vote-option ${selectedOptions.includes(opt.id) ? 'selected' : ''}`}
              onClick={() => poll.status === 'OPEN' && handleOptionToggle(opt.id)}
            >
              <input 
                type={poll.multipleChoice ? "checkbox" : "radio"} 
                checked={selectedOptions.includes(opt.id)}
                readOnly
              />
              <div className="opt-text">
                <strong>{opt.name}</strong>
                <p>{opt.description}</p>
              </div>
            </div>
          ))}
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
        <h2>Participants ({participants.length})</h2>
        <div className="participants-list">
          {participants.length === 0 ? <p>No votes yet.</p> : participants.map(p => (
            <div key={p.id} className="participant-item">
              <span>{p.firstName} {p.lastName} {p.id === user.id && "(You)"}</span>
              {isAdminOfThisPoll && p.id !== user.id && (
                <button className="btn-small btn-danger" onClick={() => removeParticipant(p.id)}>Remove</button>
              )}
            </div>
          ))}
        </div>
      </div>
    </div>
  );
};

export default PollDetails;