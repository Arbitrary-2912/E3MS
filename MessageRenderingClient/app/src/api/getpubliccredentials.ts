import axios from "axios";
import { PublicCredentials } from "../data/user.ts";

export const getPublicCredentials = async (userId: string) => {
    try {
        const response = await axios.post('http://localhost:8080/', {
            command: "getPublicCredentials",
            userId: userId
        })

        return JSON.parse(response.data.response).map((msg: any) => {
            return new PublicCredentials(
                msg.publicCredentials.identityKey,
                msg.publicCredentials.ephemeralKey,
                msg.publicCredentials.signedPreKey
            );
        });
    } catch (error) {
        console.error(error);
        return [];
    }

};