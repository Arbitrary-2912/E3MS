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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

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
                statement.execute("CREATE TABLE IF NOT EXISTS users (id VARCHAR(255), name VARCHAR(255), username VARCHAR(255), password VARCHAR(255)), identitykey VARCHAR(255), signedprekey VARCHAR(255), prekeysignature VARCHAR(255), otpkey1 VARCHAR(255), otpkey2 VARCHAR(255), otpkey3 VARCHAR(255), otpkey4 VARCHAR(255);");
                statement.execute("CREATE TABLE IF NOT EXISTS messages (id VARCHAR(255), sender VARCHAR(255), receiver VARCHAR(255), timestamp VARCHAR(255), message VARCHAR(255));");
                loadState(dbConnection.createStatement());
            } catch (SQLException e) {
                log.error("Failed to connect to database " + e.getMessage());
            }
        }

        // Initialize active maps
        activeUsers = new HashMap<>();
        activeMessages = new HashMap<>();

        activeUsers.put("User 1", new User(
                new Credentials(
                        "User 1",
                        "password",
                        "a69f73cca23a9ac5c8b567dc185a756e97c982164fe25859e0d1dcc1475c80a615b2123af1f5f94c11e3e9402c3ac558f500199d95b6d3e301758586281dcd26",
                        "5c07fc5b11d0cd030b0651221488b23314bcef74b6039f701f85b1ca54cf6d39e62f220b0a43aeed3690b3636ccb9f6f452c506af8fb8a69eb1c690062238f87",
                        "8e5d550426c0d92cc57ff20e621477a472faeea3befd013d5f83285a9430d225cc3cc740bd748d9f57382fcb550e30fdf06f502216b59ac9c8ee16c5e38bc8d7",
                        new ArrayList<>() {
                            {
                                add("90006420fc3b6672d3225d0487e27a432c13ccfd249943ad39798f678873c185a804121609c764d156374d0e5035283b94f447c1f2505ea18f517b14fc37da9");
                                add("493b73e9f18aa89b8acbc230516b0e693a0b693622361d9d84cadb82328405c7682b4b7f0cc318879994d4890823020439ed88ae81c75c23b4d3ec10ded904f3");
                                add("f435ba3ef2bf43e694c8940fa315641c67f152c2ee2021f121af5e03f9860607f74e61e1451f9489c2ff59f87dc0e1c501566e2324355de32770ec52cc3bce47");
                                add("ffe4d7127d5e222ac77ded78b503276294960867d5501eda748bbb741dbc238d1d68f5f4c76f38fdb03a491bd9ec8c1e20403440315ac5e8050946a00409a724");
                            }
                        }
                ),
                "User 1",
                "User 1"
        ));
        activeMessages.put("User 1", new Message(
                new Message.MetaData("1", "User 1", List.of("User 1"), "2021-10-10 10:10:10"),
                new Message.MessageData("Hello, World!")
        ));
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
                                users.getString("signedPreKey"),
                                users.getString("preKeySignature"),
                                new ArrayList<>() {
                                    {
                                        add(users.getString("otpKey1"));
                                        add(users.getString("otpKey2"));
                                        add(users.getString("otpKey3"));
                                        add(users.getString("otpKey4"));
                                    }
                                }
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
            String sql = "INSERT INTO users (id, name, username, password, identitykey, signedprekey, prekeysignature, otpkey1, otpkey2, otpkey3, otpkey4) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement statement = dbConnection.prepareStatement(sql);
            statement.setString(1, user.getId());
            statement.setString(2, user.getName());
            statement.setString(3, user.getCredentials().getUsername());
            statement.setString(4, user.getCredentials().getPassword());
            statement.setString(5, user.getCredentials().getIdentityKey());
            statement.setString(6, user.getCredentials().getSignedPreKey());
            statement.setString(7, user.getCredentials().getPreKeySignature());
            statement.setString(
                    8,
                    user.getCredentials().getOneTimePreKeys().get(0) == null ? "" : user.getCredentials().getOneTimePreKeys().get(0)
            );
            statement.setString(
                    9,
                    user.getCredentials().getOneTimePreKeys().get(1) == null ? "" : user.getCredentials().getOneTimePreKeys().get(1)
            );
            statement.setString(
                    10,
                    user.getCredentials().getOneTimePreKeys().get(2) == null ? "" : user.getCredentials().getOneTimePreKeys().get(2)
            );
            statement.setString(
                    11,
                    user.getCredentials().getOneTimePreKeys().get(3) == null ? "" : user.getCredentials().getOneTimePreKeys().get(3)
            );
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
                    String sql = "UPDATE users SET username = ?, password = ?, identitykey = ?, signedprekey = ?, prekeysignature = ?, otpkey1 = ?, otpkey2 = ?, otpkey3 = ?, otpkey4 = ? WHERE id = ?";
                    PreparedStatement statement = dbConnection.prepareStatement(sql);
                    statement.setString(1, credentials.getUsername());
                    statement.setString(2, credentials.getPassword());
                    statement.setString(3, credentials.getIdentityKey());
                    statement.setString(4, credentials.getSignedPreKey());
                    statement.setString(5, credentials.getPreKeySignature());
                    statement.setString(6, credentials.getOneTimePreKeys().get(0));
                    statement.setString(7, credentials.getOneTimePreKeys().get(1));
                    statement.setString(8, credentials.getOneTimePreKeys().get(2));
                    statement.setString(9, credentials.getOneTimePreKeys().get(3));
                    statement.setString(10, userId);
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