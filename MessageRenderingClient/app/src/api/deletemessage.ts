import axios from "axios";
import {baseEndpoint} from "./apiconfig.ts";

export const deleteMessage = (messageId: string) => {
    axios.post(baseEndpoint, {
        command: "deleteMessage",
        messageId: messageId
    })
        .then((response: any) => {
            console.log(response.data);
        })
        .catch((error: any) => {
            console.error(error);
        });
};
