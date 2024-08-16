import React, { useState, useEffect, ChangeEvent } from 'react';
import { getRecentMessages } from './api/getrecentmessages';
import { addMessage } from './api/addmessage';
import { Message, MetaData, MessageData } from './data/message';
import { User } from './data/user';
import { useLocation } from "react-router-dom";
import { getUsers } from "./api/getusers.ts";
import { getPublicCredentials } from "./api/getpubliccredentials.ts";

function Messages() {
    const [message, setMessage] = useState<string>('');
    const [messages, setMessages] = useState<Message[]>([]);
    const [recipient, setRecipient] = useState<string>('');
    const [validRecipient, setValidRecipient] = useState<boolean>(true);
    const location = useLocation();
    const pulledUser: User = location.state?.user;
    const user = new User(pulledUser.id, pulledUser.name, pulledUser.credentials);


    useEffect(() => {
        const fetchMessages = async () => {
            try {
                const response = await getRecentMessages(user.id);
                if (response && Array.isArray(response)) {
                    const decryptedMessages = await Promise.all(response.map(async (msg) => {
                        try {
                            if (msg.metaData.sender !== user.id) {
                                const senderPublicKey = await getPublicCredentials(msg.metaData.sender);
                                const decryptedText = await user.decryptMessage(msg.messageData.cipherText, msg.messageData.iv, senderPublicKey);
                                return new Message(
                                    msg.metaData,
                                    new MessageData(msg.messageData.cipherText, msg.messageData.iv, decryptedText)
                                );
                            } else {
                                const recipientPublicKey = await getPublicCredentials(msg.metaData.receiver[0]);
                                const reversedText = await user.reverseEncryption(msg.messageData.cipherText, msg.messageData.iv, recipientPublicKey);
                                return new Message(
                                    msg.metaData,
                                    new MessageData(msg.messageData.cipherText, msg.messageData.iv, reversedText)
                                );
                            }
                        } catch (messageError) {
                            console.error("Error decrypting message:", messageError);
                            throw messageError;
                        }
                    }));
                    setMessages(decryptedMessages);
                } else {
                    console.error("Response is not an array:", response);
                    setMessages([]);
                }
            } catch (error) {
                console.error("Error fetching messages:", error);
                setMessages([]);
            }
        };

        fetchMessages();
        const intervalId = setInterval(fetchMessages, 50);

        return () => clearInterval(intervalId);
    }, [user.name]);


    const handleMessageChange = (event: ChangeEvent<HTMLInputElement>) => {
        setMessage(event.target.value);
    };

    const handleRecipientChange = async (event: ChangeEvent<HTMLInputElement>) => {
        setValidRecipient(true);
        const inputRecipient = event.target.value;
        setRecipient(inputRecipient);
    };

    const handleSendMessage = async () => {
        if (message.trim() === '' || recipient.trim() === '') return;

        const users = await getUsers();
        const targetRecipient = users.get(recipient);

        if (!targetRecipient) return;

        setValidRecipient(true);

        try {
            const recipientPublicKey = await getPublicCredentials(targetRecipient);
            if (!recipientPublicKey) {
                console.error("Recipient's public key is not available.");
                return;
            }
            const encryptedMessage = await user.encryptMessage(message, recipientPublicKey);

            const recipients = users.get(recipient);
            if (recipients) {
                const id = Math.random().toString(36).substring(10);
                const newMessage = new Message(
                    new MetaData(
                        id,
                        user.name,
                        user.id,
                        [recipients],
                        new Date().toLocaleString()
                    ),
                    new MessageData(encryptedMessage.ciphertext, encryptedMessage.iv)
                );

                await addMessage(newMessage);

                const localMessage = new Message(
                    new MetaData(
                        id,
                        user.name,
                        user.id,
                        [recipients, user.id],
                        new Date().toLocaleString()
                    ),
                    new MessageData("", "", message)
                );

                setMessages([...messages, localMessage]);
                setMessage('');
            }
        } catch (error) {
            alert('An error occurred while sending the message');
            console.error("Failed to send the message:", error);
        }
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
                placeholder="Enter recipient"
                className="input"
                value={recipient}
                onChange={handleRecipientChange}
                style={{ borderColor: validRecipient ? 'initial' : 'red' }}
            />
            {!validRecipient && <span className="error-text">Recipient not found</span>}
            <input
                type="text"
                placeholder="Enter your message"
                className="input"
                value={message}
                onChange={handleMessageChange}
            />
            <button className="button" onClick={handleSendMessage} disabled={!validRecipient}>Send</button>
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
            <div className="message-header">
                <span className="message-sender">{message.metaData.username}</span>
                <span className="message-timestamp">{message.metaData.timestamp}</span>
            </div>
            <div className="message-content">
                <span className="message-text">{message.messageData.decryptedMessage}</span>
            </div>
        </div>
    );
};

export default Messages;
