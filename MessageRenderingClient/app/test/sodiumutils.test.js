import * as sodium from 'libsodium-wrappers';
import {
    generateX25519KeyPair,
    generateEd25519KeyPair,
    getSignedPreKeyPair,
    computeX3DHSend,
    computeX3DHReceive, generateEphemeralKeyPair, generateIdentityKeyPair
} from "../sodiumutils.ts";
import {SodiumPlus, X25519PublicKey, X25519SecretKey} from "sodium-plus";
import exp from "node:constants"; // Adjust the import based on your file structure

describe('Cryptographic Utility Functions', () => {
    beforeAll(async () => {
        await SodiumPlus.auto();
    });

    test('generateKeyPair should return a valid key pair', async () => {
        const xkeyPair = await generateX25519KeyPair();
        expect(xkeyPair.publicKey.publicKey === true);
        expect(xkeyPair.secretKey.publicKey === false);

        const edkeyPair = await generateEd25519KeyPair();
        expect(edkeyPair.publicKey.publicKey === true);
        expect(edkeyPair.secretKey.publicKey === false);
    });


    it('should correctly compute the shared secret using X3DH for both sending and receiving', async () => {
        const sodium = await SodiumPlus.auto();

        // Generate key pairs
        const recipientIdentityKeyPair = await generateIdentityKeyPair();
        const recipientSignedPreKey = (await getSignedPreKeyPair(recipientIdentityKeyPair));
        const senderIdentityKeyPair = await generateIdentityKeyPair();
        const senderEphemeralKeyPair = await generateEphemeralKeyPair();

        // Generate optional one-time pre-key for both recipient and sender (if needed)
        const recipientOneTimePreKey = await generateEphemeralKeyPair().then(kp => kp.publicKey);
        const senderOneTimePreKey = await generateEphemeralKeyPair().then(kp => kp.publicKey);

        // Compute shared secret using computeX3DHSend
        const sharedSecretSend = await computeX3DHSend(
            senderIdentityKeyPair,
            senderEphemeralKeyPair,
            recipientIdentityKeyPair.publicKey,
            recipientSignedPreKey.publicKey
        );

        // Compute shared secret using computeX3DHReceive
        const sharedSecretReceive = await computeX3DHReceive(
            recipientIdentityKeyPair,
            new X25519SecretKey(recipientSignedPreKey.signature),
            senderIdentityKeyPair.publicKey,
            senderEphemeralKeyPair.publicKey
        );

        // Assert that the computed shared secrets are equal
        expect(sharedSecretSend).toEqual(sharedSecretReceive);
    });

});
