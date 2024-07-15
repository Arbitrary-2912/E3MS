package request;

import com.google.gson.annotations.SerializedName;
import com.mysql.cj.log.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import response.Response;
import response.UpdateCredentialsResponse;
import system.Credentials;

/**
 * Represents an update credentials request.
 */
public class UpdateCredentialsRequest implements Request {
    private static final Logger log = LoggerFactory.getLogger(UpdateCredentialsRequest.class);
    @SerializedName("userId")
    private String userId;
    @SerializedName("credentials")
    private Credentials credentials;

    public UpdateCredentialsRequest(String userId, Credentials credentials) {
        this.userId = userId;
        this.credentials = credentials;
    }

    /**
     * Gets the command of the request.
     *
     * @return the command of the request
     */
    @SerializedName("command")
    @Override
    public String getCommand() {
        return "updateCredentials";
    }

    /**
     * Gets the response of the request.
     *
     * @return the response of the request
     */
    @Override
    public Response getResponse() {
        try {
            return new UpdateCredentialsResponse(userId, credentials);
        } catch (Exception e) {
            log.error("Unable to update credentials of user {} : {}", userId, e.getMessage());
            return new Response() {
                @Override
                public String status() {
                    return "400";
                }

                @Override
                public String response() {
                    return "Unable to update user credentials";
                }
            };
        }
    }

    /**
     * Executes the request.
     */
    @Override
    public void execute() {
        return;
    }
}
