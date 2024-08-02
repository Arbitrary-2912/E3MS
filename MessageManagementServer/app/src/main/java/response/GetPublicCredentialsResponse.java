package response;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import state.State;
import system.REST;
import system.User;

import javax.inject.Inject;

public class GetPublicCredentialsResponse implements Response {

    private final Gson gson = new Gson();

    @Inject
    private State state;

    private String userId;

    private boolean result;

    /**
     * Constructs a GetPublicCredentialsResponse object.
     * @param userId the user id
     */
    public GetPublicCredentialsResponse(String userId) {
        this.state = REST.getState();
        this.userId = userId;
    }


    /**
     * Gets the status of the response.
     * @return the status of the response
     */
    @Override
    public String status() {
        return result ? "200" : "403";
    }

    /**
     * Gets the response.
     * @return the response
     */
    @Override
    public String response() {
        JsonObject response = new JsonObject();

        User user = state.getUserById(userId);
        if (user == null) {
            response.addProperty("error", "User not found");
            return gson.toJson(response);
        }

        response.addProperty("identityKey", user.getCredentials().getIdentityKey());
        response.addProperty("ephemeralKey", user.getCredentials().getEphemeralKey());
        response.addProperty("signedPreKey", user.getCredentials().getSignedPreKey());
        result = true;
        return gson.toJson(response);
    }
}
