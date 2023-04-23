// import { Button, Grid, Modal } from "@material-ui/core";
// import React from "react";

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

// function UploadExcelClassForTeacherAssignmentModel(props) {
//   const [filename, setFilename] = React.useState("");

//   const handleFormSubmit = (event) => {
//     event.preventDefault();
//     props.onUpload(filename);
//   };

//   const onInputChange = (event) => {
//     let name = event.target.value;
//     setFilename(name);
//   };
//   function onFileChange(event) {
//     setFilename(event.target.files[0]);
//     console.log(event.target.files[0].name);
//   }
//   return (
//     <Modal
//       open={props.open}
//       onClose={props.onClose}
//       aria-labelledby="simple-modal-title"
//       aria-describedby="simple-modal-description"
//     >
//       <div style={modalStyle.paper}>
//         <h2 id="simple-modal-title">Chọn file excel để upload</h2>
//         <div width="100%">
//           <form onSubmit={handleFormSubmit}>
//             <Grid container spacing={1} alignItems="flex-end">
//               <Grid item xs={2}>
//                 <Button
//                   color="primary"
//                   type="submit"
//                   onChange={onInputChange}
//                   width="100%"
//                 >
//                   Tạo mới
//                 </Button>
//               </Grid>

//               <input
//                 type="file"
//                 id="selected-upload-file"
//                 onChange={onFileChange}
//               />
//             </Grid>
//           </form>
//         </div>
//       </div>
//     </Modal>
//   );
// }

// export default UploadExcelClassForTeacherAssignmentModel;
