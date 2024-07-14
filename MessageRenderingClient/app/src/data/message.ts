export class Message {
    metaData: MetaData;
    messageData: MessageData;

    constructor(metaData: MetaData, messageData: MessageData) {
        this.metaData = metaData;
        this.messageData = messageData;
    }
}

class MetaData {
    id: string;
    sender: string;
    receiver: string[];
    timestamp: string;

    constructor(id: string, sender: string, receiver: string[], timestamp: string) {
        this.id = id;
        this.sender = sender;
        this.receiver = receiver;
        this.timestamp = timestamp;
    }
}

class MessageData {
    message: string;

    constructor(message: string) {
        this.message = message;
    }
}