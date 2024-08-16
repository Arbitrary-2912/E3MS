package system;

import java.util.List;
import java.util.Set;

/**
 * Represents a conversation in the system.
 */
public class Conversation {
    public Set<User> participants;
    public List<Message> messages;

    /**
     * Constructs a conversation with the given participants and messages.
     *
     * @param participants the participants
     * @param messages     the messages
     */
    public Conversation(Set<User> participants, List<Message> messages) {
        this.participants = participants;
        this.messages = messages;
    }

    /**
     * Gets the participants.
     *
     * @return the participants
     */
    public Set<User> getParticipants() {
        return participants;
    }

    /**
     * Sets the participants.
     *
     * @param participants the participants
     */
    public void setParticipants(Set<User> participants) {
        this.participants = participants;
    }

    /**
     * Gets the messages.
     *
     * @return the messages
     */
    public List<Message> getMessages() {
        return messages;
    }

    /**
     * Sets the messages.
     *
     * @param messages the messages
     */
    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    /**
     * Adds a message to the conversation.
     *
     * @param message the message
     */
    public void addMessage(Message message) {
        messages.add(message);
    }

    /**
     * Removes a message from the conversation.
     *
     * @param message the message
     */
    public void removeMessage(Message message) {
        messages.remove(message);
    }

    /**
     * Adds a participant to the conversation.
     *
     * @param participant the participant
     */
    public void addParticipant(User participant) {
        participants.add(participant);
    }

    /**
     * Removes a participant from the conversation.
     *
     * @param participant the participant
     */
    public void removeParticipant(User participant) {
        participants.remove(participant);
    }

    /**
     * Gets the most recent message in the conversation.
     *
     * @param count the number of messages to get
     * @return the most recent message
     */
    public List<Message> getMostRecentMessage(int count) {
        return messages.subList(messages.size() - count, messages.size());
    }

    /**
     * Gets the number of messages in the conversation.
     *
     * @return the number of messages
     */
    public int getMessageCount() {
        return messages.size();
    }

    /**
     * Gets the number of participants in the conversation.
     *
     * @return the number of participants
     */
    public int getParticipantCount() {
        return participants.size();
    }

    /**
     * Checks if the conversation is equal to another object.
     * @return true if the conversation is equal to the other object, false otherwise
     */
    public int hashCode() {
        return participants.hashCode();
    }

    /**
     * Checks if the conversation is equal to another object.
     * @return true if the conversation is equal to the other object, false otherwise
     */
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Conversation)) {
            return false;
        }
        Conversation other = (Conversation) obj;
        return participants.equals(other.participants) && messages.equals(other.messages);
    }
}
