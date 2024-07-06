package response;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import state.State;
import system.REST;

import javax.inject.Inject;

/**
 * Represents the response to a get recent messages request.
 */
public class GetRecentMessagesResponse implements Response {
    @Inject
    private State state;
    private final Gson gson = new Gson();

    public GetRecentMessagesResponse() {
        state = REST.getState();
    }

    /**
     * Constructs a GetRecentMessagesResponse object.
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
        var result = state.getMessages();
        return gson.toJson(result);
    }
}
