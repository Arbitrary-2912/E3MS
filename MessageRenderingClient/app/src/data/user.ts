import {sha256} from "crypto-hash";

export class User {
    id: string;
    name: string;
    credentials: Credentials;


    constructor(id: string, name: string, credentials?: Credentials) {
        this.id = id;
        this.name = name;
        if (credentials) {
            this.credentials = credentials
        }
    }

    static async createUser(id: string, name: string, password: string, credentials?: Credentials): Promise<User> {
        if (credentials) {
            return new User(id, name, credentials);
        } else {
            const dhKeyPair = await User.generateDHKeyPair();
            const hashedPassword = await sha256(password); // Hash the password before storing
            return new User(id, name, new Credentials(name, hashedPassword, dhKeyPair));
        }
    }

    static async generateDHKeyPair(): Promise<KeyPair> {
        const keyPair = await window.crypto.subtle.generateKey(
            {
                name: "ECDH",
                namedCurve: "P-256",
            },
            true, // extractable
            ["deriveKey", "deriveBits"]
        );

        return new KeyPair(keyPair.privateKey, keyPair.publicKey);
    }

    async deriveSharedSecret(theirPublicKey: CryptoKey): Promise<CryptoKey> {
        return await window.crypto.subtle.deriveKey(
            {
                name: "ECDH",
                public: theirPublicKey,
            },
            this.credentials.dhKeyPair.privateKey,
            { name: "AES-GCM", length: 256 },
            true,
            ["encrypt", "decrypt"]
        );
    }

    async encryptMessage(message: string, theirPublicKeyString: string): Promise<{ ciphertext: string, iv: string }> {
        const theirPublicKey = await this.stringToPublicKey(theirPublicKeyString);

        const sharedSecret = await this.deriveSharedSecret(theirPublicKey);

        const iv = window.crypto.getRandomValues(new Uint8Array(12));

        const encoder = new TextEncoder();
        const encodedMessage = encoder.encode(message);

        const ciphertext = await window.crypto.subtle.encrypt(
            {
                name: "AES-GCM",
                iv: iv,
            },
            sharedSecret,
            encodedMessage
        );

        const ciphertextString = btoa(String.fromCharCode(...new Uint8Array(ciphertext)));
        const ivString = btoa(String.fromCharCode(...iv));

        return { ciphertext: ciphertextString, iv: ivString };
    }

    async decryptMessage(ciphertextString: string, ivString: string, theirPublicKeyString: string): Promise<string> {
        try {
            const theirPublicKey = await this.stringToPublicKey(theirPublicKeyString);
            const ciphertext = new Uint8Array(Array.from(atob(ciphertextString), c => c.charCodeAt(0))).buffer;
            const iv = new Uint8Array(Array.from(atob(ivString), c => c.charCodeAt(0)));
            const sharedSecret = await this.deriveSharedSecret(theirPublicKey);

            const decryptedMessage = await window.crypto.subtle.decrypt(
                {
                    name: "AES-GCM",
                    iv: iv,
                },
                sharedSecret,
                ciphertext
            );

            const decoder = new TextDecoder();
            return decoder.decode(decryptedMessage);
        } catch (error) {
            console.error("Error during decryption:", error);
            throw error;
        }
    }

    async reverseEncryption(ciphertextString: string, ivString: string, theirPublicKeyString: string): Promise<string> {
        try {

            const theirPublicKey = await this.stringToPublicKey(theirPublicKeyString);

            const ciphertext = new Uint8Array(Array.from(atob(ciphertextString), c => c.charCodeAt(0))).buffer;
            const iv = new Uint8Array(Array.from(atob(ivString), c => c.charCodeAt(0)));

            const sharedSecret = await this.deriveSharedSecret(theirPublicKey);

            const decryptedMessage = await window.crypto.subtle.decrypt(
                {
                    name: "AES-GCM",
                    iv: iv,
                },
                sharedSecret,
                ciphertext
            );

            const decoder = new TextDecoder();
            return decoder.decode(decryptedMessage);
        } catch (error) {
            console.error("Error during reverse decryption:", error);
            throw error;
        }
    }

    storeUserKeys() {
        localStorage.setItem('user_id', this.id);
        localStorage.setItem('user_name', this.name);
        localStorage.setItem('user_password', this.credentials.password);
        localStorage.setItem('user_publicKey', JSON.stringify(this.credentials.dhKeyPair.publicKey));
        localStorage.setItem('user_privateKey', JSON.stringify(this.credentials.dhKeyPair.privateKey));
    }

    static retrieveUserKeys(): User | null {
        const id = localStorage.getItem('user_id');
        const name = localStorage.getItem('user_name');
        const password = localStorage.getItem('user_password');
        const publicKey = localStorage.getItem('user_publicKey');
        const privateKey = localStorage.getItem('user_privateKey');

        if (id && name && publicKey && privateKey) {
            const keyPair = new KeyPair(JSON.parse(privateKey), JSON.parse(publicKey));
            return new User(id, name, new Credentials(name, password, keyPair));
        }
        return null;
    }

    async publicKeyToString(): Promise<string> {
        const exportedKey = await window.crypto.subtle.exportKey('spki', this.credentials.dhKeyPair.publicKey);
        return btoa(String.fromCharCode(...new Uint8Array(exportedKey)));
    }


    async stringToPublicKey(publicKeyString: string): Promise<CryptoKey> {
        const binaryDerString = atob(publicKeyString);
        const binaryDer = new Uint8Array(binaryDerString.length);
        for (let i = 0; i < binaryDerString.length; i++) {
            binaryDer[i] = binaryDerString.charCodeAt(i);
        }

        return await window.crypto.subtle.importKey(
            'spki',
            binaryDer.buffer,
            {
                name: "ECDH",
                namedCurve: "P-256",
            },
            true,
            []
        );
    }

    async getPublicInfo() {
        const publicKeyString = await this.publicKeyToString();

        return {
            credentials: {
                username: this.credentials.username,
                password: this.credentials.password,
                publicKey: publicKeyString
            },
            id: this.id,
            name: this.name
        };
    }
}
export class Credentials {
    username: string;
    password: string;
    dhKeyPair: KeyPair;

    constructor(username: string = '', password: string = '', dhKeyPair: KeyPair) {
        this.username = username;
        this.password = password;
        this.dhKeyPair = dhKeyPair;
    }
}

class KeyPair {
    privateKey: CryptoKey;
    publicKey: CryptoKey;

    constructor(privateKey: CryptoKey, publicKey: CryptoKey) {
        this.privateKey = privateKey;
        this.publicKey = publicKey;
    }
}
