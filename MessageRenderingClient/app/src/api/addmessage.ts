import axios from "axios";
import { Message } from "../data/message.ts";
import {baseEndpoint} from "./apiconfig.ts";

export const addMessage = (message: Message) => {
    // Convert the message to a server-ready object
    const serverMessage = message.toServerObject();

    axios.post(baseEndpoint, {
        command: "addMessage",
        message: serverMessage
    })
        .then((response: any) => {
            console.log(response.data);
        })
        .catch((error: any) => {
            console.error(error);
        });
};
