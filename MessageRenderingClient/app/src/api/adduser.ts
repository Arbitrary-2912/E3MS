import axios from "axios";
import {User} from "../data/user.ts";

export const addUser = (user: User) => {
    axios.post('http://localhost:8080/', {
        command: "addUser",
        user: user
    })
        .then((response: any) => {
            console.log(response.data);
        })
        .catch((error: any) => {
            console.error(error);
        });
};