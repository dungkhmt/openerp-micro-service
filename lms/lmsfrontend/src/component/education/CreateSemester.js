// import { Box, Card, CardContent } from "@material-ui/core";
// import Button from "@material-ui/core/Button";
// import Dialog from "@material-ui/core/Dialog";
// import DialogActions from "@material-ui/core/DialogActions";
// import DialogContent from "@material-ui/core/DialogContent";
// import DialogContentText from "@material-ui/core/DialogContentText";
// import DialogTitle from "@material-ui/core/DialogTitle";
// import { makeStyles, MuiThemeProvider } from "@material-ui/core/styles";
// import TextField from "@material-ui/core/TextField";
// import MaterialTable, { MTableToolbar } from "material-table";
// import { useEffect, useState } from "react";
// import { useForm } from "react-hook-form";
// import { useDispatch, useSelector } from "react-redux";
// import { authGet, request } from "../../api";
// import { tableIcons } from "../../utils/iconutil";

// const useStyles = makeStyles((theme) => ({
//   button: {
//     margin: theme.spacing(1),
//   },
// }));

// const columns = [
//   { title: "Mã học kì", field: "semesterId" },
//   { title: "Tên học kì", field: "semesterName" },
// ];

// // TODO: consider remove
// export default function CreateSemester(props) {
//   const classes = useStyles();
//   const dispatch = useDispatch();
//   const token = useSelector((state) => state.auth.token);

//   const [semesters, setSemesters] = useState([]);

//   const { register, handleSubmit } = useForm();
//   const [dialogOpen, setDialogOpen] = useState(false);

//   useEffect(() => {
//     authGet(dispatch, token, "/edu/get-all-semester").then((res) => {
//       console.log(res);
//       setSemesters(res);
//     });
//   }, []);

//   const onCloseDialog = (event) => {
//     setDialogOpen(false);
//   };

//   const onSaveSemesterHandler = (data) => {
//     setDialogOpen(false);
//     let flag = false;
//     semesters.forEach((item) => {
//       if (item.semesterId === data["semesterId"]) {
//         flag = true;
//       }
//     });
//     if (flag) {
//       alert("Mã học kì " + data["semesterId"] + " đã tồn tại trong hệ thống.");
//     } else {
//       let input = {
//         semesterId: data["semesterId"],
//         semesterName: data["semesterName"],
//       };

//       request(
//         "post",
//         "/edu/create-semester",
//         (res) => {
//           alert("Đã lưu học kì " + data["semesterId"] + ".");
//           window.location.reload();
//         },
//         {},
//         input
//       );
//     }
//   };

//   return (
//     <div>
//       <MuiThemeProvider>
//         <Card>
//           <CardContent>
//             <MaterialTable
//               title="Danh sách học kì"
//               columns={columns}
//               data={semesters}
//               icons={tableIcons}
//               localization={{
//                 header: {
//                   actions: "",
//                 },
//                 body: {
//                   emptyDataSourceMessage: "Không có bản ghi nào để hiển thị",
//                   filterRow: {
//                     filterTooltip: "Lọc",
//                   },
//                 },
//               }}
//               options={{
//                 search: false,
//                 filtering: true,
//                 actionsColumnIndex: -1,
//               }}
//               components={{
//                 Toolbar: (props) => (
//                   <div>
//                     <MTableToolbar {...props} />
//                     <MuiThemeProvider>
//                       <Box display="flex" justifyContent="flex-end" width="98%">
//                         <form>
//                           <Button
//                             variant="contained"
//                             color="primary"
//                             size="small"
//                             onClick={(event) => {
//                               setDialogOpen(true);
//                             }}
//                             className={classes.button}
//                             style={{ marginLeft: "24px" }}
//                           >
//                             Thêm mới học kì
//                           </Button>
//                           <Dialog
//                             open={dialogOpen}
//                             onClose={onCloseDialog}
//                             aria-labelledby="form-dialog-title"
//                           >
//                             <DialogTitle id="form-dialog-title">
//                               Thêm mới học kì
//                             </DialogTitle>
//                             <form
//                               onSubmit={handleSubmit(onSaveSemesterHandler)}
//                             >
//                               <DialogContent>
//                                 <DialogContentText>
//                                   Điền thông tin vào form dưới đây và nhấn Lưu
//                                   để thêm mới một học kì.
//                                 </DialogContentText>
//                                 <TextField
//                                   required
//                                   margin="dense"
//                                   name="semesterId"
//                                   label="Mã học kì"
//                                   inputRef={register({ required: true })}
//                                   fullWidth
//                                 />
//                                 <TextField
//                                   required
//                                   margin="dense"
//                                   name="semesterName"
//                                   label="Tên học kì"
//                                   inputRef={register({ required: true })}
//                                   fullWidth
//                                 />
//                               </DialogContent>
//                               <DialogActions>
//                                 <Button type="submit" color="primary">
//                                   Lưu
//                                 </Button>
//                                 <Button onClick={onCloseDialog} color="primary">
//                                   Hủy
//                                 </Button>
//                               </DialogActions>
//                             </form>
//                           </Dialog>
//                         </form>
//                       </Box>
//                     </MuiThemeProvider>
//                   </div>
//                 ),
//               }}
//             />
//           </CardContent>
//         </Card>
//       </MuiThemeProvider>
//     </div>
//   );
// }
