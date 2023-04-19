import {LOGIN_FAILURE, LOGIN_REQUESTING, LOGIN_SUCCESS, LOGOUT_SUCCESS,} from "../action/Auth";

const auth = (
  state = {
    token: null,
    isAuthenticated: false,
    isRequesting: false,
    errorState: false,
    errorMsg: null,
  },
  action
) => {
  switch (action.type) {
    case LOGOUT_SUCCESS:
      return Object.assign({}, state, {
        token: null,
        isAuthenticated: false,
        isRequesting: false,
      });

    case LOGIN_REQUESTING:
      return Object.assign({}, state, {
        isRequesting: true,
        errorState: false,
        errorMsg: null,
      }); // lay data object cu, tao object moi, assign data cho object moi
    case LOGIN_SUCCESS:
      return Object.assign({}, state, {
        token: action.token,
        isAuthenticated: true,
        isRequesting: false,
      });
    case LOGIN_FAILURE:
      return Object.assign({}, state, {
        isAuthenticated: false,
        isRequesting: false,
        errorState: action.errorState,
        errorMsg: action.errorMsg,
      });

    default:
      return state;
  }
};

export default auth;
