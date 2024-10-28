import {makeStyles} from "@material-ui/core/styles";
import {TextField} from "@mui/material";
import PrimaryButton from "component/button/PrimaryButton";
import TertiaryButton from "component/button/TertiaryButton";
import CustomizedDialogs from "component/dialog/CustomizedDialogs";
import React from "react";

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

export const useStyles = makeStyles((theme) => ({
  dialogContent: {
    minWidth: 580,
    minHeight: 64,
  },
  btn: { margin: "4px 8px" },
}));

function UpdateTeacherForAssignmentDialog(props) {
  const { open, onClose, onUpdateInfo, selectedTeacherId: teacherId } = props;
  const classes = useStyles();

  //
  const [hourLoad, setHourLoad] = React.useState(null);
  // const [minimizeNumberWorkingDays, setMinimizeNumberWorkingDays] =
  //   React.useState(null);

  // const handleFormSubmit = (event) => {
  //   event.preventDefault();
  //   props.onUpdateInfo(hourLoad, minimizeNumberWorkingDays);
  // };

  return (
    <CustomizedDialogs
      open={open}
      handleClose={onClose}
      title={`Cập nhật thông tin giảng viên ${teacherId?.substring(
        0,
        teacherId.indexOf("@")
      )}`}
      // contentTopDivider
      content={
        <TextField
          required
          id="hourLoad"
          variant="outlined"
          size="small"
          label="Tải tối đa"
          value={hourLoad}
          style={{ width: 280, marginLeft: 8 }}
          onChange={(e) => {
            setHourLoad(e.target.value);
          }}
        />
      }
      actions={
        <>
          <TertiaryButton className={classes.btn} onClick={onClose}>
            Huỷ
          </TertiaryButton>
          <PrimaryButton
            sx={{ mr: 1 }}
            className={classes.btn}
            onClick={() => onUpdateInfo(hourLoad)}
          >
            Cập nhật
          </PrimaryButton>
        </>
      }
      classNames={{ content: classes.dialogContent }}
    />

    // <Modal
    //   open={props.open}
    //   onClose={props.onClose}
    //   aria-labelledby="simple-modal-title"
    //   aria-describedby="simple-modal-description"
    // >
    //   <div style={modalStyle.paper}>
    //     <h2 id="simple-modal-title">Cập nhật giáo viên cho đợt phân công</h2>
    //     <div width="100%">
    //       <form onSubmit={handleFormSubmit}>
    //         <Grid container spacing={1} alignItems="flex-end">
    //           <Grid item xs={2}>
    //             <TextField
    //               required
    //               id="hourLoad"
    //               label="Hour Load"
    //               value={hourLoad}
    //               fullWidth
    //               onChange={(event) => {
    //                 setHourLoad(event.target.value);
    //               }}
    //             ></TextField>
    //             <TextField
    //               required
    //               id="minimizeNumberWorkingDays"
    //               select
    //               label="Tối thiểu số ngày dạy"
    //               value={minimizeNumberWorkingDays}
    //               fullWidth
    //               onChange={(event) => {
    //                 setMinimizeNumberWorkingDays(event.target.value);
    //                 //console.log(problemId,event.target.value);
    //               }}
    //             >
    //               <MenuItem key={"Y"} value={"Y"}>
    //                 {"Y"}
    //               </MenuItem>
    //               <MenuItem key={"N"} value={"N"}>
    //                 {"N"}
    //               </MenuItem>
    //             </TextField>
    //           </Grid>

    //           <Grid item xs={2}>
    //             <Button color="primary" type="submit" width="100%">
    //               Update
    //             </Button>
    //           </Grid>
    //         </Grid>
    //       </form>
    //     </div>
    //   </div>
    // </Modal>
  );
}

export default UpdateTeacherForAssignmentDialog;
