import axios from "axios";

export const verifyUsername = (username: string, password: string) : Promise<boolean> => {
    return axios.post('http://localhost:8080/', {
        command: "verifyPassword",
        userId: username,
        password: password
    })
        .then((response: any) => {
            console.log(response.data);
            return response.data.response === "true";
        })
        .catch((error: any) => {
            console.error(error);
            return false;
        });
};
