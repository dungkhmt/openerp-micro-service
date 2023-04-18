import DateFnsUtils from "@date-io/date-fns";
import {
  Button,
  CardActions,
  CircularProgress,
  FormControl,
  Input,
  InputLabel,
  MenuItem,
  Select,
} from "@material-ui/core";
import Card from "@material-ui/core/Card";
import CardContent from "@material-ui/core/CardContent";
import {makeStyles} from "@material-ui/core/styles";
import TextField from "@material-ui/core/TextField";
import Typography from "@material-ui/core/Typography";
import {KeyboardDatePicker, MuiPickersUtilsProvider,} from "@material-ui/pickers";
import React, {useEffect, useState} from "react";
import {useDispatch, useSelector} from "react-redux";
import {useHistory, useParams} from "react-router-dom";
import {failed} from "../../action";
import {authGet, authPut} from "../../api";
import {API_URL} from "../../config/config";
import withScreenSecurity from "../withScreenSecurity";

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
const ITEM_HEIGHT = 48;
const ITEM_PADDING_TOP = 8;
const MenuProps = {
  PaperProps: {
    style: {
      maxHeight: ITEM_HEIGHT * 4.5 + ITEM_PADDING_TOP,
      width: 250,
    },
  },
};

function EditUser(props) {
  const history = useHistory();
  const { partyId } = useParams();
  const token = useSelector((state) => state.auth.token);
  const dispatch = useDispatch();
  const classes = useStyles();
  const [lastName, setLastName] = useState();
  const [middleName, setMiddleName] = useState();
  const [firstName, setFirstName] = useState();
  const [userName, setUserName] = useState();
  const [email, setEmail] = useState();
  const [partyCode, setPartyCode] = useState();
  const [roles, setRoles] = useState([]);
  const [birthDate, setBirthDate] = useState(new Date());

  const [securityGroups, setSecurityGroups] = useState([]);
  const [enabled, setEnabled] = useState(null);

  const handleBirthDateChange = (date) => {
    setBirthDate(date);
  };
  const [isRequesting, setIsRequesting] = useState(false);

  function getSecurityGroups() {
    fetch(API_URL + "/get-security-groups", {
      method: "GET",
      headers: { "Content-Type": "application/json", "X-Auth-Token": token },
    })
      .then((response) => response.json())
      .then((response) => {
        console.log(response);
        let arr = [];
        response.forEach((d) => {
          arr.push(d);
        });
        setSecurityGroups(arr);
        //console.log('getDepartmentList = ',departments);
      });
  }

  useEffect(() => {
    //let lst = authGet(dispatch, token,"/get-security-groups");
    //console.log(lst);
    getSecurityGroups();
  }, []);

  const handleLastNameChange = (event) => {
    setLastName(event.target.value);
  };
  const handleMiddleNameChange = (event) => {
    setMiddleName(event.target.value);
  };
  const handleFirstNameChange = (event) => {
    setFirstName(event.target.value);
  };
  const handleEmailChange = (event) => {
    setEmail(event.target.value);
  };
  const handlePartyCodeChange = (event) => {
    setPartyCode(event.target.value);
  };

  const handleRoleChange = (event) => {
    setRoles(event.target.value);
  };
  const handleEnabledChange = (e) => {
    setEnabled(e.target.value);
  };
  const handleSubmit = () => {
    const data = {
      lastName: lastName,
      middleName: middleName,
      firstName: firstName,
      birthDate: birthDate,
      partyCode: partyCode,
      roles: roles,
      enabled: enabled,
      email: email,
    };
    setIsRequesting(true);
    authPut(dispatch, token, "/user/" + partyId, data)
      .then(
        (res) => {
          setIsRequesting(false);
          if (res.status === 401) {
            dispatch(failed());
            throw Error("Unauthorized");
          } else if (res.status === 200) {
            return res.json();
          }
        },
        (error) => {
          console.log(error);
        }
      )
      .then((res) => {
        history.push("/userlogin/" + res);
      });
  };
  useEffect(() => {
    authGet(dispatch, token, "/users/" + partyId).then(
      (res) => {
        console.log("getUserDetail for update, res = ", res);
        setFirstName(res.firstName);
        setMiddleName(res.middleName);
        setLastName(res.lastName);
        setBirthDate(res.birthDate);
        setPartyCode(res.partyCode);
        setUserName(res.userLoginId);
        setRoles(res.roles);
        setEnabled(res.enabled);
        setEmail(res.email);
      },
      (error) => {}
    );
  }, []);

  return (
    <MuiPickersUtilsProvider utils={DateFnsUtils}>
      <Card>
        <CardContent>
          <Typography variant="h5" component="h2" align="left">
            Edit User {userName}
          </Typography>
          <form className={classes.root} noValidate autoComplete="off">
            <div>
              <TextField
                id="partyCode"
                label="Party Code"
                value={partyCode}
                variant="outlined"
                onChange={handlePartyCodeChange}
                InputLabelProps={{
                  shrink: true,
                }}
              />
              <TextField
                id="firstName"
                label="First Name"
                value={firstName}
                variant="outlined"
                onChange={handleFirstNameChange}
                InputLabelProps={{
                  shrink: true,
                }}
              />
              <TextField
                id="middleName"
                label="Middle Name"
                value={middleName}
                onChange={handleMiddleNameChange}
                InputLabelProps={{
                  shrink: true,
                }}
              />
              <TextField
                id="lastName"
                label="LastName"
                value={lastName}
                onChange={handleLastNameChange}
                InputLabelProps={{
                  shrink: true,
                }}
              />
              <TextField
                id="email"
                label="email"
                value={email}
                onChange={handleEmailChange}
                InputLabelProps={{
                  shrink: true,
                }}
              />

              <KeyboardDatePicker
                disableToolbar
                variant="inline"
                format="MM/dd/yyyy"
                margin="normal"
                id="date-picker-inline"
                label="Date picker inline"
                value={birthDate}
                onChange={handleBirthDateChange}
                KeyboardButtonProps={{
                  "aria-label": "change date",
                }}
              />
              <FormControl className={classes.formControl}>
                <InputLabel id="role-label">Role</InputLabel>
                <Select
                  labelId="role-label"
                  id="demo-mutiple-name"
                  multiple
                  value={roles}
                  onChange={handleRoleChange}
                  input={<Input />}
                  MenuProps={MenuProps}
                >
                  {securityGroups.map((s) => (
                    <MenuItem key={s.groupId} value={s.groupId}>
                      {s.name}
                    </MenuItem>
                  ))}
                </Select>
              </FormControl>
              <FormControl className={classes.formControl}>
                <InputLabel id="role-label">Enabled</InputLabel>
                <Select
                  labelId="enabled"
                  id="enabled"
                  value={enabled}
                  onChange={handleEnabledChange}
                  input={<Input />}
                  MenuProps={MenuProps}
                >
                  <MenuItem key={"Y"} value={"Y"}>
                    {"Y"}
                  </MenuItem>
                  <MenuItem key={"N"} value={"N"}>
                    {"N"}
                  </MenuItem>
                </Select>
              </FormControl>
            </div>
          </form>
        </CardContent>
        <CardActions>
          <Button
            disabled={isRequesting}
            variant="contained"
            color="primary"
            onClick={handleSubmit}
          >
            {isRequesting ? <CircularProgress /> : "Save"}
          </Button>
        </CardActions>
      </Card>
    </MuiPickersUtilsProvider>
  );
}

const screenName = "SCREEN_USER_UPDATE";
export default withScreenSecurity(EditUser, screenName, true);
