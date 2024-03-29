import axios from "axios";

const urlPrefix = 'http://localhost:2805';

export default class BaseRequest {
    async get(url, params) {
        try {
            const config = {
                params,
            };
            const response = await axios.get(urlPrefix + url, config);
            return this._responseHandler(response);
        } catch (error) {
            return this._errorHandler(error);
        }
    }

    async post(url, data) {
        try {
            const response = await axios.post(urlPrefix + url, data);
            return this._responseHandler(response);
        } catch (error) {
            return this._errorHandler(error);
        }
    }

    async _responseHandler(response) {
        return response.data;
    }

    async _errorHandler(error) {
        console.log(error);
    }
}