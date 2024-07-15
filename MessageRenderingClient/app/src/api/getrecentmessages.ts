import axios from "axios";

export const getRecentMessages = () => {
    axios.post('http://localhost:8080/', {
        command: "getRecentMessages"
    })
        .then((response: any) => {
            console.log(response.data);
        })
        .catch((error: any) => {
            console.error(error);
        });
};