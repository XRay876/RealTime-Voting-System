import { useState, useEffect } from "react"
import { useParams, useNavigate } from "react-router-dom"
import API from "../API/API"
import "../App.css"

export default function VotingScreen() {
    const { pollId } = useParams()
    const navigate = useNavigate()
    const [poll, setPoll] = useState(null)
    const [selected, setSelected] = useState(null)
    useEffect(() => {
        API.get(`/polls/${pollId}`)
            .then((res) => setPoll(res.data))
            .catch((err) => console.error(err))
    }, [pollId])
    const handleVote = (e) => {
        e.preventDefault()
        if (!selected) {
            alert("Please select one of the candidates.")
            return
        }
        API.post("/votes", {
            pollId,
            candidateId: selected
        })
        .then(() => navigate("/success"))
        .catch(() => alert("Voting failed"))
    }
    if (!poll) return <p>Loading...</p>
    return (
        <div className="container">
            <div className="question-section">
                <div className="question-title">{poll.title}</div>
                    {poll.candidates.map((candidate) => (
                        <div
                            key={candidate.id}
                            className={`option ${selected === candidate.id ? "selected" : ""}`}
                            onClick={() => setSelected(candidate.id)}
                        >
                                <input
                                    type="radio"
                                    checked={selected == candidate.id}
                                    readOnly
                                />
                                {candidate.name}
                                <br />
                        </div>
                    ))}
                    <button className="vote-button" onClick={handleVote}>Vote</button>
            </div>
        </div>
    )
}