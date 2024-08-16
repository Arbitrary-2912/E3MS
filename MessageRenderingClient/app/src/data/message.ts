export class Message {
    metaData: MetaData;
    messageData: MessageData;

    constructor(metaData: MetaData, messageData: MessageData) {
        this.metaData = metaData;
        this.messageData = messageData;
    }

    // Method to return an object suitable for sending to the server
    toServerObject(): { metaData: MetaData, messageData: { ciphertext: string, iv: string } } {
        return {
            metaData: this.metaData,
            messageData: {
                ciphertext: this.messageData.cipherText,
                iv: this.messageData.iv
            }
        };
    }
}

export class MetaData {
    id: string;
    username: string
    sender: string;
    receiver: string[];
    timestamp: string;

    constructor(id: string, username: string, sender: string, receiver: string[], timestamp: string) {
        this.id = id;
        this.username = username;
        this.sender = sender;
        this.receiver = receiver;
        this.timestamp = timestamp;
    }
}

export class MessageData {
    cipherText: string;
    iv: string;
    decryptedMessage?: string; // Optional property for storing the decrypted message

    constructor(cipherText: string, iv: string, decryptedMessage?: string) {
        this.cipherText = cipherText;
        this.iv = iv;
        this.decryptedMessage = decryptedMessage;
    }
}