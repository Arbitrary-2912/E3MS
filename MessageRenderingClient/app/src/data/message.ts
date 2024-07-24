export class Message {
    metaData: MetaData;
    messageData: MessageData;

    constructor(metaData: MetaData, messageData: MessageData) {
        this.metaData = metaData;
        this.messageData = messageData;
    }
}

export class MetaData {
    id: string;
    sender: string;
    receiver: string[];
    timestamp: string;
    identityKey: string;
    signedPreKey: string;
    preKeySignature: string;
    oneTimePreKeys: string[];

    constructor(id: string, sender: string, receiver: string[], timestamp: string, identityKey: string, signedPreKey: string, preKeySignature: string, oneTimePreKeys: string[]) {
        this.id = id;
        this.sender = sender;
        this.receiver = receiver;
        this.timestamp = timestamp;
        this.identityKey = identityKey;
        this.signedPreKey = signedPreKey
        this.preKeySignature = preKeySignature;
        this.oneTimePreKeys = oneTimePreKeys;
    }
}

export class MessageData {
    message: string;

    constructor(message: string) {
        this.message = message;
    }
}