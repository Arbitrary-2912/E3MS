import { useState, ChangeEvent } from 'react';
import { useNavigate } from 'react-router-dom';
import { verifyUsername } from './api/verifyusername';

function SignIn() {
    const [username, setUsername] = useState<string>('');
    const [password, setPassword] = useState<string>('');
    const navigate = useNavigate();

    const handleUsernameChange = (event: ChangeEvent<HTMLInputElement>) => {
        setUsername(event.target.value);
    };

    const handlePasswordChange = (event: ChangeEvent<HTMLInputElement>) => {
        setPassword(event.target.value);
    };

    const handleSubmit = async () => {
        const isValidUser = await verifyUsername(username, password);
        if (isValidUser) {
            navigate('/messages');
        } else {
            alert('Invalid credentials');
        }
    };

    return (
        <div className="login-section">
            <input
                type="text"
                placeholder="Enter username"
                className="input"
                value={username}
                onChange={handleUsernameChange}
            />
            <input
                type="password"
                placeholder="Enter password"
                className="input"
                value={password}
                onChange={handlePasswordChange}
            />
            <button type="submit" className="button" onClick={handleSubmit}>Submit</button>
        </div>
    );
}

export default SignIn;
