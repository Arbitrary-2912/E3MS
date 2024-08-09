import {
    CryptographyKey,
    Ed25519PublicKey,
    Ed25519SecretKey,
    SodiumPlus,
    X25519PublicKey,
    X25519SecretKey
} from "sodium-plus";

let sodium: SodiumPlus | undefined;

type Keypair = { secretKey: X25519SecretKey; publicKey: X25519PublicKey };

/**
 * Ensure SodiumPlus is initialized.
 */
async function getSodium(): Promise<SodiumPlus> {
    if (!sodium) sodium = await SodiumPlus.auto();
    return sodium;
}

/**
 * Generate a key pair for `crypto_box`.
 */
export async function generateX25519KeyPair(): Promise<Keypair> {
    const sodium = await getSodium();
    const kp = await sodium.crypto_box_keypair();
    return {
        secretKey: await sodium.crypto_box_secretkey(kp),
        publicKey: await sodium.crypto_box_publickey(kp)
    };
}

/**
 * Generate a key pair for `crypto_sign`.
 */
export async function generateEd25519KeyPair(): Promise<{ secretKey: Ed25519SecretKey; publicKey: Ed25519PublicKey }> {
    const sodium = await getSodium();
    const kp = await sodium.crypto_sign_keypair();
    return {
        secretKey: await sodium.crypto_sign_secretkey(kp),
        publicKey: await sodium.crypto_sign_publickey(kp)
    };
}

/**
 * Generate an identity key pair for `crypto_sign`.
 */
export async function generateIdentityKeyPair(): Promise<{ secretKey: Ed25519SecretKey; publicKey: Ed25519PublicKey }> {
    return generateEd25519KeyPair();
}

/**
 * Generate an ephemeral key pair for `crypto_box`.
 */
export async function generateEphemeralKeyPair(): Promise<Keypair> {
    return generateX25519KeyPair();
}

/**
 * Generate a signed pre-key pair using an identity key pair and a pre-key pair.
 * @param identityKeyPair The identity key pair containing the private key for signing.
 * @returns A signed pre-key which includes the pre-key public key and its signature.
 */
export async function getSignedPreKeyPair(
    identityKeyPair: { secretKey: Ed25519SecretKey; publicKey: Ed25519PublicKey }
): Promise<{ publicKey: X25519PublicKey; signature: Buffer }> {
    const sodium = await SodiumPlus.auto();

    // Generate a new X25519 key pair (pre-key pair)
    const preKeyPair = await generateX25519KeyPair();

    // Serialize the pre-key's public key
    const preKeySerialized = preKeyPair.publicKey.getBuffer();

    // Sign the serialized pre-key public key using the identity key pair's secret key
    const signature = await sodium.crypto_sign_detached(preKeySerialized, identityKeyPair.secretKey);

    // Return the public key along with its signature
    return {
        publicKey: preKeyPair.publicKey,
        signature: signature
    };
}

/**
 * Compute the shared secret when sending an initial message.
 * @param senderIdentityKeyPair The identity key pair of the sender.
 * @param senderEphemeralKeyPair The ephemeral key pair of the sender.
 * @param recipientIdentityKey The identity key of the recipient.
 * @param recipientPreKey The pre-key of the recipient.
 * @param recipientOneTimePreKey An optional one-time pre-key of the recipient.
 */
export async function computeX3DHSend(
    senderIdentityKeyPair: { secretKey: Ed25519SecretKey, publicKey: Ed25519PublicKey },
    senderEphemeralKeyPair: { secretKey: X25519SecretKey, publicKey: X25519PublicKey },
    recipientIdentityKey: Ed25519PublicKey,
    recipientPreKey: X25519PublicKey,
    recipientOneTimePreKey?: X25519PublicKey // Optional one-time pre-key
): Promise<Uint8Array> {
    const sodium = await SodiumPlus.auto();

    // Convert Ed25519 keys to X25519 keys
    const senderIdentityX25519Secret = await sodium.crypto_sign_ed25519_sk_to_curve25519(senderIdentityKeyPair.secretKey);
    const recipientIdentityX25519Public = await sodium.crypto_sign_ed25519_pk_to_curve25519(recipientIdentityKey);

    // Perform ECDH operations
    const DH1 = await sodium.crypto_scalarmult(senderEphemeralKeyPair.secretKey, recipientIdentityX25519Public);
    const DH2 = await sodium.crypto_scalarmult(senderIdentityX25519Secret, recipientPreKey);
    const DH3 = await sodium.crypto_scalarmult(senderEphemeralKeyPair.secretKey, recipientPreKey);

    let DH4: Uint8Array | undefined;
    if (recipientOneTimePreKey) {
        DH4 = (await sodium.crypto_scalarmult(senderEphemeralKeyPair.secretKey, recipientOneTimePreKey)).getBuffer();
    }

    return concat(DH1.getBuffer(), DH2.getBuffer(), DH3.getBuffer(), DH4);
}


/**
 * Compute the shared secret when receiving an initial message.
 * @param recipientIdentityKeyPair The identity key pair of the recipient.
 * @param recipientPreKey The pre-key of the recipient.
 * @param senderIdentityKey The identity key of the sender.
 * @param senderEphemeralKey The ephemeral key of the sender.
 * @param recipientOneTimePreKey An optional one-time pre-key of the recipient.
 */
export async function computeX3DHReceive(
    recipientIdentityKeyPair: { secretKey: Ed25519SecretKey, publicKey: Ed25519PublicKey },
    recipientPreKey: X25519SecretKey,
    senderIdentityKey: Ed25519PublicKey,
    senderEphemeralKey: X25519PublicKey,
    recipientOneTimePreKey?: X25519SecretKey // Optional one-time pre-key
): Promise<Uint8Array> {
    const sodium = await SodiumPlus.auto();

    // Convert Ed25519 keys to X25519 keys
    const recipientIdentityX25519Secret = await sodium.crypto_sign_ed25519_sk_to_curve25519(recipientIdentityKeyPair.secretKey);
    const senderIdentityX25519Public = await sodium.crypto_sign_ed25519_pk_to_curve25519(senderIdentityKey);

    // Perform ECDH operations
    const DH1 = await sodium.crypto_scalarmult(recipientPreKey, senderIdentityX25519Public);
    const DH2 = await sodium.crypto_scalarmult(recipientIdentityX25519Secret, senderEphemeralKey);
    const DH3 = await sodium.crypto_scalarmult(recipientPreKey, senderEphemeralKey);

    let DH4: Uint8Array | undefined;
    if (recipientOneTimePreKey) {
        DH4 = (await sodium.crypto_scalarmult(recipientOneTimePreKey, senderEphemeralKey)).getBuffer();
    }

    return concat(DH1.getBuffer(), DH2.getBuffer(), DH3.getBuffer(), DH4);
}


/**
 * Concatenate multiple Uint8Array objects.
 *
 * @param {Uint8Array[]} args - The arrays to concatenate.
 * @returns {Uint8Array} - The concatenated array.
 */
export function concat(...args: Uint8Array[]): Uint8Array {
    const length = args.reduce((sum, arg) => sum + arg.length, 0);
    const output = new Uint8Array(length);
    let offset = 0;
    for (const arg of args) {
        output.set(arg, offset);
        offset += arg.length;
    }
    return output;
}


export async function convertUint8ArrayToX25519SecretKey(array: Uint8Array) {
    new X25519PublicKey(
        await sodium.sodium_hex2bin(array.toString())
    );
}
