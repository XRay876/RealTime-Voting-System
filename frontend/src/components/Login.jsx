import { useState } from "react"
import { useNavigate } from "react-router-dom"
import API from "../API/API"
import { setAuth } from "../utils/auth"
import "../App.css"

export default function Login() {
    const [email, setEmail] = useState("")
    const [password, setPassword] = useState("")
    const navigate = useNavigate()
    const handleLogin = async (e) => {
        e.preventDefault()
        try {
            const res = await API.post(
                "/v1/auth/login",
                { email, password }
            )
            setAuth(res.data)
            navigate("/")
        } catch (err) {
            console.error(err)
            alert("Login failed.")
        }
    }
    return (
        <div className="container">
            <form onSubmit={handleLogin} className="question-section">
                <h2>Login</h2>
                <input
                    type="email"
                    placeholder="email"
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                />
                <input
                    type="password"
                    placeholder="password"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                />
                <button className="vote-button" type="submit">Login</button>
            </form>
        </div>
    )
}