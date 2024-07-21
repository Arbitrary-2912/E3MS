package request;

import com.google.gson.annotations.SerializedName;
import com.google.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import response.AddMessageResponse;
import response.Response;
import state.State;
import system.Message;
import system.REST;

/**
 * Represents an add message request.
 */
public class AddMessageRequest implements Request {
    private static final Logger log = LoggerFactory.getLogger(AddMessageRequest.class);
    @Inject
    private State state;
    @SerializedName("message")
    private Message message;
    private boolean success = false;

    /**
     * Constructs an AddMessageRequest object.
     *
     * @param message the message
     */
    public AddMessageRequest(Message message) {
        this.state = REST.getState();
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
            // TODO derive the shared secret
            state.addMessage(message);
            success = true;
        } catch (Exception e) {
            log.error("Failed to add message {}: {}", message, e.getMessage());
            success = false;
        }
    }
}
