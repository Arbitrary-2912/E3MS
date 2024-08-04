import React, { useState, useEffect, ChangeEvent } from 'react';
import { getRecentMessages } from './api/getrecentmessages';
import { addMessage } from './api/addmessage';
import { Message, MetaData, MessageData } from './data/message';
import { getSharedKey, User } from './data/user';

function Messages() {
    const [message, setMessage] = useState<string>('');
    const [messages, setMessages] = useState<Message[]>([]);
    const [sharedKey, setSharedKey] = useState<Buffer | null>(null);

    useEffect(() => {
        const fetchMessages = async () => {
            try {
                const response = await getRecentMessages("User 1");
                console.log("Received messages: ", response);
                setMessages(response || []);  // Ensure response is an array
            } catch (error) {
                console.error("Error fetching messages: ", error);
                setMessages([]);  // Fallback to empty array in case of error
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
                Math.random().toString(36).substring(10),
                "User 1",
                ["User 1"],
                new Date().toLocaleString()
            ),
            new MessageData(message)
        );

        setMessages([...messages, newMessage]);  // Ensure messages is an array
        setMessage('');

        addMessage(newMessage);



        const user1 = new User("test", "test", "test");
        const user2 = new User("test1", "test1", "test1");

        const sharedKey = getSharedKey(user1, user2);
        console.log(sharedKey);

        const sharedKey2 = getSharedKey(user2, user1);
        console.log(sharedKey2);
    };

    return (
        <div className="chat-section">
            <div className="message-list">
                {messages && messages.map((msg, index) => (
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
            <button className="button" onClick={() => window.location.reload()}>Refresh</button>
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
