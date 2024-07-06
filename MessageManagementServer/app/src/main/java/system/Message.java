package system;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Class that represents a message.
 */
public class Message {
    @SerializedName("metaData")
    private MetaData metaData;
    @SerializedName("messageData")
    private MessageData messageData;

    /**
     * Constructor for the Message class.
     *
     * @param metaData    The metadata of the message.
     * @param messageData The data of the message.
     */
    public Message(MetaData metaData, MessageData messageData) {
        this.metaData = metaData;
        this.messageData = messageData;
    }

    /**
     * Gets the metadata of the message.
     *
     * @return The metadata of the message.
     */
    public MetaData getMetaData() {
        return metaData;
    }

    /**
     * Sets the metadata of the message.
     *
     * @param metaData The metadata of the message.
     */
    public void setMetaData(MetaData metaData) {
        this.metaData = metaData;
    }

    /**
     * Gets the data of the message.
     *
     * @return The data of the message.
     */
    public MessageData getMessageData() {
        return messageData;
    }

    /**
     * Sets the data of the message.
     *
     * @param messageData The data of the message.
     */
    public void setMessageData(MessageData messageData) {
        this.messageData = messageData;
    }

    /**
     * Computes the hash code of the message.
     *
     * @return The hash code of the message.
     */
    @Override
    public int hashCode() {
        return (int) (metaData.hashCode() + 31L * messageData.hashCode());
    }

    /**
     * Class that represents the metadata of a message.
     */
    public static class MetaData {
        @SerializedName("id")
        private String id;
        @SerializedName("sender")
        private String sender;
        @SerializedName("receiver")
        private List<String> receiver;
        @SerializedName("timestamp")
        private String timestamp;

        /**
         * Constructor for the MetaData class.
         *
         * @param id        The id of the message.
         * @param sender    The sender of the message.
         * @param receiver  The receiver of the message.
         * @param timestamp The timestamp of the message.
         */
        public MetaData(String id, String sender, List<String> receiver, String timestamp) {
            this.id = id;
            this.sender = sender;
            this.receiver = receiver;
            this.timestamp = timestamp;
        }

        /**
         * Gets the id of the message.
         *
         * @return The id of the message.
         */
        public String getId() {
            return id;
        }

        /**
         * Sets the id of the message.
         *
         * @param id The id of the message.
         */
        public void setId(String id) {
            this.id = id;
        }

        /**
         * Gets the sender of the message.
         *
         * @return The sender of the message.
         */
        public String getSender() {
            return sender;
        }

        /**
         * Sets the sender of the message.
         *
         * @param sender The sender of the message.
         */
        public void setSender(String sender) {
            this.sender = sender;
        }

        /**
         * Gets the receiver of the message.
         *
         * @return The receiver of the message.
         */
        public List<String> getReceiver() {
            return receiver;
        }

        /**
         * Sets the receiver of the message.
         *
         * @param receiver The receiver of the message.
         */
        public void setReceiver(List<String> receiver) {
            this.receiver = receiver;
        }

        /**
         * Gets the timestamp of the message.
         *
         * @return The timestamp of the message.
         */
        public String getTimestamp() {
            return timestamp;
        }

        /**
         * Sets the timestamp of the message.
         *
         * @param timestamp The timestamp of the message.
         */
        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }

        /**
         * Computes the hash code of the metadata.
         *
         * @return The hash code of the metadata.
         */
        public int hashCode() {
            return (int) (17 + sender.hashCode() + 31L * receiver.hashCode() + 31L * 31L * timestamp.hashCode());
        }
    }

    /**
     * Class that represents the data of a message.
     */
    public static class MessageData {
        @SerializedName("message")
        private String message;

        /**
         * Constructor for the MessageData class.
         *
         * @param message The message data.
         */
        public MessageData(String message) {
            this.message = message;
        }

        /**
         * Gets the message data.
         *
         * @return The message data.
         */
        public String getMessage() {
            return message;
        }

        /**
         * Sets the message data.
         *
         * @param message The message data.
         */
        public void setMessage(String message) {
            this.message = message;
        }

        /**
         * Computes the hash code of the message data.
         *
         * @return The hash code of the message data.
         */
        public int hashCode() {
            return message.hashCode();
        }
    }
}
