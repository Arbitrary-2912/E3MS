package request;

import com.google.gson.annotations.SerializedName;
import response.GetRecentMessagesResponse;
import response.Response;

/**
 * Represents a get recent messages request.
 */
public class GetRecentMessagesRequest implements Request {

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
        return new GetRecentMessagesResponse();
    }

    /**
     * Executes the request.
     */
    @Override
    public void execute() {
        return;
    }
}
