import axios from "axios";
import qs from "qs";
import { setAuthorizationToRequest } from "../utils/authenticate";

const urlPrefix = "http://localhost:2805";

export default class BaseRequest {
  constructor() {
    const token = localStorage.getItem("token");
    if (token !== "") {
      setAuthorizationToRequest(token);
    }
  }

  async get(url, params) {
    try {
      const config = {
        params,
        paramsSerializer: (params) => {
          return qs.stringify(params, { arrayFormat: "repeat" });
        },
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

  async put(url, data) {
    try {
      const response = await axios.put(urlPrefix + url, data);
      return this._responseHandler(response);
    } catch (error) {
      return this._errorHandler(error);
    }
  }

  async patch(url, data) {
    try {
      const response = await axios.patch(this.getUrlPrefix() + url, data);
      return this._responseHandler(response);
    } catch (error) {
      return this._errorHandler(error);
    }
  }

  async _responseHandler(response) {
    return response.data;
  }

  async _errorHandler(error) {
    // console.log(error);
    return error.response?.data;
  }
}