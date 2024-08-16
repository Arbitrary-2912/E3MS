import axios from 'axios';
import { Credentials, User } from "../data/user.ts";
import {baseEndpoint} from "./apiconfig.ts";

export const getUsers = async (): Promise<Map<string, string>> => {
    try {
        const response = await axios.post(baseEndpoint, {
            command: "getUsers"
        });


        let usersData;
        if (response.data && response.data.response) {
            try {
                usersData = JSON.parse(response.data.response);
            } catch (parseError) {
                console.error('Error parsing JSON:', parseError);
                return new Map();
            }
        } else {
            console.error('Unexpected data format:', response.data);
            return new Map();
        }

        // Check if usersData is an array
        if (!Array.isArray(usersData)) {
            console.error('Unexpected data format:', usersData);
            return new Map();
        }

        // Map the parsed data to User objects
        const users = await Promise.all(usersData.map(async (userData: any) => {
            const credentials = new Credentials(
                userData.credentials.username,
                userData.credentials.password
            );

            return new User(userData.id, userData.name, credentials);
        }));

        // Extract usernames and return as a Map
        const userIdNameMap = new Map<string, string>();
        users.forEach(user => {
            userIdNameMap.set(user.name, user.id);
        });

        return userIdNameMap;
    } catch (error) {
        console.error("Error fetching users:", error);
        return new Map();
    }
};
