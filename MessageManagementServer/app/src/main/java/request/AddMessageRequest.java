package request;

import com.google.gson.annotations.SerializedName;
import system.Message;
import response.AddMessageResponse;
import response.Response;
import state.State;

import javax.inject.Inject;

/**
 * Represents an add message request.
 */
public class AddMessageRequest implements Request {
    @Inject
    private State state;
    @SerializedName("message")
    private Message message;
    private boolean success = false;

    /**
     * Constructs an AddMessageRequest object.
     */
    public AddMessageRequest() {
    }

    /**
     * Constructs an AddMessageRequest object.
     *
     * @param message the message
     */
    public AddMessageRequest(Message message) {
        this.message = message;
    }

    /**
     * Gets the command of the request.
     *
     * @return the command of the request
     */
    @SerializedName("command")
    @Override
    public String getCommand() {
        return "addMessage";
    }

    /**
     * Gets the response of the request.
     *
     * @return the response of the request
     */
    @Override
    public Response getResponse() {
        return new AddMessageResponse(success);
    }

    /**
     * Gets the message.
     *
     * @return the message
     */
    public Message getMessage() {
        return message;
    }

    /**
     * Sets the message.
     *
     * @param message the message
     */
    public void setMessage(Message message) {
        this.message = message;
    }

    /**
     * Executes the request.
     */
    public void execute() {
        try {
            state.addMessage(message);
            success = true;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
