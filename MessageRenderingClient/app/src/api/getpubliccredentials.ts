import axios from "axios";

export const getPublicCredentials = async (userId: string) => {
    try {
        const response = await axios.post('http://localhost:8080/', {
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