import axios from "axios";
import {baseEndpoint} from "./apiconfig.ts";

export const deleteUser = (userId: string) => {
    axios.post(baseEndpoint, {
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