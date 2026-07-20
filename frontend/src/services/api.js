import API from "../constants/apiConstants";

async function request(endpoint, options = {}) {

    const response = await fetch(`${API.BASE_URL}${endpoint}`, {
        headers: {
            "Content-Type": "application/json",
            ...options.headers
        },
        ...options
    });

    if (!response.ok) {
        throw new Error(`HTTP Error : ${response.status}`);
    }

    return response.json();
}

export default request;