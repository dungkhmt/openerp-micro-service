// import DateFnsUtils from "@date-io/date-fns";
// import {CircularProgress} from "@material-ui/core";
// import Button from "@material-ui/core/Button";
// import Card from "@material-ui/core/Card";
// import CardActions from "@material-ui/core/CardActions";
// import CardContent from "@material-ui/core/CardContent";
// import FormControl from "@material-ui/core/FormControl";
// import Input from "@material-ui/core/Input";
// import InputLabel from "@material-ui/core/InputLabel";
// import MenuItem from "@material-ui/core/MenuItem";
// import Select from "@material-ui/core/Select";
// import {makeStyles} from "@material-ui/core/styles";
// import TextField from "@material-ui/core/TextField";
// import Typography from "@material-ui/core/Typography";
// import {KeyboardDatePicker, MuiPickersUtilsProvider,} from "@material-ui/pickers";
// import React, {useEffect, useState} from "react";
// import {useDispatch, useSelector} from "react-redux";
// import {useHistory} from "react-router-dom";
// import {failed} from "../../action/Auth";
// import {authPost, BASE_URL} from "../../api";
// import withScreenSecurity from "../withScreenSecurity";
//
// const useStyles = makeStyles((theme) => ({
//   root: {
//     padding: theme.spacing(4),
//     "& .MuiTextField-root": {
//       margin: theme.spacing(1),
//       width: 200,
//     },
//   },
//   formControl: {
//     margin: theme.spacing(1),
//     minWidth: 120,
//     maxWidth: 300,
//   },
// }));
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
//
// function UserCreate(props) {
//   const token = useSelector((state) => state.auth.token);
//   const dispatch = useDispatch();
//   const history = useHistory();
//
//   const [securityGroups, setSecurityGroups] = useState([]);
//
//   const [lastName, setLastName] = useState();
//   const [middleName, setMiddleName] = useState();
//   const [firstName, setFirstName] = useState();
//   const [userName, setUserName] = useState();
//   const [password, setPassword] = useState();
//   const [gender, setGender] = useState();
//   const [partyCode, setPartyCode] = useState();
//   const [roles, setRoles] = useState([]);
//   const [birthDate, setBirthDate] = useState(new Date());
//   const handleBirthDateChange = (date) => {
//     setBirthDate(date);
//   };
//   const [isRequesting, setIsRequesting] = useState(false);
//
//   const classes = useStyles();
//
//   function getSecurityGroups() {
//     fetch(BASE_URL + "/get-security-groups", {
//       method: "GET",
//       headers: { "Content-Type": "application/json", "X-Auth-Token": token },
//     })
//       .then((response) => response.json())
//       .then((response) => {
//         console.log(response);
//         let arr = [];
//         response.forEach((d) => {
//           arr.push(d);
//         });
//         setSecurityGroups(arr);
//         //console.log('getDepartmentList = ',departments);
//       });
//   }
//
//   useEffect(() => {
//     //let lst = authGet(dispatch, token,"/get-security-groups");
//     //console.log(lst);
//     getSecurityGroups();
//   }, []);
//
//   const handleUserNameChange = (event) => {
//     setUserName(event.target.value);
//   };
//   const handleLastNameChange = (event) => {
//     setLastName(event.target.value);
//   };
//   const handleMiddleNameChange = (event) => {
//     setMiddleName(event.target.value);
//   };
//   const handleFirstNameChange = (event) => {
//     setFirstName(event.target.value);
//   };
//   const handlePasswordChange = (event) => {
//     setPassword(event.target.value);
//   };
//   const handleGenderChange = (event) => {
//     setGender(event.target.value);
//   };
//   const handlePartyCodeChange = (event) => {
//     setPartyCode(event.target.value);
//   };
//
//   const handleRoleChange = (event) => {
//     // console.log(event.target);
//     // const { options } = event.target;
//     // const value = [];
//     // for (let i = 0, l = options.length; i < l; i += 1) {
//     //   if (options[i].selected) {
//     //     value.push(options[i].value);
//     //   }
//     // }
//     setRoles(event.target.value);
//   };
//   const handleSubmit = () => {
//     const data = {
//       userName: userName,
//       password: password,
//       lastName: lastName,
//       middleName: middleName,
//       firstName: firstName,
//       birthDate: birthDate,
//       gender: gender,
//       partyCode: partyCode,
//       roles: roles,
//     };
//     setIsRequesting(true);
//     authPost(dispatch, token, "/user", data)
//       .then(
//         (res) => {
//           console.log(res);
//           setIsRequesting(false);
//           if (res.status === 401) {
//             dispatch(failed());
//             throw Error("Unauthorized");
//           } else if (res.status === 409) {
//             alert("User exits!!");
//           } else if (res.status === 201) {
//             return res.json();
//           }
//         },
//         (error) => {
//           console.log(error);
//         }
//       )
//       .then((res) => {
//         history.push("/userlogin/" + res);
//       });
//   };
//
//   return (
//     <MuiPickersUtilsProvider utils={DateFnsUtils}>
//       <Card>
//         <CardContent>
//           <Typography variant="h5" component="h2">
//             Create User
//           </Typography>
//           <form className={classes.root} noValidate autoComplete="off">
//             <div>
//               <TextField
//                 id="partyCode"
//                 label="Party Code"
//                 value={partyCode}
//                 onChange={handlePartyCodeChange}
//               />
//               <TextField
//                 id="firstName"
//                 label="First Name"
//                 value={firstName}
//                 onChange={handleFirstNameChange}
//               />
//               <TextField
//                 id="middleName"
//                 label="Middle Name"
//                 value={middleName}
//                 onChange={handleMiddleNameChange}
//               />
//               <TextField
//                 id="lastName"
//                 label="LastName"
//                 value={lastName}
//                 onChange={handleLastNameChange}
//               />
//               <TextField
//                 id="select-gender"
//                 select
//                 label="Select"
//                 value={gender}
//                 onChange={handleGenderChange}
//                 helperText="Select your gender"
//               >
//                 <MenuItem key="male" value="M">
//                   Male
//                 </MenuItem>
//                 <MenuItem key="female" value="F">
//                   Female
//                 </MenuItem>
//               </TextField>
//               <KeyboardDatePicker
//                 disableToolbar
//                 variant="inline"
//                 format="MM/dd/yyyy"
//                 margin="normal"
//                 id="date-picker-inline"
//                 label="Date picker inline"
//                 value={birthDate}
//                 onChange={handleBirthDateChange}
//                 KeyboardButtonProps={{
//                   "aria-label": "change date",
//                 }}
//               />
//               <TextField
//                 id="userName"
//                 label="UserName"
//                 value={userName}
//                 onChange={handleUserNameChange}
//               />
//               <TextField
//                 id="password"
//                 label="Password"
//                 value={password}
//                 type="password"
//                 onChange={handlePasswordChange}
//               />
//               <FormControl className={classes.formControl}>
//                 <InputLabel id="role-label">Role</InputLabel>
//                 <Select
//                   labelId="role-label"
//                   id="demo-mutiple-name"
//                   multiple
//                   value={roles}
//                   onChange={handleRoleChange}
//                   input={<Input />}
//                   MenuProps={MenuProps}
//                 >
//                   {securityGroups.map((s) => (
//                     <MenuItem key={s.groupId} value={s.groupId}>
//                       {s.description}
//                     </MenuItem>
//                   ))}
//                 </Select>
//               </FormControl>
//             </div>
//           </form>
//         </CardContent>
//         <CardActions>
//           <Button
//             disabled={isRequesting}
//             variant="contained"
//             color="primary"
//             onClick={handleSubmit}
//           >
//             {isRequesting ? <CircularProgress /> : "Save"}
//           </Button>
//         </CardActions>
//       </Card>
//     </MuiPickersUtilsProvider>
//   );
// }
// const screenName = "SCREEN_USER_CREATE";
// export default withScreenSecurity(UserCreate, screenName, true);
