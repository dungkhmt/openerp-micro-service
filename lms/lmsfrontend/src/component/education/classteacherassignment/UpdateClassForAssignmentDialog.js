import {makeStyles} from "@material-ui/core/styles";
import {TextField} from "@mui/material";
import PrimaryButton from "component/button/PrimaryButton";
import TertiaryButton from "component/button/TertiaryButton";
import CustomizedDialogs from "component/dialog/CustomizedDialogs";
import React from "react";

const useStyles = makeStyles((theme) => ({
  dialogContent: {
    minWidth: 480,
    minHeight: 64,
  },
  btn: { margin: "4px 8px" },
}));

function UpdateClassForAssignmentDialog(props) {
  const { open, onClose, onUpdateInfo, selectedClassId: classId } = props;
  const classes = useStyles();

  //
  const [hourLoad, setHourLoad] = React.useState(null);

  return (
    <CustomizedDialogs
      open={open}
      handleClose={onClose}
      title={`Cập nhật thông tin lớp ${classId}`}
      // contentTopDivider
      content={
        <TextField
          required
          id="hourLoad"
          variant="outlined"
          size="small"
          label="Giờ quy đổi"
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
            className={classes.btn}
            onClick={() => onUpdateInfo(hourLoad)}
          >
            Cập nhật
          </PrimaryButton>
        </>
      }
      classNames={{ content: classes.dialogContent }}
    />
  );
}

export default UpdateClassForAssignmentDialog;
