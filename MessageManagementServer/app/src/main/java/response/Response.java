package response;

import com.google.gson.annotations.SerializedName;

/**
 * Represents the response to a request.
 */
public interface Response {
    /**
     * Gets the status of the response.
     *
     * @return the status of the response
     */
    @SerializedName("status")
    String status();

    /**
     * Gets the response.
     *
     * @return the response
     */
    @SerializedName("response")
    String response();
}
