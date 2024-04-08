// import { Button, Grid, MenuItem, Modal, TextField } from "@material-ui/core";
// import React, { useEffect } from "react";
// import { request } from "../../../api";

// const modalStyle = {
//   paper: {
//     boxSizing: "border-box",
//     position: "absolute",
//     width: 600,
//     maxHeight: 600,
//     // border: '2px solid #000',
//     borderRadius: "5px",
//     boxShadow:
//       "0px 3px 1px -2px rgb(0 0 0 / 20%), 0px 2px 2px 0px rgb(0 0 0 / 14%), 0px 1px 5px 0px rgb(0 0 0 / 12%)",
//     backgroundColor: "white",
//     zIndex: 999,
//     left: "50%",
//     top: "50%",
//     transform: "translate(-50% , -50%)",
//     padding: "20px 40px",
//   },
// };

// function SuggestTeachersForClassModel(props) {
//   const classId = props.selectedClassId;
//   const planId = props.planId;
//   const [selectedTeacher, setSelectedTeacher] = React.useState("");
//   const [teachers, setTeachers] = React.useState([]);

//   const handleFormSubmit = (event) => {
//     event.preventDefault();
//     props.onSelectAssign(selectedTeacher);
//   };

//   async function getTeacherForAssignmentList(classId) {
//     //alert("get suggested teacher for class " + classId);
//     request(
//       // token,
//       // history,
//       "GET",
//       "/get-suggested-teacher-for-class/" + classId + "/" + planId,
//       (res) => {
//         let temp = [];
//         res.data.map((elm, index) => {
//           temp.push({
//             teacherId: elm.teacherId,
//             courseId: elm.teacherName,
//             priority: elm.hourLoad,
//             info: elm.info,
//             selected: false,
//           });
//         });
//         setTeachers(temp);

//         //setTeacherList(res.data);
//       }
//     );
//   }
//   function handleGetData() {
//     //alert("get data " + classId);
//     getTeacherForAssignmentList(classId);
//   }

//   useEffect(() => {
//     //getTeacherForAssignmentList();
//   }, []);

//   return (
//     <Modal
//       open={props.open}
//       onClose={props.onClose}
//       aria-labelledby="simple-modal-title"
//       aria-describedby="simple-modal-description"
//     >
//       <div style={modalStyle.paper}>
//         <h2 id="simple-modal-title">Gợi ý giáo viên cho lớp</h2>
//         <div width="100%">
//           <form onSubmit={handleFormSubmit}>
//             <Grid container spacing={1} alignItems="flex-end">
//               <Grid item xs={2}>
//                 <Button color="primary" width="100%" onClick={handleGetData}>
//                   GET DATA
//                 </Button>
//               </Grid>

//               <Grid item xs={2}>
//                 <TextField
//                   required
//                   id="selectedTeacher"
//                   select
//                   label="Chọn GV"
//                   value={selectedTeacher}
//                   fullWidth
//                   onChange={(event) => {
//                     setSelectedTeacher(event.target.value);
//                   }}
//                 >
//                   {teachers.map((item) => (
//                     <MenuItem key={item.teacherId} value={item.teacherId}>
//                       {item.info}
//                     </MenuItem>
//                   ))}
//                 </TextField>
//               </Grid>

//               <Grid item xs={2}>
//                 <Button color="primary" type="submit" width="100%">
//                   Chon
//                 </Button>
//               </Grid>
//             </Grid>
//           </form>
//         </div>
//       </div>
//     </Modal>
//   );
// }

// export default SuggestTeachersForClassModel;
