package request;

import com.google.gson.annotations.SerializedName;
import response.GetPublicCredentialsResponse;
import response.Response;

public class GetPublicCredentialsRequest implements Request {

    @SerializedName("userId")
    private String userId;

    /**
     * Constructs a GetPublicCredentialsRequest object.
     *
     * @param userId the user id
     */
    public GetPublicCredentialsRequest(String userId) {
        this.userId = userId;
    }

    /**
     * Gets the command of the request.
     *
     * @return the command of the request
     */
    @Override
    public String getCommand() {
        return "getPublicCredentials";
    }

    /**
     * Gets the response of the request.
     *
     * @return the response of the request
     */
    @Override
    public Response getResponse() {
        return new GetPublicCredentialsResponse(userId);
    }

    /**
     * Executes the request.
     */
    @Override
    public void execute() { }
}
