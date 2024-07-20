import axios from "axios";
import {Message, MessageData, MetaData} from "../data/message.ts";
import {User} from "../data/user.ts";

export const getRecentMessages = async (user: User) : Promise<Message[]> => {
    return axios.post('http://localhost:8080/', {
        command: "getRecentMessages",
        user: user
    })
        .then((response: any) => {
            const messageArray = JSON.parse(response.data.response);
            return messageArray.map((msg: any) => {
                const metaData = new MetaData(
                    msg.metaData.id,
                    msg.metaData.sender,
                    msg.metaData.receiver,
                    msg.metaData.timestamp
                );
                const messageData = new MessageData(msg.messageData.message);

                return new Message(metaData, messageData);
            });
        })
        .catch((error: any) => {
            console.error(error);
            return [];
        });

};