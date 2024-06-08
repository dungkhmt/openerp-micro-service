import React, { useState } from "react";
import "./Login.css";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faFacebookF, faGooglePlusG } from "@fortawesome/free-brands-svg-icons";
import { useNavigate } from "react-router-dom";
import AccountRequest from "../../services/AccountRequest";
import { useDispatch } from "react-redux";
import { toast, ToastContainer } from "react-toastify";
import { login_success } from "../../store/auth";
import { setAuthorizationToRequest } from "../../utils/authenticate";
import { useDisclosure } from "@mantine/hooks";
import { Button, Dialog, TextInput, Group, Loader } from "@mantine/core";

const Login = () => {
  const navigate = useNavigate();
  const dispatch = useDispatch();
  const [openedReset, setOpenedReset] = useState(false);
  const [email, setEmail] = useState("");

  const [showSignUp, setShowSignUp] = useState(false);
  const [dataLogin, setDataLogin] = useState({});
  const [dataSignUp, setDataSignUp] = useState({});

  const [loading, setLoading] = useState(false);

  const handleChangeLogin = (event) => {
    const name = event.target.name;
    const value = event.target.value;
    setDataLogin((values) => ({ ...values, [name]: value }));
  };

  const handleChangeSignUp = (event) => {
    const name = event.target.name;
    const value = event.target.value;
    setDataSignUp((values) => ({ ...values, [name]: value }));
  };

  const handleToSignUp = () => {
    setShowSignUp(true);
  };

  const handleToLogin = () => {
    setShowSignUp(false);
  };

  const handleSignUp = (event) => {
    event.preventDefault();
    setLoading(true);
    const accountRequest = new AccountRequest();
    accountRequest
      .signup(dataSignUp)
      .then((response) => {
        const statusCode = response.code;
        if (statusCode === 200) {
          toast.success(response.data);
          setDataSignUp({});
        } else {
          toast.error(response.data.message);
        }
      })
      .finally(() => setLoading(false));
  };

  const handleLogin = (event) => {
    event.preventDefault();
    const accountRequest = new AccountRequest();
    accountRequest
      .login(dataLogin)
      .then((response) => {
        const statusCode = response.code;
        if (statusCode === 200) {
          toast.success("Đăng nhập thành công");
          dispatch(login_success(response.data));
          setAuthorizationToRequest(response.data);
          navigate("/");
        } else {
          toast.error("Đăng nhập không thành công");
        }
      })
      .then();
  };

  const handleResetPass = () => {
    const accountRequest = new AccountRequest();
    close();
    accountRequest
      .reset_pass({
        email,
      })
      .then((response) => {
        if (response.code === 200) {
          toast.success(response.data);
          setOpenedReset(false);
          setEmail("");
        } else {
          toast.error(response.data.message);
        }
      });
  };
  return (
    <div
      className={showSignUp ? "login-container active" : "login-container"}
      id="container"
    >
      {showSignUp ? (
        <div className="form-container sign-up">
          {loading ? (
            <div
              className="flexColCenter"
              style={{
                height: "100%",
                marginTop: "auto",
              }}
            >
              <Loader color="blue" size="50" />
            </div>
          ) : (
            <form onSubmit={handleSignUp}>
              <h1>Create Account</h1>
              <div className="social-icons">
                <a
                  href="http://localhost:2805/oauth2/authorization/facebook"
                  className="icon"
                >
                  <FontAwesomeIcon
                    icon={faFacebookF}
                    className="fa-brands fa-facebook-f"
                  />
                </a>
              </div>
              <span>or use your email for registeration</span>
              <input
                name="name"
                type="text"
                placeholder="Tên"
                pattern="[A-Za-z0-9]{5-10}"
                title="5-10 ký tự"
                value={dataSignUp.name || ""}
                onChange={handleChangeSignUp}
              />
              <input
                name="email"
                type="email"
                placeholder="Email"
                value={dataSignUp.email || ""}
                onChange={handleChangeSignUp}
              />
              <input
                name="password"
                type="password"
                placeholder="Password"
                pattern="[A-Za-z0-9]{8-16}"
                title="8-16 ký tự"
                value={dataSignUp.password || ""}
                onChange={handleChangeSignUp}
              />
              <button type="submit">Sign Up</button>
            </form>
          )}
        </div>
      ) : (
        <div className="form-container sign-in">
          <form onSubmit={handleLogin}>
            <h1>Sign In</h1>
            <div className="social-icons">
              <a
                href="http://localhost:2805/oauth2/authorization/facebook"
                className="icon"
              >
                <FontAwesomeIcon
                  icon={faFacebookF}
                  className="fa-brands fa-facebook-f"
                />
              </a>
            </div>
            <span>or use your email password</span>
            <input
              name="email"
              type="email"
              placeholder="Email"
              value={dataLogin.email || ""}
              onChange={handleChangeLogin}
            />
            <input
              name="password"
              type="password"
              placeholder="Password"
              pattern="[A-Za-z0-9]{8-16}"
              title="8-16 ký tự"
              value={dataLogin.password || ""}
              onChange={handleChangeLogin}
            />
            <div
              onClick={() => setOpenedReset(true)}
              style={{
                cursor: "pointer",
              }}
            >
              Forget Your Password?
            </div>
            <button type="submit">Sign In</button>
          </form>
        </div>
      )}
      <div className="toggle-container">
        <div className="toggle">
          <div className="toggle-panel toggle-left">
            <h1>Welcome Back!</h1>
            <p>Enter your personal details to use all of site features</p>
            <button className="hidden" id="login" onClick={handleToLogin}>
              Sign In
            </button>
          </div>
          <div className="toggle-panel toggle-right">
            <h1>Hello, Friend!</h1>
            <p>
              Register with your personal details to use all of site features
            </p>
            <button className="hidden" id="register" onClick={handleToSignUp}>
              Sign Up
            </button>
            <p>Or</p>
            <button
              className="hidden"
              onClick={() => navigate("/")}
              style={{ marginTop: "5px" }}
            >
              Home
            </button>
          </div>
        </div>
      </div>

      <ToastContainer
        position="top-left"
        autoClose={3000}
        hideProgressBar={false}
        newestOnTop={false}
        closeOnClick
        rtl={false}
        pauseOnFocusLoss
        draggable
        pauseOnHover
      />

      <Dialog
        zIndex={1001}
        opened={openedReset}
        withCloseButton
        onClose={() => setOpenedReset(false)}
        size="lg"
        radius="md"
      >
        <Group align="flex-end">
          <TextInput
            placeholder="hello@gluesticker.com"
            style={{ flex: 1 }}
            onChange={(event) => setEmail(event.currentTarget.value)}
          />
          <Button onClick={handleResetPass} disabled={email === ""}>
            Gửi
          </Button>
        </Group>
      </Dialog>
    </div>
  );
};

export default Login;
