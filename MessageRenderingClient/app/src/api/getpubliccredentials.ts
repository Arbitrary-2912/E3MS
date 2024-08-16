import axios from "axios";
import {baseEndpoint} from "./apiconfig.ts";

export const getPublicCredentials = async (userId: string) => {
    try {
        const response = await axios.post(baseEndpoint, {
            command: "getPublicCredentials",
            userId: userId
        })
        const data = JSON.parse(response.data.response);
        return data.publicKey;
    } catch (error) {
        console.error(error);
        return [];
    }
};