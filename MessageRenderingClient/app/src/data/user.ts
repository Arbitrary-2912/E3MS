export class User {
    credentials: Credentials;
    id: string;
    name: string;

    constructor(credentials: Credentials, id: string, name: string) {
        this.credentials = credentials;
        this.id = id;
        this.name = name;
    }
}

class Credentials {
    username: string;
    password: string;

    constructor(username: string, password: string) {
        this.username = username;
        this.password = password;
    }
}