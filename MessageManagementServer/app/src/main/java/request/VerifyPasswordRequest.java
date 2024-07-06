package request;

import com.google.gson.annotations.SerializedName;
import response.Response;
import response.VerifyPasswordResponse;

public class VerifyPasswordRequest implements Request {
    @SerializedName("userId")
    private String userId;
    @SerializedName("password")
    private String password;
    private final Response response;

    public VerifyPasswordRequest() {
        this(null, null);
    }

    public VerifyPasswordRequest(String userId, String password) {
        this.userId = userId;
        this.password = password;
        this.response = new VerifyPasswordResponse(userId, password);
    }

    @SerializedName("command")
    @Override
    public String getCommand() {
        return "verifyPassword";
    }

    @Override
    public Response getResponse() {
        return response;
    }

    public void execute() {
        return;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
