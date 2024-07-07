const axios = require('axios');

const postUsername = (username, password) => {
    axios.post('http://localhost:8080/', {
        command: "verifyPassword",
        userId: username,
        password: password
    })
        .then((response) => {
            console.log(response.data);
        })
        .catch((error) => {
            console.error(error);
        });
};

postUsername("username", "password");