import axios from "axios";

export const getUsers = () => {
    axios.post('http://localhost:8080/', {
        command: "getUsers"
    })
        .then((response: any) => {
            console.log(response.data);
        })
        .catch((error: any) => {
            console.error(error);
        });
};