import { useState, useEffect } from "react"
import { useNavigate } from "react-router-dom"
import API from "../API/API"
import { getToken } from "../utils/auth"
import "../App.css"

export default function PollSelection() {
    const navigate = useNavigate()
    const [polls, setPolls] = useState([])
    const [selected, setSelected] = useState(null)
    useEffect(() => {
        if (!token) {
            navigate("/login")
        }
        API.get("/polls")
            .then((res) => setPolls(res.data.filter(poll => poll.status === "OPEN")))
            .catch((err) => console.error("Error fetching polls:", err))
    }, [])
    const handleSubmit = (e) => {
        e.preventDefault()
        if (!selected) {
            alert("Please select an option.")
            return
        }
        navigate(`/vote/${selected}`)
    }
    return (
        <div className="container">
            <div className="question-section">
                <h2>Select a Poll</h2>
                {polls.map((poll) => (
                    <div
                        key={poll.id}
                        className={`option ${selected === poll.id ? "selected" : ""}`}
                        onClick={() => setSelected(poll.id)}
                    >
                        <input
                            type="radio"
                            checked={selected === poll.id}
                            readOnly
                        />
                        {poll.title}
                        <br />
                    </div>
                ))}
                <button className="vote-button" onClick={handleSubmit}>Continue</button>
            </div>
        </div>
    )
}