import React, { useState, useEffect, ChangeEvent } from 'react';
import { sha3_512 } from 'js-sha3';
import { verifyUsername } from './api/verifyusername';
import { getRecentMessages } from './api/getrecentmessages';
import './App.css';
import { Message, MetaData, MessageData } from './data/message.ts';

function App() {
    const [username, setDisplayUsername] = useState<string>('');
    const [password, setDisplayPassword] = useState<string>('');
    const [currentUsername, setLocalUsername] = useState<string>('');
    const [currentPassword, setLocalPassword] = useState<string>('');
    const [message, setMessage] = useState<string>('');
    const [messages, setMessages] = useState<Message[]>([]);

    useEffect(() => {
        const fetchMessages = async () => {
            const response = await getRecentMessages();
            console.log("response: ", response)
            const jsonResponse = JSON.parse(response);
            console.log(jsonResponse.response);
        }



        fetchMessages();

        const intervalId = setInterval(fetchMessages, 5000); // Poll every 5 seconds

        return () => clearInterval(intervalId); // Cleanup interval on component unmount
    }, []);



    const handleUsernameChange = (event: ChangeEvent<HTMLInputElement>) => {
        setDisplayUsername(event.target.value);
    }

    const handlePasswordChange = (event: ChangeEvent<HTMLInputElement>) => {
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

    const handleMessageChange = (event: ChangeEvent<HTMLInputElement>) => {
        setMessage(event.target.value);
    }

    const handleSendMessage = () => {
        if (message.trim() === '') return;

        const newMessage = new Message(
            new MetaData(
                Math.random().toString(36).substr(2, 9), // generate a random id
                currentUsername,
                [],
                new Date().toLocaleString()
            ),
            new MessageData(message)
        );

        setMessages([...messages, newMessage]);
        setMessage('');
    };

    return (
        <div className="app">
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
                <p>Username: {currentUsername}</p>
                <p>Password: {currentPassword}</p>
                <p>Hash: {sha3_512(currentPassword)}</p>
            </div>
            <div className="chat-section">
                <div className="message-list">
                    {messages.map((msg, index) => (
                        <MessageComponent key={index} message={msg} />
                    ))}
                </div>
                <input
                    type="text"
                    placeholder="Enter your message"
                    className="input"
                    value={message}
                    onChange={handleMessageChange}
                />
                <button className="button" onClick={handleSendMessage}>Send</button>
            </div>
        </div>
    )
}

interface MessageComponentProps {
    message: Message;
}

const MessageComponent: React.FC<MessageComponentProps> = ({ message }) => {
    return (
        <div className="message">
            <span className="message-text">{message.messageData.message}</span>
            <span className="message-timestamp">{message.metaData.timestamp}</span>
        </div>
    );
}

export default App;
