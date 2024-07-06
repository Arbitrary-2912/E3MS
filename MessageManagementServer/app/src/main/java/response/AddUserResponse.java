package response;

/**
 * Represents the response to an add user request.
 */
public class AddUserResponse implements Response {
    private final boolean result;

    /**
     * Constructs an AddUserResponse object.
     *
     * @param result the result of the request
     */
    public AddUserResponse(boolean result) {
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
        return "User added";
    }
}
