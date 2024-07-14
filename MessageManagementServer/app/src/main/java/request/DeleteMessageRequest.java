package request;

import com.google.gson.annotations.SerializedName;
import com.google.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import response.Response;
import state.State;
import system.REST;

/**
 * Represents a delete message request.
 */
public class DeleteMessageRequest implements Request {
    private static final Logger log = LoggerFactory.getLogger(DeleteMessageRequest.class);
    @Inject
    private State state;
    @SerializedName("messageId")
    private String messageId;
    private boolean success = false;

    /**
     * Constructs a DeleteMessageRequest object.
     */
    public DeleteMessageRequest() {
        this(null);
    }

    /**
     * Constructs a DeleteMessageRequest object.
     *
     * @param messageId the message id
     */
    public DeleteMessageRequest(String messageId) {
        this.messageId = messageId;
    }

    /**
     * Gets the command of the request.
     *
     * @return the command of the request
     */
    @SerializedName("command")
    @Override
    public String getCommand() {
        return "deleteMessage";
    }

    /**
     * Gets the response of the request.
     *
     * @return the response of the request
     */
    @Override
    public Response getResponse() {
        return new response.DeleteMessageResponse(success);
    }

    /**
     * Gets the message id.
     *
     * @return the message id
     */
    public String getMessageId() {
        return messageId;
    }

    /**
     * Sets the message id.
     *
     * @param messageId the message id
     */
    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    /**
     * Executes the request.
     */
    public void execute() {
        try {
            state.removeMessage(state.getMessageById(messageId));
            success = true;
        } catch (Exception e) {
            log.error("Failed to delete message {}: {}", messageId, e.getMessage());
            success = false;
        }
    }
}
