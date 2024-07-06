package request;

import com.google.gson.annotations.SerializedName;
import response.GetUsersResponse;
import response.Response;

/**
 * Represents a get users request.
 */
public class GetUsersRequest implements Request {
    /**
     * Constructs a GetUsersRequest object.
     */
    @SerializedName("command")
    @Override
    public String getCommand() {
        return "getUsers";
    }

    /**
     * Gets the response of the request.
     *
     * @return the response of the request
     */
    @Override
    public Response getResponse() {
        return new GetUsersResponse();
    }

    /**
     * Executes the request.
     */
    @Override
    public void execute() {
        return;
    }
}
