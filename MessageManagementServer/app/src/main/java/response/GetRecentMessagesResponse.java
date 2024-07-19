package response;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.inject.Inject;
import state.State;
import system.Message;
import system.REST;

import java.util.List;

/**
 * Represents the response to a get recent messages request.
 */
public class GetRecentMessagesResponse implements Response {
    private final Gson gson = new Gson();
    private final List<Message> messages;

    public GetRecentMessagesResponse(List<Message> messages) {
        this.messages = messages;
    }

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
        return gson.toJson(messages);
    }
}
