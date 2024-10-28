// import React from "react";
// import Button from "@material-ui/core/Button";
// import Dialog from "@material-ui/core/Dialog";
// import DialogActions from "@material-ui/core/DialogActions";
// import DialogContent from "@material-ui/core/DialogContent";
// import DialogContentText from "@material-ui/core/DialogContentText";
// import DialogTitle from "@material-ui/core/DialogTitle";
//
// export default function AlertDialog(props) {
//   const {
//     title = "",
//     message = "",
//     open,
//     setOpen,
//     afterShowCallback = {},
//   } = props;
//
//   const handleClose = () => {
//     setOpen(false);
//     let okCallback = afterShowCallback["OK"];
//     if (typeof okCallback === "function") {
//       okCallback();
//     }
//   };
//
//   return (
//     <div>
//       <Dialog
//         open={open}
//         onClose={handleClose}
//         aria-labelledby="alert-dialog-title"
//         aria-describedby="alert-dialog-description"
//       >
//         <DialogTitle id="alert-dialog-title">{title}</DialogTitle>
//         <DialogContent>
//           <DialogContentText id="alert-dialog-description">
//             {message}
//           </DialogContentText>
//         </DialogContent>
//         <DialogActions>
//           <Button onClick={handleClose} color="primary">
//             OK
//           </Button>
//         </DialogActions>
//       </Dialog>
//     </div>
//   );
// }
