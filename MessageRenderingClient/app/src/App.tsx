import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';
import SignIn from './SignIn';
import Messages from './Messages';
import './App.css';

function App() {
    return (
        <Router>
            <Routes>
                <Route path="/" element={<SignIn />} />
                <Route path="/messages" element={<Messages />} />
            </Routes>
        </Router>
    );
}

export default App;
