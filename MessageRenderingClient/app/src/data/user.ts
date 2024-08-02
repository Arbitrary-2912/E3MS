import nacl from 'tweetnacl';

export class User {
    credentials: Credentials;
    id: string;
    name: string;

    constructor(id: string, name: string, password: string) {
        this.credentials = new Credentials(name, password);
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
            },
            id: this.id,
            name: this.name,
        };
    }
}

export class PublicCredentials {
    identityKey: string;
    ephemeralKey: string;
    signedPreKey: string;

    constructor(identityKey: string, ephemeralKey: string, signedPreKey: string) {
        this.identityKey = identityKey;
        this.ephemeralKey = ephemeralKey;
        this.signedPreKey = signedPreKey;
    }
}

class Credentials {
    username: string;
    password: string;
    identityKeyPair: { publicKey: string; secretKey: string };
    ephemeralKeyPair: { publicKey: string; secretKey: string };
    signedPreKeyPair: { publicKey: string; secretKey: string };

    constructor(username: string, password: string) {
        this.username = username;

        this.password = uint8ArrayToBase64(nacl.hash(new TextEncoder().encode(password)));

        this.identityKeyPair = this.generateKeyPair();

        this.ephemeralKeyPair = this.generateKeyPair();

        this.signedPreKeyPair = this.generateKeyPair();
    }

    private generateKeyPair(): { publicKey: string; secretKey: string } {
        const keyPair = nacl.box.keyPair();
        return {
            publicKey: uint8ArrayToBase64(keyPair.publicKey),
            secretKey: uint8ArrayToBase64(keyPair.secretKey)
        };
    }

    private diffieHellman(publicKey: Uint8Array, secretKey: Uint8Array): Uint8Array {
        return nacl.scalarMult(secretKey, publicKey);
    }

    public x3dh(
        recipientIdentityPublicKey: Uint8Array,
        recipientSignedPreKey: Uint8Array,
        recipientOneTimePreKey: Uint8Array | null
    ): Uint8Array {
        const DH1 = this.diffieHellman(base64ToUint8Array(this.identityKeyPair.secretKey), recipientSignedPreKey);
        const DH2 = this.diffieHellman(base64ToUint8Array(this.ephemeralKeyPair.secretKey), recipientIdentityPublicKey);
        const DH3 = this.diffieHellman(base64ToUint8Array(this.ephemeralKeyPair.secretKey), recipientIdentityPublicKey);
        let DH4: Uint8Array | null = null;

        if (recipientOneTimePreKey) {
            DH4 = this.diffieHellman(base64ToUint8Array(this.ephemeralKeyPair.secretKey), recipientOneTimePreKey);
        }

        // Calculate the total length of all DH shared secrets
        const totalLength = DH1.length + DH2.length + DH3.length + (DH4 ? DH4.length : 0);

        // Create a new Uint8Array with the total length
        const sharedSecret = new Uint8Array(totalLength);

        // Copy each DH shared secret into the combined array
        sharedSecret.set(DH1, 0);
        sharedSecret.set(DH2, DH1.length);
        sharedSecret.set(DH3, DH1.length + DH2.length);
        if (DH4) {
            sharedSecret.set(DH4, DH1.length + DH2.length + DH3.length);
        }

        return sharedSecret;
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

export async function storeKeys(username: string, password: string, keys: Credentials) {
    const enc = new TextEncoder();
    const encPassword = enc.encode(password);

    // Derive a key from the password
    const keyMaterial = await window.crypto.subtle.importKey(
        "raw", encPassword, "PBKDF2", false, ["deriveKey"]
    );
    const key = await window.crypto.subtle.deriveKey(
        {
            "name": "PBKDF2",
            salt: enc.encode(username),
            iterations: 100000,
            hash: "SHA-256"
        },
        keyMaterial,
        { "name": "AES-GCM", "length": 256 },
        false,
        ["encrypt", "decrypt"]
    );

    // Encrypt the keys
    const keysJson = JSON.stringify(keys);
    const keysArray = enc.encode(keysJson);
    const iv = window.crypto.getRandomValues(new Uint8Array(12));
    const encryptedKeys = await window.crypto.subtle.encrypt(
        { "name": "AES-GCM", iv },
        key,
        keysArray
    );

    // Store encrypted keys and iv in localStorage
    localStorage.setItem(`${username}-keys`, uint8ArrayToBase64(new Uint8Array(encryptedKeys)));
    localStorage.setItem(`${username}-iv`, uint8ArrayToBase64(iv));
}

export async function retrieveKeys(username: string, password: string): Promise<Credentials | null> {
    const enc = new TextEncoder();
    const dec = new TextDecoder();
    const encPassword = enc.encode(password);

    // Derive a key from the password
    const keyMaterial = await window.crypto.subtle.importKey(
        "raw", encPassword, "PBKDF2", false, ["deriveKey"]
    );
    const key = await window.crypto.subtle.deriveKey(
        {
            "name": "PBKDF2",
            salt: enc.encode(username),
            iterations: 100000,
            hash: "SHA-256"
        },
        keyMaterial,
        { "name": "AES-GCM", "length": 256 },
        false,
        ["encrypt", "decrypt"]
    );

    // Retrieve the encrypted keys and iv from localStorage
    const encryptedKeysBase64 = localStorage.getItem(`${username}-keys`);
    const ivBase64 = localStorage.getItem(`${username}-iv`);
    if (!encryptedKeysBase64 || !ivBase64) {
        return null;
    }

    const encryptedKeys = base64ToUint8Array(encryptedKeysBase64);
    const iv = base64ToUint8Array(ivBase64);

    // Decrypt the keys
    const decryptedKeysArray = await window.crypto.subtle.decrypt(
        { "name": "AES-GCM", iv },
        key,
        encryptedKeys
    );

    const keysJson = dec.decode(decryptedKeysArray);
    return JSON.parse(keysJson);
}
