import axios from "axios";
import {Message} from "../data/message.ts";

export const addMessage = (message: Message) => {
    axios.post('http://localhost:8080/', {
        command: "addMessage",
        message: message
    })
        .then((response: any) => {
            console.log(response.data);
        })
        .catch((error: any) => {
            console.error(error);
        });
};