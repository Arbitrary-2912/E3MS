package system;

import com.google.gson.annotations.SerializedName;

/**
 * Represents a user in the system.
 * A user has credentials, an id, and a name.
 * The id is unique to each user.
 * The name is the user's display name.
 */
public class User {
    @SerializedName("credentials")
    private Credentials credentials;
    @SerializedName("id")
    private String id;
    @SerializedName("name")
    private String name;

    /**
     * Constructs a user with the given credentials, id, and name.
     *
     * @param credentials the user's credentials
     * @param id          the user's id
     * @param name        the user's name
     */
    public User(Credentials credentials, String id, String name) {
        this.credentials = credentials;
        this.id = id;
        this.name = name;
    }

    /**
     * Gets the user's credentials.
     *
     * @return the user's credentials
     */
    public Credentials getCredentials() {
        return credentials;
    }

    /**
     * Sets the user's credentials.
     *
     * @param credentials the user's credentials
     */
    public void setCredentials(Credentials credentials) {
        this.credentials = credentials;
    }

    /**
     * Gets the user's id.
     *
     * @return the user's id
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the user's id.
     *
     * @param id the user's id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Gets the user's name.
     *
     * @return the user's name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the user's name.
     *
     * @param name the user's name
     */
    public void setName(String name) {
        this.name = name;
    }
}
