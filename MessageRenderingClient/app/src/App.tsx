import {useState} from 'react';
import {sha3_512} from "js-sha3";
import {verifyUsername} from "./api/verifyusername.ts";
import './App.css'

function App() {
    const [username, setDisplayUsername] = useState('');
    const [password, setDisplayPassword] = useState('');
    const [currentUsername, setLocalUsername] = useState('');
    const [currentPassword, setLocalPassword] = useState('');
    const handleUsernameChange = (event: React.ChangeEvent<HTMLInputElement>) => {
        setDisplayUsername(event.target.value);
    }

    const handlePasswordChange = (event: React.ChangeEvent<HTMLInputElement>) => {
        setDisplayPassword(event.target.value);
    }

    const handleSubmit = () => {
        setLocalUsername(username);
        setLocalPassword(password);

        setDisplayUsername('');
        setDisplayPassword('');

        console.log("Username: " + username + " Password: " + password)
        verifyUsername(username, password);
    };

    return (
        <div>
            <input
                type="text"
                placeholder="Enter username"
                className="message-input"
                value={username}
                onChange={handleUsernameChange}
            />
            <input
                type="password"
                placeholder="Enter password"
                className="message-input"
                value={password}
                onChange={handlePasswordChange}
            />
            <button type="submit" className="submit-button" onClick={handleSubmit}>Submit</button>
            <p>Username: {currentUsername}</p>
            <p>Password: {currentPassword}</p>
            <p>Hash: {sha3_512(currentPassword)}</p>
        </div>
    )
}

export default App


