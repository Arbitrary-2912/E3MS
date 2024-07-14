package request;

import com.google.gson.annotations.SerializedName;
import com.google.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import response.DeleteUserResponse;
import response.Response;
import state.State;
import system.REST;

/**
 * Represents a delete user request.
 */
public class DeleteUserRequest implements Request {
    private static final Logger log = LoggerFactory.getLogger(DeleteUserRequest.class);
    @Inject
    private State state;
    @SerializedName("userId")
    public String userId;
    private boolean success = false;

    /**
     * Constructs a DeleteUserRequest object.
     */
    public DeleteUserRequest() {
        this(null);
    }

    /**
     * Constructs a DeleteUserRequest object.
     *
     * @param userId the user id
     */
    public DeleteUserRequest(String userId) {
        this.userId = userId;
    }

    /**
     * Gets the command of the request.
     *
     * @return the command of the request
     */
    @SerializedName("command")
    @Override
    public String getCommand() {
        return "deleteUser";
    }

    /**
     * Gets the response of the request.
     *
     * @return the response of the request
     */
    @Override
    public Response getResponse() {
        return new DeleteUserResponse(success);
    }

    /**
     * Gets the user id.
     *
     * @return the user id
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Sets the user id.
     *
     * @param userId the user id
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * Executes the request.
     */
    public void execute() {
        try {
            state.removeUser(state.getUserById(userId));
            success = true;
        } catch (Exception e) {
            log.error("Error removing user {} : {}", userId, e.getMessage());
            success = false;
        }
    }
}
