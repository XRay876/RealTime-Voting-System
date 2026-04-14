import { BrowserRouter as Router, Routes, Route } from 'react-router-dom'
import Register from './components/Register'
import Login from "./components/Login"
import PollSelection from './components/PollSelection'
import VotingScreen from './components/VotingScreen'
import SuccessfulVotingScreen from './components/SuccessfulVotingScreen'
import './App.css'

function App() {
  return (
    <Router>
      <Routes>
        <Route path='/register' element={<Register />} />
        <Route path='/login' element={<Login />} />
        <Route path='/' element={<PollSelection />} />
        <Route path='/vote/:pollId' element={<VotingScreen />} />
        <Route path='/success' element={<SuccessfulVotingScreen />} />
      </Routes>
    </Router>
  )
}

export default App
