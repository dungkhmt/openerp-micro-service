import {CircularProgress, FormHelperText} from "@material-ui/core";
import Button from "@material-ui/core/Button";
import Container from "@material-ui/core/Container";
import CssBaseline from "@material-ui/core/CssBaseline";
import FormControl from "@material-ui/core/FormControl";
import Grid from "@material-ui/core/Grid";
import IconButton from "@material-ui/core/IconButton";
import InputAdornment from "@material-ui/core/InputAdornment";
import InputLabel from "@material-ui/core/InputLabel";
import OutlinedInput from "@material-ui/core/OutlinedInput";
import {makeStyles} from "@material-ui/core/styles";
import Typography from "@material-ui/core/Typography";
import Visibility from "@material-ui/icons/Visibility";
import VisibilityOff from "@material-ui/icons/VisibilityOff";
import React, {useState} from "react";
import {useDispatch, useSelector} from "react-redux";
import {useHistory} from "react-router";
import {failed} from "../../action";
import {request} from "../../api";

const useStyles = makeStyles((theme) => ({
  paper: {
    marginTop: theme.spacing(8),
    display: "flex",
    flexDirection: "column",
    alignItems: "center",
  },
  avatar: {
    margin: theme.spacing(1),
    backgroundColor: theme.palette.secondary.main,
  },
  form: {
    width: "100%", // Fix IE 11 issue.
    marginTop: theme.spacing(3),
  },
  submit: {
    margin: theme.spacing(3, 0, 2),
  },
}));
export default function ChangePassword() {
  const dispatch = useDispatch();
  const history = useHistory();
  const token = useSelector((state) => state.auth.token);

  const [currentPassword, setCurrentPassword] = useState(null);
  const [newPassword, setNewPassword] = useState(null);
  const [confirmPassword, setConfirmPassword] = useState(null);
  const [showPassword, setShowPassword] = useState(false);
  const [passwordChecked, setPasswordChecked] = useState(false);
  const [isRequesting, setIsRequesting] = useState(false);
  const [currentPasswordFailed, setCurrentPasswordFailed] = useState(false);

  const handleSubmit = (e) => {
    e.preventDefault();
    const data = {
      currentPassword: currentPassword,
      newPassword: newPassword,
    };
    setIsRequesting(true);
    request(
      "post",
      "/change-password",
      (res) => {
        console.log(res);
        setIsRequesting(false);
        if (res.status === 401) {
          dispatch(failed());
        } else if (res.status === 400) {
          setCurrentPasswordFailed(true);
        } else if (res.status === 200) {
          history.push("/");
          console.log("change password successully");
        }
      },
      {},
      data
    );
    /*
    authPost(dispatch, token, "/change-password", data).then(
      (res) => {
        console.log(res);
        setIsRequesting(false);
        if (res.status === 401) {
          dispatch(failed());
        } else if (res.status === 400) {
          setCurrentPasswordFailed(true);
        } else if (res.status === 200) {
          history.push("/");
          console.log("change password successully");
        }
      },
      (error) => {
        console.log("Exception " + error);
      }
    );
    */
  };

  const handleShowPassword = () => {
    setShowPassword(!showPassword);
  };
  const handleCurrentPasswordChange = (event) => {
    setCurrentPasswordFailed(false);
    setCurrentPassword(event.target.value);
  };
  const handleNewPasswordChange = (event) => {
    setNewPassword(event.target.value);
  };
  const handleConfirmPasswordChange = (event) => {
    setConfirmPassword(event.target.value);
    if (event.target.value === newPassword) {
      setPasswordChecked(true);
    } else setPasswordChecked(false);
  };
  const classes = useStyles();

  return (
    <Container component="main" maxWidth="xs">
      <CssBaseline />
      <div className={classes.paper}>
        <Typography component="h1" variant="h4">
          Change Password
        </Typography>
        <form className={classes.form} noValidate>
          <Grid container spacing={2}>
            <Grid item xs={12}>
              <FormControl
                error={currentPasswordFailed}
                fullWidth
                variant="outlined"
              >
                <InputLabel htmlFor="current-password">
                  Current Password
                </InputLabel>
                <OutlinedInput
                  id="current-password"
                  type={showPassword ? "text" : "password"}
                  value={currentPassword}
                  onChange={handleCurrentPasswordChange}
                  endAdornment={
                    <InputAdornment position="end">
                      <IconButton
                        aria-label="toggle password visibility"
                        onClick={handleShowPassword}
                        edge="end"
                      >
                        {showPassword ? <Visibility /> : <VisibilityOff />}
                      </IconButton>
                    </InputAdornment>
                  }
                  labelWidth={150}
                />
                <FormHelperText id="confirm-help-text">
                  {currentPasswordFailed
                    ? "Current password isn't correct"
                    : ""}
                </FormHelperText>
              </FormControl>
            </Grid>
            <Grid item xs={12}>
              <FormControl fullWidth variant="outlined">
                <InputLabel htmlFor="new-password">New Password</InputLabel>
                <OutlinedInput
                  id="new-password"
                  type={showPassword ? "text" : "password"}
                  value={newPassword}
                  onChange={handleNewPasswordChange}
                  endAdornment={
                    <InputAdornment position="end">
                      <IconButton
                        aria-label="toggle password visibility"
                        onClick={handleShowPassword}
                        edge="end"
                      >
                        {showPassword ? <Visibility /> : <VisibilityOff />}
                      </IconButton>
                    </InputAdornment>
                  }
                  labelWidth={150}
                />
              </FormControl>
            </Grid>
            <Grid item xs={12}>
              <FormControl
                error={!passwordChecked}
                fullWidth
                variant="outlined"
              >
                <InputLabel error={!passwordChecked} htmlFor="confirm-password">
                  Confirm Password
                </InputLabel>
                <OutlinedInput
                  id="confirm-password"
                  type={showPassword ? "text" : "password"}
                  value={confirmPassword}
                  onChange={handleConfirmPasswordChange}
                  aria-describedby={
                    !passwordChecked ? "confirm-help-text" : null
                  }
                  endAdornment={
                    <InputAdornment position="end">
                      <IconButton
                        aria-label="toggle password visibility"
                        onClick={handleShowPassword}
                        edge="end"
                      >
                        {showPassword ? <Visibility /> : <VisibilityOff />}
                      </IconButton>
                    </InputAdornment>
                  }
                  labelWidth={150}
                />
                <FormHelperText id="confirm-help-text">
                  {!passwordChecked ? "Confirm password isn't correct" : ""}
                </FormHelperText>
              </FormControl>
            </Grid>
          </Grid>
          {isRequesting === true ? (
            <Button
              type="submit"
              fullWidth
              variant="contained"
              color="primary"
              disabled={isRequesting}
              className={classes.submit}
              onClick={handleSubmit}
            >
              <CircularProgress /> Change
            </Button>
          ) : (
            <Button
              type="submit"
              fullWidth
              variant="contained"
              color="primary"
              disabled={!passwordChecked}
              className={classes.submit}
              onClick={handleSubmit}
            >
              Change
            </Button>
          )}
        </form>
      </div>
    </Container>
  );
}
