package request;

import com.google.gson.annotations.SerializedName;
import response.Response;
import state.State;

import javax.inject.Inject;

/**
 * Represents a delete message request.
 */
public class DeleteMessageRequest implements Request {
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
            success = true;
            state.removeMessage(state.getMessageById(messageId));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
