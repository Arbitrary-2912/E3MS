import { ChangeEvent } from 'react';
import { useNavigate } from 'react-router-dom';
import { verifyUsername } from './api/verifyusername';
import { addUser } from './api/adduser.ts';
import {Credentials, User} from './data/user.ts';
import { useUser } from './UserContext';

function SignIn() {
    const { username, password, setUsername, setPassword } = useUser();
    const navigate = useNavigate();

    const handleUsernameChange = (event: ChangeEvent<HTMLInputElement>) => {
        setUsername(event.target.value);
    };

    const handlePasswordChange = (event: ChangeEvent<HTMLInputElement>) => {
        setPassword(event.target.value);
    };

    const handleLogIn = async () => {
        const isValidUser = await verifyUsername(username, password);
        if (isValidUser) {
            const loggedInUser = User.retrieveUserKeys();
            if (loggedInUser) {
                navigate('/messages', { state: { user: loggedInUser } });
            }
        } else {
            alert('Invalid credentials');
        }
    };

    const handleSignUp = async () => {
        try {
            const newUser = await User.createUser(Math.random().toString(36).substring(7), username, password);
            const user = await addUser(newUser);
            if (user) {
                newUser.storeUserKeys();
                navigate('/messages', { state: { user: newUser } });
            } else {
                alert('Invalid credentials');
            }
        } catch (error) {
            alert('An error occurred during sign-up.');
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
            <button type="submit" className="button" onClick={handleLogIn}>Log In</button>
            <button type="submit" className="button" onClick={handleSignUp}>Sign Up</button>
        </div>
    );
}

export default SignIn;
