package request;

import com.google.gson.annotations.SerializedName;
import response.GetRecentMessagesResponse;
import response.Response;
import system.Message;
import system.REST;
import system.User;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Represents a get recent messages request.
 */
public class GetRecentMessagesRequest implements Request {
    private static int RECENCY_BUFFER = 10;
    private User user;
    private List<Message> messages;

    public GetRecentMessagesRequest(User user) {
        this.user = user;
    }
    /**
     * Constructs a GetRecentMessagesRequest object.
     */
    @SerializedName("command")
    @Override
    public String getCommand() {
        return "getRecentMessages";
    }

    /**
     * Gets the response of the request.
     *
     * @return the response of the request
     */
    @Override
    public Response getResponse() {
        return new GetRecentMessagesResponse(messages);
    }

    /**
     * Executes the request.
     */
    @Override
    public void execute() {
        try {
            messages = REST.getState().getMessagesByReceiver(user).subList(Math.max(0, REST.getState().getMessagesByReceiver(user).size() - RECENCY_BUFFER), REST.getState().getMessagesByReceiver(user).size());
        } catch (Exception e) {
            messages = Collections.emptyList();
        }
    }
}
