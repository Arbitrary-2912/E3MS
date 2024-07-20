import axios from "axios";

export const deleteMessage = (messageId: string) => {
    axios.post('http://localhost:8080/', {
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
