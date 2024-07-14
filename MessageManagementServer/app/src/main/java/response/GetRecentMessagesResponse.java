package response;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.inject.Inject;
import state.State;
import system.Message;

import java.util.List;

/**
 * Represents the response to a get recent messages request.
 */
public class GetRecentMessagesResponse implements Response {
    @Inject
    private State state;
    private final Gson gson = new Gson();
    private final Integer recencyBuffer = 10;

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
        List<Message> result = state.getMessages().subList(Math.max(0, state.getMessages().size() - recencyBuffer), state.getMessages().size());
        return gson.toJson(result);
    }
}
