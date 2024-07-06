package state;

import com.rabbitmq.client.ConnectionFactory;
import system.Message;
import system.Config;
import system.Credentials;
import system.User;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeoutException;

/**
 * Class that holds the state of the application.
 */
@Singleton
public class State {
    private com.rabbitmq.client.Connection rabbitMqConnection;
    private java.sql.Connection dbConnection;
    private HashMap<String, User> activeUsers;
    private HashMap<String, Message> activeMessages;

    /**
     * Constructor for the State class.
     */
    @Inject
    public State() {
        try {
            // Initialize RabbitMQ connection
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost(Config.RABBITMQ_URL);
            factory.setUsername(Config.RABBITMQ_USERNAME);
            factory.setPassword(Config.RABBITMQ_PASSWORD);
            rabbitMqConnection = factory.newConnection();
        } catch (IOException | TimeoutException e) {
            System.out.println("Failed to connect to RabbitMQ " + e.getMessage());
        }

        try {
            // Initialize database connection
            dbConnection = DriverManager.getConnection(Config.DB_URL, Config.DB_USERNAME, Config.DB_PASSWORD);

            // Create tables if they don't exist
            Statement statement = dbConnection.createStatement();
            statement.execute("CREATE DATABASE IF NOT EXISTS e3ms;");
            statement.execute("USE e3ms;");
            statement.execute("CREATE TABLE IF NOT EXISTS users (id VARCHAR(255), name VARCHAR(255), username VARCHAR(255), password VARCHAR(255));");
            statement.execute("CREATE TABLE IF NOT EXISTS messages (id VARCHAR(255), sender VARCHAR(255), receiver VARCHAR(255), timestamp VARCHAR(255), message VARCHAR(255));");
            loadState(dbConnection.createStatement());
        } catch (SQLException e) {
            System.out.println("Failed to connect to database " + e.getMessage());
        }

        // Initialize active maps
        activeUsers = new HashMap<>();
        activeMessages = new HashMap<>();
    }

    /**
     * Load the state of the application from the database.
     *
     * @param statement The statement to execute queries on the database.
     * @throws SQLException If an error occurs while executing the queries.
     */
    private void loadState(Statement statement) throws SQLException {
        ResultSet users = statement.executeQuery("SELECT * FROM users");
        while (users.next()) {
            User user = new User(
                    new Credentials(
                            users.getString("username"), users.getString("password")
                    ),
                    users.getString("name"),
                    users.getString("name")
            );
            user.setId(users.getString("id"));
            activeUsers.put(user.getId(), user);
        }

        ResultSet messages = statement.executeQuery("SELECT * FROM messages");
        while (messages.next()) {
            Message message = new Message(
                    new Message.MetaData(
                            messages.getString("id"),
                            messages.getString("sender"),
                            List.of(messages.getString("receiver").split(",")),
                            messages.getString("timestamp")
                    ),
                    new Message.MessageData(messages.getString("message"))
            );
            activeMessages.put(message.getMetaData().getId(), message);
        }
    }

    /**
     * Add a user to the state.
     *
     * @param user The user to add.
     * @throws SQLException If an error occurs while executing the query.
     */
    public void addUser(User user) throws SQLException {
        String sql = "INSERT INTO users (id, name, username, password) VALUES (?, ?, ?, ?)";
        PreparedStatement statement = dbConnection.prepareStatement(sql);
        statement.setString(1, user.getId());
        statement.setString(2, user.getName());
        statement.setString(3, user.getCredentials().getUsername());
        statement.setString(4, user.getCredentials().getPassword());
        statement.executeUpdate();
        activeUsers.put(user.getId(), user);
    }

    /**
     * Add a message to the state.
     *
     * @param message The message to add.
     * @throws SQLException If an error occurs while executing the query.
     */
    public void addMessage(Message message) throws SQLException {
        String sql = "INSERT INTO messages (id, sender, receiver, timestamp, message) VALUES (?, ?, ?, ?, ?)";
        PreparedStatement statement = dbConnection.prepareStatement(sql);
        statement.setString(1, message.getMetaData().getId());
        statement.setString(2, message.getMetaData().getSender());
        statement.setString(3, String.join(",", message.getMetaData().getReceiver())); // Assuming receiver is a list of strings
        statement.setString(4, message.getMetaData().getTimestamp());
        statement.setString(5, message.getMessageData().getMessage());
        statement.executeUpdate();
        activeMessages.put(message.getMetaData().getId(), message);
    }

    /**
     * Get the RabbitMQ connection.
     *
     * @return The RabbitMQ connection.
     */
    public com.rabbitmq.client.Connection getQueueConnection() {
        return rabbitMqConnection;
    }

    /**
     * Get the database connection.
     *
     * @return The database connection.
     */
    public User getUserById(String id) {
        return activeUsers.get(id);
    }

    /**
     * Get a user by their id.
     *
     * @param id The id of the user.
     * @return The user with the given id.
     */
    public Message getMessageById(String id) {
        return activeMessages.get(id);
    }

    /**
     * Get a message by its sender.
     *
     * @param sender The sender of the message.
     * @return The message with the given sender.
     */
    public List<Message> getMessagesBySender(User sender) {
        List<Message> messages = new ArrayList<>();
        for (Message message : activeMessages.values()) {
            if (message.getMetaData().getSender().equals(sender.getId())) {
                messages.add(message);
            }
        }
        return messages;
    }

    /**
     * Get a message by its receiver.
     *
     * @param receiver The receiver of the message.
     * @return The message with the given receiver.
     */
    public List<Message> getMessagesByReceiver(User receiver) {
        List<Message> messages = new ArrayList<>();
        for (Message message : activeMessages.values()) {
            if (message.getMetaData().getReceiver().contains(receiver.getId())) {
                messages.add(message);
            }
        }
        return messages;
    }

    /**
     * Get all the messages in the state.
     *
     * @return A list of all the messages in the state.
     */
    public List<Message> getMessages() {
        return new LinkedList<>(activeMessages.values());
    }

    /**
     * Get all the users in the state.
     *
     * @return A list of all the users in the state.
     */
    public List<User> getUsers() {
        return new LinkedList<>(activeUsers.values());
    }

    /**
     * Remove a user from the state.
     *
     * @param user The user to remove.
     * @throws SQLException If an error occurs while executing the query.
     */
    public void removeUser(User user) throws SQLException {
        String sql = "DELETE FROM users WHERE id = ?";
        PreparedStatement statement = dbConnection.prepareStatement(sql);
        statement.setString(1, user.getId());
        statement.executeUpdate();

        sql = "DELETE FROM messages WHERE sender = ?";
        statement = dbConnection.prepareStatement(sql);
        statement.setString(1, user.getId());
        statement.executeUpdate();

        activeUsers.remove(user.getId());
    }

    /**
     * Remove a message from the state.
     *
     * @param message The message to remove.
     * @throws SQLException If an error occurs while executing the query.
     */
    public void removeMessage(Message message) throws SQLException {
        String sql = "DELETE FROM messages WHERE id = ?";
        PreparedStatement statement = dbConnection.prepareStatement(sql);
        statement.setString(1, message.getMetaData().getId());
        statement.executeUpdate();
        activeMessages.remove(message.getMetaData().getId());
    }

    /**
     * Close the connections.
     *
     * @throws IOException  If an error occurs while closing the RabbitMQ connection.
     * @throws SQLException If an error occurs while closing the database connection.
     */
    public void close() throws IOException, SQLException {
        rabbitMqConnection.close();
        dbConnection.close();
    }

    /**
     * Clear the active state of the application.
     */
    private void clear() {
        activeUsers.clear();
        activeMessages.clear();
    }
}