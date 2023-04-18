import {combineReducers} from "redux";
import auth from "./auth";
import error from "./error";
import screenSecurity from "./screen";
import classReducer from "./classReducer";

export default combineReducers({
  auth,
  // menu,
  error,
  screenSecurity,
  class: classReducer,
});
