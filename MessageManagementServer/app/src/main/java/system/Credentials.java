package system;

import com.google.gson.annotations.SerializedName;

/**
 * This class represents the credentials of a user.
 */
public class Credentials {
    @SerializedName("username")
    private String username;
    @SerializedName("password")
    private String password;
    @SerializedName("publicKey")
    private String publicKey;

    /**
     * Constructs a new Credentials object with the given username and password.
     *
     * @param username the username
     * @param password the password
     */
    public Credentials(String username, String password, String publicKey) {
        this.username = username;
        this.password = password;
        this.publicKey = publicKey;
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
     * Gets the public key.
     *
     * @return the public key
     */
    public String getPublicKey() {
        return publicKey;
    }

}
