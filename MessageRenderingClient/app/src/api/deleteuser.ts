import axios from "axios";

export const deleteUser = (userId: string) => {
    axios.post('http://localhost:8080/', {
        command: "deleteUser",
        userId: userId
    })
        .then((response: any) => {
            console.log(response.data);
        })
        .catch((error: any) => {
            console.error(error);
        });
};