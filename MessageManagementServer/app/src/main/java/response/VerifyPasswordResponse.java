package response;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import state.State;
import system.REST;

import javax.inject.Inject;

/**
 * Represents the response to a verify password request.
 */
public class VerifyPasswordResponse implements Response {
    private boolean result = false;
    private final Gson gson = new Gson();
    @Inject
    private State state;

    /**
     * Constructs a VerifyPasswordResponse object.
     *
     * @param userId   the user's id
     * @param password the user's password
     */
    public VerifyPasswordResponse(String userId, String password) {
        state = REST.getState();
        try {
            this.result = state.getUserById(userId).getCredentials().getPassword().equals(password);
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
    }

    /**
     * Gets the status of the response.
     *
     * @return the status of the response
     */
    @SerializedName("status")
    @Override
    public String status() {
        return result ? "200" : "403";
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
