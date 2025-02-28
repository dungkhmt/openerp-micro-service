import axios from "axios";
import { config } from "config/constant";
import keycloak from "config/keycloak";
import { infoNoti, wifiOffNotify } from "./utils/notification";

export const isFunction = (func) =>
  typeof func === "function" || func instanceof Function;

const axiosInstance = axios.create({
  baseURL: config.url.API_URL,
  headers: { "Content-Type": "application/json" },
});

const wifiOffNotifyToastId = "cannot connect to server";

export const bearerAuth = (token) => `Bearer ${token}`;

const handleUnauthorized = () => {
  keycloak.login();
};

const handleForbidden = (errorHandlers, error) => {
  if (isFunction(errorHandlers?.[403])) {
    errorHandlers[403](error);
  } else {
    infoNoti("Bạn cần được cấp quyền để thực hiện hành động này.");
  }
};

const handleRequestError = (error, errorHandlers) => {
  console.log(error);

  if (error.response) {
    const { status } = error.response;

    if (status === 401) return handleUnauthorized();
    if (status === 403) return handleForbidden(errorHandlers, error);

    // For 410 errors, use the custom handler if provided
    if (status === 410) {
      // Always throw the error to ensure it propagates
      throw error;
    }

    if (isFunction(errorHandlers?.[status])) {
      errorHandlers[status](error);
    } else if (isFunction(errorHandlers?.rest)) {
      errorHandlers.rest(error);
    }
  } else if (error.request) {
    if (isFunction(errorHandlers?.noResponse)) {
      errorHandlers.noResponse(error);
    }
    wifiOffNotify(wifiOffNotifyToastId);
  } else {
    console.error("Request setup error:", error.message);
  }


};

export async function request(
  method,
  url,
  successHandler,
  errorHandlers,
  data,
  config = {},
  controller,
) {
  if (!keycloak.authenticated) {
    keycloak.login({
      redirectUri: window.location.origin + window.location.pathname,
    });
    return;
  }

  try {
    await keycloak.updateToken(70);
  } catch (error) {
    console.error("Failed to refresh token:", error);
    return handleUnauthorized();
  }

  // Ensure config is an object to avoid null.headers access
  const safeConfig = config || {};
  
  const headers = {
    authorization: bearerAuth(keycloak.token),
    ...(safeConfig.headers || {})
  };

  if (safeConfig.headers?.["Content-Type"] === "multipart/form-data") {
    headers["Content-Type"] = "multipart/form-data";
  }

  const options = {
    method: method.toLowerCase(),
    url,
    data,
    ...safeConfig,
    headers,
    signal: controller?.signal,
  };

  try {
    const res = await axiosInstance.request(options);
    if (isFunction(successHandler)) successHandler(res);
    return res;
  } catch (error) {
    return handleRequestError(error, errorHandlers);
  }
}
