package state;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import request.Request;
import response.Response;
import system.Config;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;

/**
 * Class that publishes messages to a RabbitMQ queue.
 */
@Singleton
public class Handler {
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
        server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/", new MessageHandler());
        new Thread(server::start).start();
    }

    public void stop() {
        server.stop(0);
    }

    /**
     * Closes the connection to the RabbitMQ server.
     *
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
                if ("OPTIONS".equals(exchange.getRequestMethod()) || "PUT".equals(exchange.getRequestMethod()) || "POST".equals(exchange.getRequestMethod())) {
                    InputStream requestStream = exchange.getRequestBody();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(requestStream, StandardCharsets.UTF_8));
                    StringBuilder request = new StringBuilder();

                    String line;
                    while ((line = reader.readLine()) != null) {
                        request.append(line);
                    }

                    reader.close();

                    String payload = request.toString();

                    Request message = convertStringToMessage(payload);
                    Response response = message.getResponse();

                    exchange.sendResponseHeaders(Integer.parseInt(response.status()), response.response().length());
                    OutputStream os = exchange.getResponseBody();
                    os.write(response.response().getBytes());
                    os.close();
                } else {
                    exchange.sendResponseHeaders(405, 0);
                }
                exchange.close();
            } catch (Exception e) {
                System.out.println(e.getMessage());
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
            case "verifyPassword" -> gson.fromJson(obj, request.VerifyPasswordRequest.class);
            default -> null;
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