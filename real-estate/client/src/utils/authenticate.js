import axios from "axios";

export const setAuthorizationToRequest = (accessToken) => {
    if (!accessToken) {
        return delete axios.defaults.headers.common['Authorization'];
    }
    axios.defaults.headers.common['Authorization'] = `Bearer ${accessToken}`;
};
