package state;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import request.Request;
import request.VerifyPasswordRequest;
import response.Response;
import system.Config;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

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
            case "addUser" -> gson.fromJson(obj, request.AddUserRequest.class);
            case "getUsers" -> gson.fromJson(obj, request.GetUsersRequest.class);
            case "deleteUser" -> gson.fromJson(obj, request.DeleteUserRequest.class);
            case "addMessage" -> gson.fromJson(obj, request.AddMessageRequest.class);
            case "getRecentMessages" -> gson.fromJson(obj, request.GetRecentMessagesRequest.class);
            case "deleteMessage" -> gson.fromJson(obj, request.DeleteMessageRequest.class);
            case "updateCredentials" -> gson.fromJson(obj, request.UpdateCredentialsRequest.class);
            case "verifyPassword" -> new VerifyPasswordRequest(obj.get("userId").getAsString(), obj.get("password").getAsString());
            default -> {
                log.info("Command not recognized: " + obj.get("command").getAsString());
                yield null;
            }
        };
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