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

    @SerializedName("ephemeralKey")
    private String ephemeralKey;
    @SerializedName("signedPreKey")
    private String signedPreKey;

    /**
     * Constructs a new Credentials object with the given username and password.
     *
     * @param username the username
     * @param password the password
     */
    public Credentials(String username, String password, String identityKey, String ephemeralKey, String signedPreKey) {
        this.username = username;
        this.password = password;
        this.identityKey = identityKey;
        this.ephemeralKey = ephemeralKey;
        this.signedPreKey = signedPreKey;
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
    public String getEphemeralKey() {
        return ephemeralKey;
    }

}
