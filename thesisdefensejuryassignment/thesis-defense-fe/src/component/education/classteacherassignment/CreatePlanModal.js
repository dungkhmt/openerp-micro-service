import {makeStyles} from "@material-ui/core/styles";
import TextField from "@mui/material/TextField";
import PrimaryButton from "component/button/PrimaryButton";
import CustomizedDialogs from "component/dialog/CustomizedDialogs";
import React from "react";

const useStyles = makeStyles((theme) => ({
  dialogContent: {
    minWidth: 400,
  },
}));

function CreatePlanModal(props) {
  const { open, onClose, onCreate } = props;
  const classes = useStyles();

  //
  const [planName, setPlanName] = React.useState("");

  const onPlanNameChange = (event) => {
    setPlanName(event.target.value);
  };

  return (
    <CustomizedDialogs
      open={open}
      handleClose={onClose}
      title={`Tạo đợt phân công`}
      contentTopDivider
      content={
        <TextField
          onChange={onPlanNameChange}
          label="Tên*"
          value={planName}
          size="small"
          sx={{ width: 300, m: 1 }}
        />
      }
      actions={
        <PrimaryButton
          disabled={planName.trim().length === 0}
          sx={{ mr: 1 }}
          onClick={() => {
            onCreate(planName);
          }}
        >
          Tạo mới
        </PrimaryButton>
      }
      classNames={{ content: classes.dialogContent }}
    />
  );
}

export default CreatePlanModal;
