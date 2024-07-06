package request;

import com.google.gson.annotations.SerializedName;
import response.Response;

/**
 * Represents a request.
 */
public interface Request {

    /**
     * Gets the command of the request.
     *
     * @return the command of the request
     */
    @SerializedName("command")
    String getCommand();

    /**
     * Gets the response of the request.
     *
     * @return the response of the request
     */
    Response getResponse();

    /**
     * Executes the request.
     */
    void execute();
}
