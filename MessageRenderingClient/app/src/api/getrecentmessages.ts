import axios from "axios";

export const getRecentMessages = () => {
    axios.get('http://localhost:8080/', {
        params: {
            command: "getRecentMessages"
        }
    })
        .then((response: any) => {
            console.log(response.data);
        })
        .catch((error: any) => {
            console.error(error);
        });
};