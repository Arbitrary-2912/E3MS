import { useState } from 'react';
import {sha3_512} from "js-sha3";
import axios from 'axios';
import './App.css'

function App() {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [currentUsername, setCurrentUsername] = useState('');
    const [currentPassword, setCurrentPassword] = useState('');
    const handleUsernameChange = (event: React.ChangeEvent<HTMLInputElement>) => {
        setUsername(event.target.value);
    }

    const handlePasswordChange = (event: React.ChangeEvent<HTMLInputElement>) => {
        setPassword(event.target.value);
    }

    const handleSubmit = () => {
        setCurrentUsername(username);
        setCurrentPassword(password);

        setUsername('');
        setPassword('');

        console.log("Username: " + username + " Password: " + password)
        axios.post('http://localhost:8080/', {
            command: "verifyPassword",
            userId: "username",
            password: "password"
        }).then((response) => {
            console.log(response.data);
        }).catch((error) => {
            console.error(error);
        });
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


