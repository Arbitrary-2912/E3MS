import React, { useState, useEffect, ChangeEvent } from 'react';
import { getRecentMessages } from './api/getrecentmessages';
import { addMessage } from './api/addmessage';
import { Message, MetaData, MessageData } from './data/message';
import { Credentials, User } from './data/user';
import crypto from 'crypto';

function Messages() {
    const [message, setMessage] = useState<string>('');
    const [messages, setMessages] = useState<Message[]>([]);
    const [sharedKey, setSharedKey] = useState<Buffer | null>(null);


    useEffect(() => {
        const fetchMessages = async () => {
            try {
                const userCredentials = new Credentials(
                    "User 1",
                    "password",
                );
                console.log('password:', userCredentials.password)
                console.log('Identity Public Key:', userCredentials.identityKeyPair.publicKey);
                console.log('Identity Private Key:', userCredentials.identityKeyPair.secretKey);
                console.log('Ephemeral Public Key:', userCredentials.ephemeralKeyPair.publicKey);
                console.log('Ephemeral Private Key:', userCredentials.ephemeralKeyPair.secretKey);
                console.log('Signed Prekey Public Key:', userCredentials.signedPrekeyPair.publicKey);
                console.log('Signed Prekey Private Key:', userCredentials.signedPrekeyPair.secretKey);

                const response = await getRecentMessages(new User(userCredentials, "User 1", "User 1"));
                console.log("Received messages: ", response);
                setMessages(response);
            } catch (error) {
                console.error("Error fetching messages: ", error);
            }
        };

        fetchMessages();
        const intervalId = setInterval(fetchMessages, 5000);

        return () => clearInterval(intervalId);
    }, []);



    const handleMessageChange = (event: ChangeEvent<HTMLInputElement>) => {
        setMessage(event.target.value);
    };

    const handleSendMessage = () => {
        if (message.trim() === '') return;

        const newMessage = new Message(
            new MetaData(
                Math.random().toString(36).substring(7),
                "User 1",
                ["User 1"],
                new Date().toLocaleString()
            ),
            new MessageData(message)
        );

        setMessages([...messages, newMessage]);
        setMessage('');

        addMessage(newMessage);
    };

    return (
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
            <button className="button" onClick={handleSendMessage}>Refresh</button>
        </div>
    );
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
};

export default Messages;
