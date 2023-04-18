import base64 from "base-64";
import {request} from "../api";
import {authState} from "../state/AuthState";
import {menuState} from "../state/MenuState";
import {notificationState} from "../state/NotificationState";
import {errorNoti} from "../utils/notification";

export const LOGIN_REQUESTING = "LOGIN_REQUESTING";
export const LOGIN_SUCCESS = "LOGIN_SUCCESS";
export const LOGOUT_SUCCESS = "LOGOUT_SUCCESS";
export const LOGIN_FAILURE = "LOGIN_FAILURE";
export const ERROR = "ERROR";

export const logout = () => {
  authState.merge({
    isAuthenticated: false,
    token: undefined,
    isValidating: false,
  });
  menuState.permittedFunctions.set(new Set());
  notificationState.merge({
    notifications: undefined,
    numUnRead: 0,
    hasMore: false,
  });

  return (dispatch, getState) => {
    dispatch(logoutSuccessfully());

    // Don't care if this request success or not.
    dispatch(requesting()); // create a action
    request("get", "/logout", undefined, {
      onError: () => dispatch(failed()),
      401: () => {},
    });
  };
};

export const login = (
  username,
  password,
  onLoginSuccessfully,
  onLoginFailed
) => {
  return (dispatch) => {
    dispatch(requesting()); // create a action

    request(
      "get",
      `/`,
      (res) => {
        const token = res.headers["x-auth-token"];

        authState.merge({
          isAuthenticated: true,
          token: token,
          isValidating: false,
        });
        dispatch(success(token));
        onLoginSuccessfully();
      },
      {
        401: (error) => {
          dispatch(failed(true, "Username or password is incorrect!!"));
          errorNoti("Tài khoản hoặc mật khẩu không đúng");
        },
        rest: (e) => {
          dispatch(failed());
        },
        ...onLoginFailed,
      },
      {},
      {
        headers: {
          Authorization: "Basic " + base64.encode(username + ":" + password),
          "X-Auth-Token": undefined,
        },
      }
    );
  };
};

const requesting = () => {
  return {
    type: LOGIN_REQUESTING,
  };
};

export const failed = (errorState = false, errorMsg = null) => {
  return {
    type: LOGIN_FAILURE,
    errorState: errorState,
    errorMsg: errorMsg,
  };
};

export const success = (token) => {
  return {
    type: LOGIN_SUCCESS,
    token: token,
  };
};
const logoutSuccessfully = (token) => {
  return {
    type: LOGOUT_SUCCESS,
  };
};

export const error = (errorState = false, errorMsg = null) => {
  console.log(errorMsg);
  return {
    type: ERROR,
    errorState: errorState,
    errorMsg: errorMsg,
  };
};
