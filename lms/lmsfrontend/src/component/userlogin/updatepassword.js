import DateFnsUtils from "@date-io/date-fns";
import {CircularProgress} from "@material-ui/core";
import Button from "@material-ui/core/Button";
import Card from "@material-ui/core/Card";
import CardActions from "@material-ui/core/CardActions";
import CardContent from "@material-ui/core/CardContent";
import {makeStyles} from "@material-ui/core/styles";
import TextField from "@material-ui/core/TextField";
import Typography from "@material-ui/core/Typography";
import {MuiPickersUtilsProvider} from "@material-ui/pickers";
import React, {useState} from "react";
import {useDispatch, useSelector} from "react-redux";
import {useHistory} from "react-router-dom";
import {failed} from "../../action/Auth";

const useStyles = makeStyles((theme) => ({
  root: {
    padding: theme.spacing(4),
    "& .MuiTextField-root": {
      margin: theme.spacing(1),
      width: 200,
    },
  },
  formControl: {
    margin: theme.spacing(1),
    minWidth: 120,
    maxWidth: 300,
  },
}));

// const ITEM_HEIGHT = 48;
// const ITEM_PADDING_TOP = 8;
// const MenuProps = {
//   PaperProps: {
//     style: {
//       maxHeight: ITEM_HEIGHT * 4.5 + ITEM_PADDING_TOP,
//       width: 250,
//     },
//   },
// };

export default function UpdateStudents(props) {
  const token = useSelector((state) => state.auth.token);
  const dispatch = useDispatch();
  const history = useHistory();

  const [userLoginId, setUserLoginId] = useState();
  const [password, setPassword] = useState();

  const [isRequesting, setIsRequesting] = useState(false);

  const classes = useStyles();

  const handleChangeUserLoginId = (event) => {
    setUserLoginId(event.target.value);
  };

  const handleChangePassword = (event) => {
    setPassword(event.target.value);
  };

  const input = {
    userLoginId: userLoginId,
    password: password,
  };

  const requestOptions = {
    method: "POST",
    headers: { "Content-Type": "application/json", "X-Auth-Token": token },
    body: JSON.stringify(input),
  };

  function updatePassword() {
    setIsRequesting(true);
    fetch("http://localhost:8080/api/user/updatepassword2", requestOptions)
      .then((response) => response.json())
      .then(
        (response) => {
          console.log(userLoginId);
          console.log(password);
          console.log("success!");
          setIsRequesting(false);
          if (console.status === 401) {
            dispatch(failed());
          }
        },
        (error) => {
          console.log(error);
        }
      );

    setIsRequesting(true);
    // /preventDefault();
    history.push("/userlogin/list");
  }

  const handleCancel = () => {
    history.push("/userlogin/list");
  };

  return (
    <MuiPickersUtilsProvider utils={DateFnsUtils}>
      <Card>
        <CardContent>
          <Typography variant="h5" component="h2">
            Cập nhật mật khẩu
          </Typography>
          <form className={classes.root} noValidate autoComplete="off">
            <div>
              <TextField
                id="select-userLoginId"
                label="Id muốn thay mật khẩu"
                value={userLoginId}
                onChange={handleChangeUserLoginId}
              ></TextField>

              <TextField
                id="select-password"
                label="Password"
                value={password}
                onChange={handleChangePassword}
              ></TextField>
            </div>
          </form>
        </CardContent>
        <CardActions>
          <Button
            disabled={isRequesting}
            variant="contained"
            color="primary"
            onClick={updatePassword}
          >
            {isRequesting ? <CircularProgress /> : "Lưu"}
          </Button>
          <Button
            disabled={isRequesting}
            variant="contained"
            color="primary"
            onClick={handleCancel}
            onChange={handleCancel}
          >
            {isRequesting ? <CircularProgress /> : "Hủy"}
          </Button>
        </CardActions>
      </Card>
    </MuiPickersUtilsProvider>
  );
}
