import { useNavigate } from "react-router-dom"
import { logout } from "../utils/auth"
import "../App.css"

export default function SuccessfulVotingScreen() {
    const navigate = useNavigate()
    return (
        <div className="container">
            <div className="question-section">
                <h2>Vote Submitted Successfully</h2>
                <button className="vote-button" onClick={() => navigate("/")}>
                    Vote in Another Poll
                </button>
                <button
                    className="vote-button"
                    onClick={() => {
                        logout()
                        navigate("/login")
                    }}
                >
                    Logout
                </button>
            </div>
        </div>
    )
}