package response;

/**
 * Represents the response to an add message request.
 */
public class AddMessageResponse implements Response {
    private final boolean result;

    /**
     * Constructs an AddMessageResponse object.
     *
     * @param result the result of the request
     */
    public AddMessageResponse(boolean result) {
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
        return "Message added";
    }
}
