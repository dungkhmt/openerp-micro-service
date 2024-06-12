import React, { useState } from "react";
import "./Login.css";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faFacebookF } from "@fortawesome/free-brands-svg-icons";
import { useNavigate } from "react-router-dom";
import AccountRequest from "../../services/AccountRequest";
import { useDispatch } from "react-redux";
import { toast, ToastContainer } from "react-toastify";
import { login_success } from "../../store/auth";
import { setAuthorizationToRequest } from "../../utils/authenticate";
import {
  Button,
  Dialog,
  TextInput,
  Group,
  Loader,
  PasswordInput,
  Text,
  Divider,
  Anchor,
} from "@mantine/core";
import { hasLength, isEmail, matches, useForm } from "@mantine/form";

const Login = () => {
  const navigate = useNavigate();
  const dispatch = useDispatch();
  const [openedReset, setOpenedReset] = useState(false);
  const [email, setEmail] = useState("");

  const [showSignUp, setShowSignUp] = useState(false);

  const formSignup = useForm({
    model: "uncontrolled",
    initialValues: {
      name: "",
      email: "",
      password: "",
    },
    validate: (values) => ({
      name:
        values.name.length < 5 || values.name.length > 20
          ? "5 đến 20 ký tự"
          : null,
      email: /^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$/.test(
        values.email,
      )
        ? null
        : "Sai định dạng email",
      password: /^[a-zA-Z0-9]{8,16}$/.test(values.password)
        ? null
        : "Yêu cầu 8 đến 16 ký tự",
    }),
  });

  const formLogin = useForm({
    model: "uncontrolled",
    initialValues: {
      email: "",
      password: "",
    },
    validate: (values) => ({
      email: /^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$/.test(
        values.email,
      )
        ? null
        : "Sai định dạng email",
      password: /^[a-zA-Z0-9]{8,16}$/.test(values.password)
        ? null
        : "Yêu cầu 8 đến 16 ký tự",
    }),
  });

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

  const handleSignUp = (values) => {
    setLoading(true);
    const accountRequest = new AccountRequest();
    accountRequest
      .signup({
        name: values.name,
        email: values.email,
        password: values.password,
      })
      .then((response) => {
        const statusCode = response.code;
        if (statusCode === 200) {
          toast.success(response.data);
          formSignup.setValues({
            name: "",
            email: "",
            password: "",
          });
        } else {
          toast.error(response.data.message);
        }
      })
      .finally(() => setLoading(false));
  };

  const handleLogin = (values) => {
    const accountRequest = new AccountRequest();
    accountRequest
      .login({
        email: values.email,
        password: values.password,
      })
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
            <form
              // onSubmit={handleSignUp}
              onSubmit={(event) => {
                event.preventDefault();
                formSignup.onSubmit(handleSignUp)(event);
              }}
            >
              <h1>Tạo tài khoản</h1>
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
              <Divider
                label="hoặc tiếp tực với email"
                labelPosition="center"
                my="sm"
              />

              <TextInput
                style={{
                  width: "75%",
                  // marginBottom: "5px",
                }}
                placeholder="Tên"
                title="2-20 ký tự"
                key={formSignup.key("name")}
                {...formSignup.getInputProps("name")}
              />
              <TextInput
                // type="email"
                style={{
                  width: "75%",
                  // marginBottom: "5px",
                }}
                placeholder="Email"
                key={formSignup.key("email")}
                {...formSignup.getInputProps("email")}
              />
              <PasswordInput
                style={{
                  width: "75%",
                }}
                placeholder="Mật khẩu"
                title="8-16 ký tự"
                key={formSignup.key("password")}
                {...formSignup.getInputProps("password")}
              />

              <Button
                style={{
                  marginTop: "10px",
                  backgroundColor: "#512da8",
                  color: "#fff",
                  textTransform: "uppercase",
                  border: "1px solid transparent",
                }}
                type="submit"
                radius="lg"
              >
                Đăng ký
              </Button>
            </form>
          )}
        </div>
      ) : (
        <div className="form-container sign-in">
          <form
            onSubmit={(event) => {
              event.preventDefault();
              formLogin.onSubmit(handleLogin)(event);
            }}
          >
            <h1>Đăng nhập</h1>
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
            <Divider
              label="hoặc tiếp tục với email"
              labelPosition="center"
              my="sm"
            />
            <TextInput
              // type="email"
              style={{
                width: "75%",
                // marginBottom: "5px",
              }}
              placeholder="Email"
              key={formLogin.key("email")}
              {...formLogin.getInputProps("email")}
            />
            <PasswordInput
              style={{
                width: "75%",
              }}
              placeholder="Mật khẩu"
              title="8-16 ký tự"
              key={formLogin.key("password")}
              {...formLogin.getInputProps("password")}
            />

            <Group w={"70%"} justify="flex-end" mt="lg">
              <Anchor
                component="button"
                size="sm"
                onClick={(event) => {
                  event.preventDefault();
                  setOpenedReset(true);
                }}
              >
                Quên mật khẩu?
              </Anchor>
            </Group>
            <Button
              style={{
                marginTop: "10px",
                backgroundColor: "#512da8",
                color: "#fff",
                textTransform: "uppercase",
                border: "1px solid transparent",
              }}
              type="submit"
              radius="lg"
            >
              Đăng nhập
            </Button>
          </form>
        </div>
      )}
      <div className="toggle-container">
        <div className="toggle">
          <div className="toggle-panel toggle-left">
            <h1>Chào mừng bạn trở lại</h1>
            {/*<p>Sử dụng </p>*/}
            <Button
              color="#fff"
              variant="outline"
              id="login"
              onClick={handleToLogin}
              radius="lg"
            >
              Đăng nhập
            </Button>
          </div>
          <div className="toggle-panel toggle-right">
            <h1>Chào bạn!</h1>
            <p
              style={{
                fontSize: "14px",
                lineHeight: "20px",
                letterSpacing: "0.3px",
                margin: "20px 0",
              }}
            >
              Đăng ký với thông tin cá nhân để có thể trải nghiệm sản phẩm của
              chúng tôi
            </p>
            <Button
              color="#fff"
              variant="outline"
              radius="lg"
              style={{
                fontSize: "12px",
              }}
              onClick={handleToSignUp}
            >
              Đăng ký
            </Button>
            <p>hoặc</p>
            <Button
              color="#fff"
              variant="outline"
              onClick={() => navigate("/")}
              radius="lg"
              style={{
                fontSize: "12px",
              }}
            >
              Trang chủ
            </Button>
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
        <Text size="sm" mb="xs" fw={500}>
          Lấy lại mật khẩu
        </Text>
        <Group align="flex-end">
          <TextInput
            placeholder="điền email đã đăng ký"
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
