package system;

import com.google.gson.annotations.SerializedName;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;

/**
 * Class that represents a message.
 */
public class Message implements Comparable<Message> {
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
     * Gets the participants' ids of the message
     * @return The participants of the message.
     */
    public List<String> getParticipantIds() {
        List<String> participantIds = metaData.getReceiver();
        participantIds.add(metaData.getSender());
        return participantIds;
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
        @SerializedName("username")
        private String username;
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
         * @param name      The name of the message.
         * @param sender    The sender of the message.
         * @param receiver  The receiver of the message.
         * @param timestamp The timestamp of the message.
         */
        public MetaData(String id, String username, String sender, List<String> receiver, String timestamp) {
            this.id = id;
            this.username = username;
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
         * Gets the username of the message.
         *
         * @return The username of the message.
         */
        public String getUsername() {
            return username;
        }

        /**
         * Sets the username of the message.
         *
         * @param username The name of the message.
         */
        public void setUsername(String username) {
            this.username = username;
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
        @SerializedName("cipherText")
        private String cipherText;

        @SerializedName("iv")
        private String iv;


        /**
         * Constructor for the MessageData class.
         *
         * @param cipherText The cipher text of the message.
         * @param iv        The initialization vector of the message.
         */
        public MessageData(String cipherText, String iv) {
            this.cipherText = cipherText;
            this.iv = iv;
        }

        /**
         * Gets the cipher text of the message.
         *
         * @return The cipher text of the message.
         */
        public String getCipherText() {
            return cipherText;
        }

        /**
         * Sets the cipher text of the message.
         *
         * @param cipherText The cipher text of the message.
         */
        public void setCipherText(String cipherText) {
            this.cipherText = cipherText;
        }

        /**
         * Gets the initialization vector of the message.
         *
         * @return The initialization vector of the message.
         */
        public String getIv() {
            return iv;
        }

        /**
         * Sets the initialization vector of the message.
         *
         * @param iv The initialization vector of the message.
         */
        public void setIv(String iv) {
            this.iv = iv;
        }
    }

    public int compareTo(Message o) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yyyy, h:mm:ss a");
        LocalDateTime thisTimestamp = LocalDateTime.parse(this.metaData.getTimestamp(), formatter);
        LocalDateTime otherTimestamp = LocalDateTime.parse(o.metaData.getTimestamp(), formatter);
        return thisTimestamp.compareTo(otherTimestamp);
    }

}
