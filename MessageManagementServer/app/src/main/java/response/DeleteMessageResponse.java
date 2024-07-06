package response;

/**
 * Represents the response to a delete message request.
 */
public class DeleteMessageResponse implements Response {
    private final boolean result;

    /**
     * Constructs a DeleteMessageResponse object.
     *
     * @param result the result of the request
     */
    public DeleteMessageResponse(boolean result) {
        this.result = result;
    }

    /**
     * Gets the status of the response.
     *
     * @return the status of the response
     */
    @Override
    public String status() {
        return result ? "200" : "403";
    }

    /**
     * Gets the response.
     *
     * @return the response
     */
    @Override
    public String response() {
        return "Message deleted";
    }
}
