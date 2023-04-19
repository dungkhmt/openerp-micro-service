import {connect} from "react-redux";
import {login} from "../action";
import {getScrSecurInfo} from "../action/Screen";
import SignIn from "../component/SignIn";

const mapStateToProps = (state1) => ({
  // query de lay ra
  isAuthenticated: state1.auth.isAuthenticated,
  isRequesting: state1.auth.isRequesting,
  errorState: state1.auth.errorState,
  errorMsg: state1.auth.errorMsg,
});

const mapDispatchToProps = (dispatch) => ({
  requestLogin: (username, password, onLoginSuccessfully, onLoginFailed) =>
    dispatch(login(username, password, onLoginSuccessfully, onLoginFailed)), // truyen action login thanh props login, create action
  getScreenSecurityInfo: (history) => dispatch(getScrSecurInfo(history)),
});

export default connect(mapStateToProps, mapDispatchToProps)(SignIn);
// truyen vao props mapStateToProps vao component SignIn, trong component nay co the access
// props.isAuthenticated va props.isRequesting
// state1.auth duoc dinh nghia trong reducers.auth

// truyen props mapDispatchToProps (trong do co function la login) vao component SignIn
// trong component SignIn co the call props.requestLogin(username, password), khi call ham nay thi se call
// login(username,password) cua "../action"
//
