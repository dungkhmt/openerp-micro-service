import axios from "axios";
import { config } from "../../config/config";
import keycloak from "../../config/keycloak";

const baseURL = config.url.API_URL;

const privateClient = axios.create({
  baseURL,
  paramsSerializer: (params) => {
    return Object.entries(params)
      .map(([key, value]) => `${key}=${value}`)
      .join("&");
  },
});

privateClient.defaults.headers.common["Content-Type"] = "application/json";

privateClient.interceptors.request.use(
  async (config) => {
    let access_token = keycloak.token;
    if (access_token) {
      config.headers = {
        ...config.headers,
        Authorization: `Bearer ${access_token}`,
      };
      return config;
    } else {
      keycloak.login();
    }
  },
  (error) => Promise.reject(error)
);

export default privateClient;
