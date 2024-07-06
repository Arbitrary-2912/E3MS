package response;

/**
 * Represents the response to a delete user request.
 */
public class DeleteUserResponse implements Response {
    private final boolean result;

    public DeleteUserResponse(boolean result) {
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
        return "User deleted";
    }
}
