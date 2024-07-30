import nacl from 'tweetnacl';

export class User {
    credentials: Credentials;
    id: string;
    name: string;

    constructor(credentials: Credentials, id: string, name: string) {
        this.credentials = credentials;
        this.id = id;
        this.name = name;
    }

    public getPublicInfo() {
        return {
            credentials: {
                username: this.name,
                password: this.credentials.password,
                identityKey: this.credentials.identityKeyPair.publicKey,
                ephemeralKey: this.credentials.ephemeralKeyPair.publicKey,
                signedPreKey: this.credentials.signedPreKeyPair.publicKey,
                oneTimePreKeys: this.credentials.oneTimePreKeyPairs.map(keyPair => (keyPair.publicKey))
            },
            id: this.id,
            name: this.name,
        };
    }
}

export class Credentials {
    username: string;
    password: string;
    identityKeyPair: { publicKey: string; secretKey: string };
    ephemeralKeyPair: { publicKey: string; secretKey: string };
    signedPreKeyPair: { publicKey: string; secretKey: string };
    oneTimePreKeyPairs: { publicKey: string; secretKey: string }[];

    constructor(username: string, password: string) {
        this.username = username;

        this.password = uint8ArrayToBase64(nacl.hash(new TextEncoder().encode(password)));

        this.identityKeyPair = this.generateKeyPair();

        this.ephemeralKeyPair = this.generateKeyPair();

        this.signedPreKeyPair = this.generateKeyPair();

        this.oneTimePreKeyPairs = [];
        for (let i = 0; i < 4; i++) {
            this.oneTimePreKeyPairs.push(this.generateKeyPair());
        }
    }

    private generateKeyPair(): { publicKey: string; secretKey: string } {
        const keyPair = nacl.box.keyPair();
        return {
            publicKey: uint8ArrayToBase64(keyPair.publicKey),
            secretKey: uint8ArrayToBase64(keyPair.secretKey)
        };
    }
}

export function base64ToUint8Array(base64: string): Uint8Array {
    const binaryString = atob(base64);
    const len = binaryString.length;
    const bytes = new Uint8Array(len);
    for (let i = 0; i < len; i++) {
        bytes[i] = binaryString.charCodeAt(i);
    }
    return bytes;
}

export function uint8ArrayToBase64(uint8Array: Uint8Array): string {
    let binaryString = '';
    const len = uint8Array.byteLength;
    for (let i = 0; i < len; i++) {
        binaryString += String.fromCharCode(uint8Array[i]);
    }
    return btoa(binaryString);
}