package state;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import system.Config;
import system.Credentials;
import system.Message;
import system.User;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

/**
 * Class that holds the state of the application.
 */
@Singleton
public class State {
    private static final Logger log = LoggerFactory.getLogger(State.class);
    private java.sql.Connection dbConnection;
    private final HashMap<String, User> activeUsers;
    private final HashMap<String, Message> activeMessages;

    /**
     * Constructor for the State class.
     */
    @Inject
    public State() {
        if (Config.DB_ENABLED) {
            try {
                // Initialize database connection
                dbConnection = DriverManager.getConnection(Config.DB_URL, Config.DB_USERNAME, Config.DB_PASSWORD);

                // Create tables if they don't exist
                Statement statement = dbConnection.createStatement();
                statement.execute("CREATE DATABASE IF NOT EXISTS e3ms;");
                statement.execute("USE e3ms;");
                statement.execute("CREATE TABLE IF NOT EXISTS users (id VARCHAR(255), alias VARCHAR(255), username VARCHAR(255), password VARCHAR(255)), identitykey VARCHAR(255), ephemeralkey VARCHAR(255), signedprekey VARCHAR(255);");
                statement.execute("CREATE TABLE IF NOT EXISTS messages (id VARCHAR(255), sender VARCHAR(255), receiver VARCHAR(255), timestamp VARCHAR(255), message VARCHAR(7500));");
                loadState(dbConnection.createStatement());
            } catch (SQLException e) {
                log.error("Failed to connect to database " + e.getMessage());
            }
        } else {
            activeUsers = new HashMap<>();
            activeMessages = new HashMap<>();
        }
    }

    /**
     * Load the state of the application from the database.
     *
     * @param statement The statement to execute queries on the database.
     * @throws SQLException If an error occurs while executing the queries.
     */
    private void loadState(Statement statement) throws SQLException {
        if (Config.DB_ENABLED) {
            ResultSet users = statement.executeQuery("SELECT * FROM users");
            while (users.next()) {
                User user = new User(
                        new Credentials(
                                users.getString("username"),
                                users.getString("password"),
                                users.getString("identityKey"),
                                users.getString("ephemeralKey"),
                                users.getString("signedPreKey")
                        ),
                        users.getString("id"),
                        users.getString("alias")
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
        } else {
            log.info("Database is disabled, skipping loading state");
            clear();
        }
    }

    /**
     * Add a user to the state.
     *
     * @param user The user to add.
     * @throws SQLException If an error occurs while executing the query.
     */
    public void addUser(User user) throws SQLException {
        if (Config.DB_ENABLED) {
            String sql = "INSERT INTO users (id, alias, username, password, identitykey, ephemeralkey, signedprekey) VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement statement = dbConnection.prepareStatement(sql);
            statement.setString(1, user.getId());
            statement.setString(2, user.getAlias());
            statement.setString(3, user.getCredentials().getUsername());
            statement.setString(4, user.getCredentials().getPassword());
            statement.setString(5, user.getCredentials().getIdentityKey());
            statement.setString(6, user.getCredentials().getEphemeralKey());
            statement.setString(7, user.getCredentials().getSignedPreKey());
            statement.executeUpdate();
        }
        activeUsers.put(user.getId(), user);
    }

    /**
     * Add a message to the state.
     *
     * @param message The message to add.
     * @throws SQLException If an error occurs while executing the query.
     */
    public void addMessage(Message message) throws SQLException {
        if (Config.DB_ENABLED) {
            String sql = "INSERT INTO messages (id, sender, receiver, timestamp, message) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement statement = dbConnection.prepareStatement(sql);
            statement.setString(1, message.getMetaData().getId());
            statement.setString(2, message.getMetaData().getSender());
            statement.setString(3, String.join(",", message.getMetaData().getReceiver())); // Assuming receiver is a list of strings
            statement.setString(4, message.getMetaData().getTimestamp());
            statement.setString(5, message.getMessageData().getMessage());
            statement.executeUpdate();
        }
        activeMessages.put(message.getMetaData().getId(), message);
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
    public List<Message> getMessagesBySender(User sender) {  // TODO perform credentials agreement checking
        if (Config.DB_ENABLED) {
            String sql = "SELECT id, sender, receiver, timestamp, message FROM messages WHERE sender = ? ORDER BY timestamp DESC";
            try {
                PreparedStatement statement = dbConnection.prepareStatement(sql);
                statement.setString(1, sender.getId());
                ResultSet resultSet = statement.executeQuery();
                List<Message> messages = new ArrayList<>();
                while (resultSet.next()) {
                    Message message = new Message(
                            new Message.MetaData(
                                    resultSet.getString("id"),
                                    resultSet.getString("sender"),
                                    List.of(resultSet.getString("receiver").split(",")),
                                    resultSet.getString("timestamp")
                            ),
                            new Message.MessageData(resultSet.getString("message"))
                    );
                    messages.add(message);
                }
                return messages;
            } catch (SQLException e) {
                log.error("Failed to get messages by sender " + e.getMessage());
            }
        }
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
    public List<Message> getMessagesByReceiver(User receiver) { // TODO perform credentials agreement checking
        if (Config.DB_ENABLED) {
            String sql = "SELECT id, sender, receiver, timestamp, message FROM messages WHERE FIND_IN_SET(?, receiver) ORDER BY timestamp DESC";
            try {
                PreparedStatement statement = dbConnection.prepareStatement(sql);
                statement.setString(1, receiver.getId());
                ResultSet resultSet = statement.executeQuery();
                List<Message> messages = new ArrayList<>();
                while (resultSet.next()) {
                    Message message = new Message(
                            new Message.MetaData(
                                    resultSet.getString("id"),
                                    resultSet.getString("sender"),
                                    List.of(resultSet.getString("receiver").split(",")),
                                    resultSet.getString("timestamp")
                            ),
                            new Message.MessageData(resultSet.getString("message"))
                    );
                    messages.add(message);
                }
                return messages;
            } catch (SQLException e) {
                log.error("Failed to get messages by receiver " + e.getMessage());
            }
        }
        List<Message> messages = new ArrayList<>();
        for (Message message : activeMessages.values()) {
            if (message.getMetaData().getReceiver().contains(receiver.getId())) {
                messages.add(message);
            }
        }
        return messages;
    }

    /**
     * Get the most recent messages.
     *
     * @param userId The userId to get the messages for.
     * @param count The maximum number of messages to return.
     * @return The most recent messages.
     */
    public List<Message> getRecentMessages(String userId, int count) { // TODO perform credentials agreement checking
        if (Config.DB_ENABLED) {
            String sql = "SELECT id, sender, receiver, timestamp, message FROM messages WHERE FIND_IN_SET(?, receiver) ORDER BY timestamp DESC LIMIT " + count;
            try {
                PreparedStatement statement = dbConnection.prepareStatement(sql);
                statement.setString(1, userId);
                ResultSet resultSet = statement.executeQuery();
                List<Message> messages = new ArrayList<>();
                while (resultSet.next()) {
                    Message message = new Message(
                            new Message.MetaData(
                                    resultSet.getString("id"),
                                    resultSet.getString("sender"),
                                    List.of(resultSet.getString("receiver").split(",")),
                                    resultSet.getString("timestamp")
                            ),
                            new Message.MessageData(resultSet.getString("message"))
                    );
                    messages.add(message);
                }
                return messages;
            } catch (SQLException e) {
                log.error("Failed to get recent messages " + e.getMessage());
            }
        }
        List<Message> messages = new ArrayList<>();
        for (Message message : activeMessages.values()) {
            if (message.getMetaData().getReceiver().contains(userId) && count-- > 0) {
                messages.add(message);
            } else if (count <= 0) {
                break;
            }
        }
        Collections.sort(messages);
        return messages;
    }

    /**
     * Get all the messages in the state.
     *
     * @return A list of all the messages in the state.
     */
    public List<Message> getMessages() {
        return new ArrayList<>(activeMessages.values());
    }

    /**
     * Get all the users in the state.
     *
     * @return A list of all the users in the state.
     */
    public List<User> getUsers() {
        return new ArrayList<>(activeUsers.values());
    }

    /**
     * Remove a user from the state.
     *
     * @param user The user to remove.
     * @throws SQLException If an error occurs while executing the query.
     */
    public void removeUser(User user) throws SQLException {
        if (Config.DB_ENABLED) {
            String sql = "DELETE FROM users WHERE id = ?";
            PreparedStatement statement = dbConnection.prepareStatement(sql);
            statement.setString(1, user.getId());
            statement.executeUpdate();

            sql = "DELETE FROM messages WHERE sender = ?";
            statement = dbConnection.prepareStatement(sql);
            statement.setString(1, user.getId());
            statement.executeUpdate();
        }

        activeUsers.remove(user.getId());
    }

    /**
     * Remove a message from the state.
     *
     * @param message The message to remove.
     * @throws SQLException If an error occurs while executing the query.
     */
    public void removeMessage(Message message) throws SQLException {
        if (Config.DB_ENABLED) {
            String sql = "DELETE FROM messages WHERE id = ?";
            PreparedStatement statement = dbConnection.prepareStatement(sql);
            statement.setString(1, message.getMetaData().getId());
            statement.executeUpdate();
        }
        activeMessages.remove(message.getMetaData().getId());
    }

    /**
     * Update the credentials of a user.
     * @param userId The id of the user.
     * @param credentials The new credentials.
     */
    public void updateCredentials(String userId, Credentials credentials) {
        User user = activeUsers.get(userId);
        if (user != null) {
            if (Config.DB_ENABLED) {
                try {
                    String sql = "UPDATE users SET username = ?, password = ?, identitykey = ?, ephemeralkey = ?, signedprekey = ? WHERE id = ?";
                    PreparedStatement statement = dbConnection.prepareStatement(sql);
                    statement.setString(1, credentials.getUsername());
                    statement.setString(2, credentials.getPassword());
                    statement.setString(3, credentials.getIdentityKey());
                    statement.setString(4, credentials.getEphemeralKey());
                    statement.setString(5, credentials.getSignedPreKey());
                    statement.setString(6, userId);
                    statement.executeUpdate();
                } catch (SQLException e) {
                    log.error("Failed to update credentials for user " + userId + " : " + e.getMessage());
                }
            }
            user.setCredentials(credentials);
            activeUsers.put(userId, user);
        }
    }

    /**
     * Close the connections.
     *
     * @throws SQLException If an error occurs while closing the database connection.
     */
    public void close() throws SQLException {
        if (Config.DB_ENABLED) {
            dbConnection.close();
        }
    }

    /**
     * Clear the active state of the application.
     */
    private void clear() {
        activeUsers.clear();
        activeMessages.clear();
    }
}