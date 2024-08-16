package response;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.annotations.SerializedName;
import com.google.inject.Inject;
import state.State;
import system.REST;

/**
 * Represents the response to a get users request.
 */
public class GetUsersResponse implements Response {
    @Inject
    private State state;
    private final Gson gson = new Gson();

    public GetUsersResponse() {
        state = REST.getState();
    }

    /**
     * Constructs a GetUsersResponse object.
     */
    @SerializedName("status")
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
        var result = state.getUsers();
        JsonArray users = new JsonArray();
        result.forEach(user -> users.add(gson.toJsonTree(user)));
        return gson.toJson(users);
    }
}
