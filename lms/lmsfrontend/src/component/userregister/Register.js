// import React, {useState} from "react";
// import {textField} from "../../utils/FormUtils";
// import Button from "@material-ui/core/Button";
// import {API_URL} from "../../config/config";
// import {useHistory} from "react-router-dom";
// import AlertDialog from "../../utils/AlertDialog";
//
// export default function Register() {
//   const history = useHistory();
//
//   const [userLoginId, setUserLoginId] = useState("");
//   const [password, setPassword] = useState("");
//   const [email, setEmail] = useState("");
//   // const [fullName, setFullName] = useState('');
//
//   const [firstName, setFirstName] = useState("");
//   const [middleName, setMiddleName] = useState("");
//   const [lastName, setLastName] = useState("");
//
//   /*
//    * BEGIN: Alert Dialog
//    */
//   const [alertTitle, setAlertTitle] = useState("");
//   const [alertMessage, setAlertMessage] = useState("");
//   const [openAlert, setOpenAlert] = useState(false);
//   const [alertCallback, setAlertCallback] = useState({});
//
//   function showAlert(title = "", message = "", callback = {}) {
//     setAlertTitle(title);
//     setAlertMessage(message);
//     setAlertCallback(callback);
//     setOpenAlert(true);
//   }
//
//   /*
//    * END: Alert Dialog
//    */
//
//   function verify(field, conditional, errorMessage) {
//     if (!conditional(field)) {
//       showAlert("Đã có lỗi xảy ra", errorMessage);
//       return false;
//     }
//     return true;
//   }
//
//   function isValidEmail(email) {
//     const re =
//       /^(([^<>()\[\]\.,;:\s@\"]+(\.[^<>()\[\]\.,;:\s@\"]+)*)|(\".+\"))@(([^<>()[\]\.,;:\s@\"]+\.)+[^<>()[\]\.,;:\s@\"]{2,})$/i;
//     return re.test(String(email).toLowerCase());
//   }
//
//   async function handleSubmit() {
//     let notEmptyCheck = (e) => e !== "";
//     let emptyErrorMessage = "Vui lòng nhập đầy đủ các trường trước khi đăng ký";
//     if (
//       verify(userLoginId, notEmptyCheck, emptyErrorMessage) &&
//       verify(password, notEmptyCheck, emptyErrorMessage) &&
//       verify(email, isValidEmail, "Hãy nhập một email hợp lệ!") &&
//       verify(firstName, notEmptyCheck, emptyErrorMessage) &&
//       verify(middleName, notEmptyCheck, emptyErrorMessage) &&
//       verify(lastName, notEmptyCheck, emptyErrorMessage)
//     ) {
//       let body = {
//         userLoginId,
//         password,
//         email,
//         firstName,
//         middleName,
//         lastName,
//       };
//       let userRegister = await fetch(API_URL + "/user/register/", {
//         method: "POST",
//         headers: {
//           "content-type": "application/json",
//         },
//         body: JSON.stringify(body),
//       }).then((r) => r.json());
//
//       if (userRegister && userRegister["userLoginId"]) {
//         showAlert(
//           "Đăng ký thành công",
//           "Đăng ký thành công người dùng " +
//             userLoginId +
//             ", người dùng đang chờ được phê duyệt.",
//           { OK: () => history.push("/login") }
//         );
//       } else {
//         showAlert(
//           "Đăng ký thất bại",
//           "Đăng ký thất bại, tên người dùng " +
//             userLoginId +
//             " hoặc địa chỉ email " +
//             email +
//             " có thể đã được sử dụng!"
//         );
//       }
//     }
//   }
//
//   return (
//     <div style={{ marginLeft: "20px" }}>
//       <h2>Đăng ký</h2>
//
//       {textField(
//         "userLoginId",
//         "User Name",
//         "search",
//         userLoginId,
//         setUserLoginId
//       )}
//       {textField("password", "Password", "password", password, setPassword)}
//       {textField("email", "Email", "search", email, setEmail)}
//       {textField("lastName", "Last Name", "search", lastName, setLastName)}
//       {textField(
//         "middleName",
//         "Middle Name",
//         "search",
//         middleName,
//         setMiddleName
//       )}
//       {textField("firstName", "First Name", "search", firstName, setFirstName)}
//
//       <AlertDialog
//         title={alertTitle}
//         message={alertMessage}
//         open={openAlert}
//         setOpen={setOpenAlert}
//         afterShowCallback={alertCallback}
//       />
//
//       <Button variant="contained" color="primary" onClick={handleSubmit}>
//         Register
//       </Button>
//     </div>
//   );
// }
