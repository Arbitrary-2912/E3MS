package response;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.inject.Inject;
import state.State;
import system.Credentials;
import system.REST;

/**
 * Represents the response to an update credentials request.
 */
public class UpdateCredentialsResponse implements Response {
    @Inject
    private State state;
    private final Gson gson = new Gson();
    private boolean result;

    /**
     * Constructs an UpdateCredentialsResponse object.
     * @param userId User ID to update credentials
     * @param credentials New credentials
     */
    public UpdateCredentialsResponse(String userId, Credentials credentials) throws Exception {
        try {
            state.getUserById(userId).setCredentials(credentials);
            result = true;
        } catch (Exception e) {
            result = false;
        }
    }

    /**
     * Gets the status of the response.
     * @return the status of the response
     */
    @Override
    public String status() {
        return "200";
    }

    /**
     * Gets the response.
     *
     * @return the response
     */
    @SerializedName("response")
    @Override
    public String response() {
        return gson.toJson(result);
    }
}
