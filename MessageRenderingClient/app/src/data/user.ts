export class User {
    credentials: Credentials;
    id: string;
    name: string;

    constructor(credentials: Credentials, id: string, name: string) {
        this.credentials = credentials;
        this.id = id;
        this.name = name;
    }
}

export class Credentials {
    username: string;
    password: string;
    identityKey: string;
    signedPreKey: string;
    preKeySignature: string;
    oneTimePreKeys: string[];

    constructor(username: string, password: string, identityKey: string, signedPreKey: string, preKeySignature: string, oneTimePreKeys: string[]) {
        this.username = username;
        this.password = password;
        this.identityKey = identityKey;
        this.signedPreKey = signedPreKey;
        this.preKeySignature = preKeySignature;
        this.oneTimePreKeys = oneTimePreKeys;
    }
}