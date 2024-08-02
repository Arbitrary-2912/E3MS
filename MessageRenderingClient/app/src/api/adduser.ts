import axios from "axios";
import {User} from "../data/user.ts";

export const addUser = (user: User) : Promise<boolean> => {
    const publicUserInfo = user.getPublicInfo();

    return axios.post('http://localhost:8080/', {
        command: "addUser",
        user: publicUserInfo
    })
        .then((response: any) => {
            console.log(response.data);
            return true;
        })
        .catch((error: any) => {
            console.error(error);
            return false;
        });
};