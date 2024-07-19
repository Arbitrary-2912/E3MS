import React, { useState, useEffect, ChangeEvent } from 'react';
import { sha3_512 } from 'js-sha3';
import { verifyUsername } from './api/verifyusername';
import { getRecentMessages } from './api/getrecentmessages';
import './App.css';
import { Message, MetaData, MessageData } from './data/message.ts';
import {addMessage} from "./api/addmessage.ts";
import {Credentials, User} from "./data/user.ts";

function App() {
    const [username, setDisplayUsername] = useState<string>('');
    const [password, setDisplayPassword] = useState<string>('');
    const [currentUsername, setLocalUsername] = useState<string>('');
    const [currentPassword, setLocalPassword] = useState<string>('');
    const [message, setMessage] = useState<string>('');
    const [messages, setMessages] = useState<Message[]>([]);

    useEffect(() => {
        const fetchMessages = async () => {
            try {
                const credentials = new Credentials(
                    "User 1",
                    "password",
                    "a69f73cca23a9ac5c8b567dc185a756e97c982164fe25859e0d1dcc1475c80a615b2123af1f5f94c11e3e9402c3ac558f500199d95b6d3e301758586281dcd26",
                    "5c07fc5b11d0cd030b0651221488b23314bcef74b6039f701f85b1ca54cf6d39e62f220b0a43aeed3690b3636ccb9f6f452c506af8fb8a69eb1c690062238f87",
                    "8e5d550426c0d92cc57ff20e621477a472faeea3befd013d5f83285a9430d225cc3cc740bd748d9f57382fcb550e30fdf06f502216b59ac9c8ee16c5e38bc8d7",
                    [
                        "90006420fc3b6672d3225d0487e27a432c13ccfd249943ad39798f678873c185a804121609c764d156374d0e5035283b94f447c1f2505ea18f517b14fc37da9",
                        "493b73e9f18aa89b8acbc230516b0e693a0b693622361d9d84cadb82328405c7682b4b7f0cc318879994d4890823020439ed88ae81c75c23b4d3ec10ded904f3",
                        "f435ba3ef2bf43e694c8940fa315641c67f152c2ee2021f121af5e03f9860607f74e61e1451f9489c2ff59f87dc0e1c501566e2324355de32770ec52cc3bce47",
                        "ffe4d7127d5e222ac77ded78b503276294960867d5501eda748bbb741dbc238d1d68f5f4c76f38fdb03a491bd9ec8c1e20403440315ac5e8050946a00409a724",
                    ]
                );
                const response = await getRecentMessages(new User(credentials, "User 1", "User 1"));
                console.log("Received messages: ", response);
                setMessages(response); // Update the state with the received messages
            } catch (error) {
                console.error("Error fetching messages: ", error);
            }
        };

        fetchMessages();
        const intervalId = setInterval(fetchMessages, 500000); // Poll every 5 seconds

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
                Math.random().toString(36).substring(7),
                currentUsername,
                [],
                new Date().toLocaleString()
            ),
            new MessageData(message)
        );

        setMessages([...messages, newMessage]);
        setMessage('');
        addMessage(newMessage);
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