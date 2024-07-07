package request;

import com.google.gson.annotations.SerializedName;
import response.Response;
import state.State;
import system.REST;
import system.User;

import javax.inject.Inject;

/**
 * Represents an add user request.
 */
public class AddUserRequest implements Request {
    @Inject
    private State state;
    @SerializedName("user")
    private User user;
    private boolean success = false;

    /**
     * Constructs an AddUserRequest object.
     */
    public AddUserRequest() {
        this.state = REST.getState();
    }

    /**
     * Constructs an AddUserRequest object.
     *
     * @param user the user
     */
    public AddUserRequest(User user) {
        this.user = user;
    }

    /**
     * Gets the command of the request.
     *
     * @return the command of the request
     */
    @SerializedName("command")
    @Override
    public String getCommand() {
        return "addUser";
    }

    /**
     * Gets the response of the request.
     *
     * @return the response of the request
     */
    @Override
    public Response getResponse() {
        return new response.AddUserResponse(success);
    }

    /**
     * Gets the user.
     *
     * @return the user
     */
    public User getUser() {
        return user;
    }

    /**
     * Sets the user.
     *
     * @param user the user
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * Executes the request.
     */
    public void execute() {
        try {
            state.addUser(user);
            success = true;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
