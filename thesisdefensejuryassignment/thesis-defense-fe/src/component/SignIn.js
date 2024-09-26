import Button from "@mui/material/Button";
import CircularProgress from "@mui/material/CircularProgress";
import Link from "@mui/material/Link";
import {makeStyles} from "@material-ui/core/styles";
import TextField from "@mui/material/TextField";
import Typography from "@mui/material/Typography";
import React, {useState} from "react";
import {Redirect, useHistory} from "react-router";
import {NavLink} from "react-router-dom";
import {useRouteState} from "../state/RouteState";
import Box from "@mui/material/Box";

/*
function Copyright() {
  return (
    <Typography variant="body2" color="textSecondary" align="center">
      {"Copyright © "}
      <Link color="inherit" href="">
        Phạm Quang Dũng
      </Link>{" "}
      {new Date().getFullYear()}
      {"."}
    </Typography>
  );
}
*/
const useStyles = makeStyles((theme) => ({
  "@global": {
    body: {
      backgroundColor: theme.palette.common.white,
    },
  },
  paper: {
    maxWidth: 320,
    marginRight: 34,
    display: "flex",
    flexDirection: "column",
    alignItems: "center",
    textAlign: "center",
  },
  avatar: {
    margin: theme.spacing(1),
    width: theme.spacing(12),
    height: theme.spacing(8),
  },
  form: {
    width: "100%", // Fix IE 11 issue.
    marginTop: theme.spacing(1),
  },
  container: {
    display: "flex",
    alignItems: "center",
    height: "100vh",
    width: "100vw",
    flexDirection: "row-reverse",
  },
  image: {
    position: "absolute",
    width: "100vw",
    maxHeight: "100vh",
  },
  wrapper: {
    background: "white",
  },
  submitBtnWrapper: {
    // margin: theme.spacing(1),
    marginTop: 12,
    paddingBottom: 16,
    borderBottom: "1px solid gray",
    position: "relative",
  },
  submitBtn: {
    height: 40,

    width: "100%",
    borderRadius: 25,
    borderBottom: "1px solid lightgray",
    textTransform: "none",
  },
  buttonProgress: {
    // color: green[500],
    position: "absolute",
    top: "50%",
    left: "50%",
    marginTop: -8,
    marginLeft: -12,
  },
}));

export default function SignIn(props) {
  const history = useHistory();
  const classes = useStyles();

  //
  const {currentRoute} = useRouteState();

  //
  const [userName, setUserName] = useState(""); // new State (var) userName
  const [password, setPassword] = useState(""); // new State (var) password
  const [isTyping, setIsTyping] = useState(false);
  const [isRequesting, setIsRequesting] = useState(false);
  const [loggedInSuccessfully, setLoggedInSuccessfully] = useState(false);

  //
  const handleUserNameChange = (event) => {
    setIsTyping(true);
    setUserName(event.target.value);
  };

  const handlePasswordChange = (event) => {
    setIsTyping(true);
    setPassword(event.target.value);
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    setIsRequesting(true);
    setIsTyping(false);

    props.requestLogin(
      userName,
      password,
      () => {
        setLoggedInSuccessfully(true);
      },
      {
        onError: () => {
          setIsRequesting(false);
        },
      }
    );
  };

  if (loggedInSuccessfully) {
    props.getScreenSecurityInfo(history);

    if (currentRoute.get()) {
      history.replace(currentRoute.get());
      return null;
    } else
      return (
        <Redirect to={{pathname: "/", state: {from: history.location}}}/>
      );
  } else
    return (
      <Box className={classes.container}>
        <img
          alt="Welcome"
          src="/static/images/welcome.jpg"
          className={classes.image}
        />
        <Box className={classes.paper}>
          {/* <img
          // alt="Hust"
          // className={classes.avatar}
          // src={process.env.PUBLIC_URL + "/soict-logo.png"}
          /> */}
          <Typography
            component="h1"
            variant="h4"
            style={{position: "relative"}}
          >
            Đăng nhập
          </Typography>
          <form className={classes.form} onSubmit={handleSubmit} noValidate>
            {props.errorState === true && isTyping === false ? (
              <Typography variant="overline" display="block" color="error">
                {props.errorMsg}
              </Typography>
            ) : (
              ""
            )}
            <TextField
              variant="outlined"
              margin="normal"
              required
              fullWidth
              id="user"
              label="Username"
              name="user"
              onChange={handleUserNameChange}
              error={
                isTyping === false &&
                props.errorState !== null &&
                props.errorState === true
              }
              autoFocus
            />
            <TextField
              variant="outlined"
              margin="normal"
              required
              fullWidth
              name="password"
              label="Password"
              type="password"
              id="password"
              onChange={handlePasswordChange}
              error={
                isTyping === false &&
                props.errorState !== null &&
                props.errorState === true
              }
              autoComplete="current-password"
            />

            <Box className={classes.submitBtnWrapper}>
              <Button
                variant="contained"
                color="primary"
                disabled={isRequesting}
                type="submit"
                className={classes.submitBtn}
              >
                Login
              </Button>
              {isRequesting && (
                <CircularProgress
                  size={24}
                  className={classes.buttonProgress}
                />
              )}
              <Link
                component={NavLink}
                to={process.env.PUBLIC_URL + "/user/forgetpassword"}
                variant="body2"
                sx={{position: "relative", fontSize: "14px"}}
              >
                {"Forget password?"}
              </Link>
            </Box>

            {/* <Grid item xs>
                
                <Link href="#" variant="body2" style = {{position:"relative"}}>
                  Quên mật khẩu?
                </Link>
              </Grid> */}

            <Box sx={{display: "flex", justifyContent: "space-around", marginTop: "12px"}}>
              <Button
                variant="contained"
                color="success"
                href={process.env.PUBLIC_URL + "/user/register"}
                sx={{width: "72%"}}
              >
                Sign up
              </Button>
            </Box>
            <Box>
              <Typography
                component="h1"
                variant="h6"
                sx={{position: "relative", marginTop: "28px"}}
              >
                (Contact: dungkhmt@gmail.com)
              </Typography>
            </Box>
            {/* <Box mt={2} className={classes.cp}>
                <Copyright />
              </Box> */}
          </form>
        </Box>
      </Box>
    );
}
