package request;

import com.google.gson.annotations.SerializedName;
import response.Response;
import response.VerifyPasswordResponse;

/**
 * Represents a verify password request.
 */
public class VerifyPasswordRequest implements Request {
    @SerializedName("userId")
    private String userId;
    @SerializedName("password")
    private String password;
    private final Response response;

    /**
     * Constructs a VerifyPasswordRequest object.
     */
    public VerifyPasswordRequest() {
        this(null, null);
    }

    /**
     * Constructs a VerifyPasswordRequest object.
     * @param userId user id of user to verify
     * @param password password to verify
     */
    public VerifyPasswordRequest(String userId, String password) {
        this.userId = userId;
        this.password = password;
        this.response = new VerifyPasswordResponse(userId, password);
    }

    /**
     * Gets the command of the request.
     * @return the command of the request
     */
    @SerializedName("command")
    @Override
    public String getCommand() {
        return "verifyPassword";
    }

    /**
     * Gets the response of the request.
     * @return the response of the request
     */
    @Override
    public Response getResponse() {
        return response;
    }

    /**
     * Executes the request.
     */
    public void execute() {
        return;
    }
}
