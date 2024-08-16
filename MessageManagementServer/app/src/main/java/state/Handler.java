package state;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import request.*;
import response.Response;
import system.Config;
import system.Credentials;
import system.Message;
import system.User;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

/**
 * Class that publishes messages to a RabbitMQ queue.
 */
@Singleton
public class Handler {
    private static final Logger log = LoggerFactory.getLogger(Handler.class);
    private final HttpServer server;
    private static final int PORT = Config.PORT;
    private static final Gson gson = new Gson();

    /**
     * Constructor for the QueuePublisher class.
     *
     * @throws Exception If an error occurs.
     */
    @Inject
    public Handler() throws Exception {
        server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.createContext("/", new MessageHandler());
        new Thread(server::start).start();
    }

    public void stop() {
        server.stop(0);
    }

    /**
     * Closes the connection to the RabbitMQ server.
     */
    @Singleton
    private class MessageHandler implements HttpHandler {

        /**
         * Handles an incoming HTTP request.
         *
         * @param exchange the exchange containing the request from the
         *                 client and used to send the response
         */
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            try {
                // Add CORS headers
                Headers headers = exchange.getResponseHeaders();
                headers.add("Access-Control-Allow-Origin", "*");
                headers.add("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
                headers.add("Access-Control-Allow-Headers", "Content-Type");

                if ("OPTIONS".equals(exchange.getRequestMethod())) {
                    // Handle preflight request
                    exchange.sendResponseHeaders(204, -1);
                } else if ("POST".equals(exchange.getRequestMethod())) {
                    InputStream requestStream = exchange.getRequestBody();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(requestStream, StandardCharsets.UTF_8));
                    StringBuilder request = new StringBuilder();

                    String line;
                    while ((line = reader.readLine()) != null) {
                        request.append(line);
                    }
                    reader.close();

                    String payload = request.toString();
                    log.info("Received payload: " + payload);

                    Request message = convertStringToMessage(payload);
                    if (message != null) {
                        message.execute();
                        Response response = message.getResponse();
                        String jsonResponse = convertMessageToString(response);

                        exchange.getResponseHeaders().set("Content-Type", "application/json");
                        exchange.sendResponseHeaders(Integer.parseInt(response.status()), jsonResponse.length());
                        OutputStream os = exchange.getResponseBody();
                        os.write(jsonResponse.getBytes());
                        os.close();
                    } else {
                        exchange.sendResponseHeaders(400, -1); // Bad Request
                    }
                } else {
                    log.info("Method not allowed: " + exchange.getRequestMethod());
                    exchange.sendResponseHeaders(405, -1); // Method Not Allowed
                }
            } catch (Exception e) {
                log.error("Error handling request: " + e.getMessage());
                exchange.sendResponseHeaders(500, -1); // Internal Server Error
            } finally {
                exchange.close();
            }
        }
    }

    /**
     * Converts an input stream to a request.
     *
     * @param stream the input stream to convert
     * @return the request
     */
    private Request convertStringToMessage(String stream) {
        JsonObject obj = gson.fromJson(stream, JsonObject.class);
        return switch (obj.get("command").getAsString()) {
            case "addUser" -> handleAddUserRequest(obj);
            case "getUsers" -> handleGetUsersRequest(obj);
            case "deleteUser" -> handleDeleteUserRequest(obj);
            case "addMessage" -> handleAddMessageRequest(obj);
            case "getRecentMessages" -> handleGetRecentMessagesRequest(obj);
            case "deleteMessage" -> handleDeleteMessageRequest(obj);
            case "updateCredentials" -> handleUpdateCredentialsRequest(obj);
            case "getPublicCredentials" -> handleGetPublicCredentialsRequest(obj);
            case "verifyPassword" -> handleVerifyPasswordRequest(obj);
            default -> {
                log.info("Command not recognized: " + obj.get("command").getAsString());
                yield null;
            }
        };
    }

    /**
     * Handles an add user request.
     * @param obj the JSON object
     * @return the request
     */
    private Request handleAddUserRequest(JsonObject obj) {
        JsonObject user = (JsonObject) obj.get("user");
        JsonObject userCredentials = (JsonObject) user.get("credentials");
        return new AddUserRequest(
                new User(
                        new Credentials(
                                userCredentials.get("username").getAsString(),
                                userCredentials.get("password").getAsString(),
                                userCredentials.get("publicKey").getAsString()
                        ),
                        user.get("id").getAsString(),
                        user.get("name").getAsString()
                )
        );
    }

    /**
     * Handles a get users request.
     * @param obj the JSON object
     * @return the request
     */
    private Request handleGetUsersRequest(JsonObject obj) {
        return new GetUsersRequest();
    }

    /**
     * Handles a delete user request.
     * @param obj the JSON object
     * @return the request
     */
    private Request handleDeleteUserRequest(JsonObject obj) {
        String userId = obj.get("userId").getAsString();
        return new DeleteUserRequest(userId);
    }

    /**
     * Handles an add message request.
     * @param obj the JSON object
     * @return the request
     */
    private Request handleAddMessageRequest(JsonObject obj) {
        JsonObject messageContainer = (JsonObject) obj.get("message");
        JsonObject metaDataContainer = (JsonObject) messageContainer.get("metaData");
        JsonObject messageDataContainer = (JsonObject) messageContainer.get("messageData");

        JsonArray receiverArray = (JsonArray) metaDataContainer.get("receiver");
        ArrayList<String> receiverList = new ArrayList<>();
        for (int i = 0; i < receiverArray.size(); i++) {
            receiverList.add(receiverArray.get(i).getAsString());
        }

        return new AddMessageRequest(
                new Message(
                        new Message.MetaData(
                                metaDataContainer.get("id").getAsString(),
                                metaDataContainer.get("username").getAsString(),
                                metaDataContainer.get("sender").getAsString(),
                                receiverList,
                                metaDataContainer.get("timestamp").getAsString()
                        ),
                        new Message.MessageData(
                                messageDataContainer.get("ciphertext").getAsString(),
                                messageDataContainer.get("iv").getAsString()
                        )
                )
        );
    }


    /**
     * Handles a get recent messages request.
     * @param obj the JSON object
     * @return the request
     */
    private Request handleGetRecentMessagesRequest(JsonObject obj) {
        return new GetRecentMessagesRequest(obj.get("userId").getAsString());
    }

    /**
     * Handles a delete message request.
     * @param obj the JSON object
     * @return the request
     */
    private Request handleDeleteMessageRequest(JsonObject obj) {
        JsonObject metaData = (JsonObject) obj.get("metaData");
        return new DeleteMessageRequest(metaData.get("id").getAsString());
    }


    /**
     * Handles an update credentials request.
     * @param obj the JSON object
     * @return the request
     */
    private Request handleUpdateCredentialsRequest(JsonObject obj) {
        String userId = obj.get("userId").getAsString();
        JsonObject userCredentials = (JsonObject) obj.get("credentials");
        return new UpdateCredentialsRequest(
                userId,
                new Credentials(
                        userCredentials.get("username").getAsString(),
                        userCredentials.get("password").getAsString(),
                        userCredentials.get("publicKey").getAsString()
                )
        );
    }


    /**
     * Handles a get public credentials request.
     * @param obj the JSON object
     * @return the request
     */
    private Request handleGetPublicCredentialsRequest(JsonObject obj) {
        return new GetPublicCredentialsRequest(obj.get("userId").getAsString());
    }


    /**
     * Handles a verify password request.
     * @param obj the JSON object
     * @return the request
     */
    private Request handleVerifyPasswordRequest(JsonObject obj) {
        return new VerifyPasswordRequest(obj.get("userId").getAsString(), obj.get("password").getAsString());
    }


    /**
     * Converts a response to a string.
     *
     * @param response the response to convert
     * @return the string
     */
    private String convertMessageToString(Response response) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("status", response.status());
        jsonObject.addProperty("response", response.response());
        return jsonObject.toString();
    }
}