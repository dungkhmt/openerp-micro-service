// import DateFnsUtils from "@date-io/date-fns";
// import {Button, CardActions, CircularProgress} from "@material-ui/core";
// import Card from "@material-ui/core/Card";
// import CardContent from "@material-ui/core/CardContent";
// import {makeStyles} from "@material-ui/core/styles";
// import TextField from "@material-ui/core/TextField";
// import Typography from "@material-ui/core/Typography";
// import {MuiPickersUtilsProvider} from "@material-ui/pickers";
// import React, {useEffect, useState} from "react";
// import {useDispatch, useSelector} from "react-redux";
// import {useHistory, useParams} from "react-router-dom";
// import {failed} from "../../action";
// import {authPost} from "../../api";
// import {API_URL} from "../../config/config";
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
//
// // const ITEM_HEIGHT = 48;
// // const ITEM_PADDING_TOP = 8;
// // const MenuProps = {
// //   PaperProps: {
// //     style: {
// //       maxHeight: ITEM_HEIGHT * 4.5 + ITEM_PADDING_TOP,
// //       width: 250,
// //     },
// //   },
// // };
//
// function EditUser(props) {
//   const history = useHistory();
//   const { partyId } = useParams();
//   const token = useSelector((state) => state.auth.token);
//   const dispatch = useDispatch();
//   const classes = useStyles();
//   const [password, setPassword] = useState();
//
//   const [securityGroups, setSecurityGroups] = useState([]);
//
//   const [isRequesting, setIsRequesting] = useState(false);
//
//   function getSecurityGroups() {
//     fetch(API_URL + "/get-security-groups", {
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
//   const handleChangePassword = (event) => {
//     setPassword(event.target.value);
//   };
//
//   const handleCancel = () => {
//     history.push("/userlogin/list");
//   };
//
//   const handleSubmit = () => {
//     const data = {
//       password: password,
//     };
//     setIsRequesting(true);
//     authPost(dispatch, token, "/user/updatepassword3/" + partyId, data).then(
//       (res) => {
//         setIsRequesting(false);
//         if (res.status === 401) {
//           dispatch(failed());
//           throw Error("Unauthorized");
//         } else if (res.status === 200) {
//           return res.json();
//         }
//       },
//       (error) => {
//         console.log(error);
//       }
//     );
//     setIsRequesting(true);
//     // /preventDefault();
//     history.push("/userlogin/list");
//   };
//
//   return (
//     <MuiPickersUtilsProvider utils={DateFnsUtils}>
//       <Card>
//         <CardContent>
//           <Typography variant="h5" component="h2">
//             Cập nhật mật khẩu
//           </Typography>
//           <form className={classes.root} noValidate autoComplete="off">
//             <div>
//               <TextField
//                 id="select-password"
//                 label="Password"
//                 value={password}
//                 onChange={handleChangePassword}
//               ></TextField>
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
//             {isRequesting ? <CircularProgress /> : "Lưu"}
//           </Button>
//           <Button
//             disabled={isRequesting}
//             variant="contained"
//             color="primary"
//             onClick={handleCancel}
//             onChange={handleCancel}
//           >
//             {isRequesting ? <CircularProgress /> : "Hủy"}
//           </Button>
//         </CardActions>
//       </Card>
//     </MuiPickersUtilsProvider>
//   );
// }
//
// const screenName = "SCREEN_USER_UPDATE";
// export default withScreenSecurity(EditUser, screenName, true);
