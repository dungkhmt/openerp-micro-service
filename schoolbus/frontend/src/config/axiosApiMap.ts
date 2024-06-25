import axios from 'axios';
import Cookies from 'js-cookie';
import { camelCase } from 'lodash';

const api_key = "5b3ce3597851110001cf624835ea0f4836b24bcc8faea58c6a8a4c7d";

const apiMap = axios.create({
    baseURL: "https://api.openrouteservice.org",
});

apiMap.interceptors.request.use((config) => {
    if (config.method === 'get') {
    config.params = {
        ...config.params,
        api_key: api_key
    }
    } else {
    config.headers['Authorization'] = `${api_key}`;
    }

    if (config.data) {
        config.data = toSnakeCase(config.data);
    }

    return config;
}, (error) => {
    return Promise.reject(error);
});

apiMap.interceptors.response.use((response) => {
    response.data = toCamelCase(response.data);
    return response;
}, async (error) => {
    return Promise.reject(error);
});

function toCamelCase(obj: any): any {
    if (Array.isArray(obj)) {
        return obj.map(v => toCamelCase(v));
    } else if (obj !== null && obj !== undefined && obj.constructor === Object) {
        return Object.fromEntries(
            Object.entries(obj).map(([k, v]) => [camelCase(k), toCamelCase(v)])
        );
    } else {
        return obj;
    }
}

function toSnakeCase(obj: any): any {
    if (Array.isArray(obj)) {
        return obj.map(v => toSnakeCase(v));
    } else if (obj !== null && obj !== undefined && obj.constructor === Object) {
        return Object.fromEntries(
            Object.entries(obj).map(([k, v]) => [k.replace(/[A-Z]/g, letter => `_${letter.toLowerCase()}`), toSnakeCase(v)])
        );
    } else {
        return obj;
    }
}

export default apiMap;