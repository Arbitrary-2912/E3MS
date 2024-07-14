import axios from "axios";

export const verifyUsername = (username: string, password: string) => {
    axios.post('http://localhost:8080/', {
        command: "verifyPassword",
        userId: username,
        password: password
    })
        .then((response: any) => {
            console.log(response.data);
        })
        .catch((error: any) => {
            console.error(error);
        });
};
