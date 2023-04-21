export const LOGIN_REQUESTING = "LOGIN_REQUESTING";
export const LOGIN_SUCCESS = "LOGIN_SUCCESS";
export const LOGOUT_SUCCESS = "LOGOUT_SUCCESS";
export const LOGIN_FAILURE = "LOGIN_FAILURE";

export const failed = (errorState = false, errorMsg = null) => {
  return {
    type: LOGIN_FAILURE,
    errorState: errorState,
    errorMsg: errorMsg,
  };
};
