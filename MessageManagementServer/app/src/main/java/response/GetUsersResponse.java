package response;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.inject.Inject;
import state.State;

/**
 * Represents the response to a get users request.
 */
public class GetUsersResponse implements Response {
    @Inject
    private State state;
    private final Gson gson = new Gson();

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
        return gson.toJson(result);
    }
}
