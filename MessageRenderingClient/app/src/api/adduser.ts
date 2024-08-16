import axios from 'axios';
import { User } from '../data/user';
import { Credentials } from '../data/user.ts';

export const addUser = async (user: User): Promise<boolean> => {
    try {
        const publicUserInfo = await getPublicInfo(user);

        const response = await axios.post('http://localhost:8080/', {
            command: "addUser",
            user: publicUserInfo
        });

        console.log(response.data);
        return true;
    } catch (error) {
        console.error(error);
        return false;
    }
}

async function getPublicInfo(user: User) {
    const publicKeyString = await user.publicKeyToString();

    return {
        credentials: {
            username: user.credentials.username,
            password: user.credentials.password,
            publicKey: publicKeyString,
        },
        id: user.id,
        name: user.name,
    };
}

