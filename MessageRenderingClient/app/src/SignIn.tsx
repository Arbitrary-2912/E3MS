import { ChangeEvent } from 'react';
import { useNavigate } from 'react-router-dom';
import { verifyUsername } from './api/verifyusername';
import { addUser } from './api/adduser.ts';
import {Credentials, retrieveKeys, storeKeys, User} from './data/user.ts';
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
            const credentials = await retrieveKeys(username, password);
            if (credentials) {
                const user = new User(credentials, username, username);
                navigate('/messages');
            } else {
                alert('Failed to retrieve keys. Please try again.');
            }
            navigate('/messages');
        } else {
            alert('Invalid credentials');
        }
    };

    const handleSignUp = async () => {
        const userCredentials = new Credentials(
            username,
            password,
        );

        // TODO - add check to make sure user id is unique
        await storeKeys(username, password, userCredentials);
        const user = await addUser(new User(userCredentials, Math.random().toString(36).substring(7), username));

        console.log(user)
        // console.log('Decoded identity key:', base64ToUint8Array(userCredentials.identityKeyPair.publicKey));
        if (user) {
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
            <button type="submit" className="button" onClick={handleLogIn}>Log In</button>
            <button type="submit" className="button" onClick={handleSignUp}>Sign Up</button>
        </div>
    );
}

export default SignIn;