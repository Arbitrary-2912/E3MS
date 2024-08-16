import axios from "axios";
import {Message, MessageData, MetaData} from "../data/message.ts";
import {InitServerInfo} from "rawr-x3dh";

export const getRecentMessages = async (userId: string): Promise<Message[]> => {
    try {
        const response = await axios.post('http://localhost:8080/', {
            command: "getRecentMessages",
            userId: userId
        });

        return JSON.parse(response.data.response).map((msg: any) => {
            const metaData = new MetaData(
                msg.metaData.id,
                msg.metaData.username,
                msg.metaData.sender,
                msg.metaData.receiver,
                msg.metaData.timestamp
            );
            const messageData = new MessageData(msg.messageData.cipherText, msg.messageData.iv);

            return new Message(metaData, messageData);
        });
    } catch (error) {
        console.error(error);
        return [];
    }
};