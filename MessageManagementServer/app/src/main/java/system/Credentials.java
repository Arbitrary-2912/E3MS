package system;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Stack;

/**
 * This class represents the credentials of a user.
 */
public class Credentials {
    @SerializedName("username")
    private String username;
    @SerializedName("password")
    private String password;
    @SerializedName("identityKey")
    private String identityKey;
    @SerializedName("signedPreKey")
    private String signedPreKey;
    @SerializedName("preKeySignature")
    private String preKeySignature;
    @SerializedName("oneTimePreKeys")
    private ArrayList<String> oneTimePreKeys;

    /**
     * Constructs a new Credentials object with the given username and password.
     *
     * @param username the username
     * @param password the password
     */
    public Credentials(String username, String password, String identityKey, String signedPreKey, String preKeySignature, ArrayList<String> oneTimePreKeys) {
        this.username = username;
        this.password = password;
        this.identityKey = identityKey;
        this.signedPreKey = signedPreKey;
        this.preKeySignature = preKeySignature;
        this.oneTimePreKeys = oneTimePreKeys;
    }

    /**
     * Gets the username.
     *
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Gets the password.
     *
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Gets the identity key.
     *
     * @return the identity key
     */
    public String getIdentityKey() {
        return identityKey;
    }

    /**
     * Gets the signed pre key.
     *
     * @return the signed pre key
     */
    public String getSignedPreKey() {
        return signedPreKey;
    }

    /**
     * Gets the pre key signature.
     *
     * @return the pre key signature
     */
    public String getPreKeySignature() {
        return preKeySignature;
    }

    /**
     * Gets the one time pre keys.
     *
     * @return the one time pre keys
     */
    public ArrayList<String> getOneTimePreKeys() {
        return oneTimePreKeys;
    }
}
