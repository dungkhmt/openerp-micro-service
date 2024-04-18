//import { makeStyles } from "@mui/styles"; //"@material-ui/core/styles";
import {makeStyles} from "@material-ui/core/styles";

import {MenuItem, TextField} from "@mui/material";
import PrimaryButton from "component/button/PrimaryButton";
import TertiaryButton from "component/button/TertiaryButton";
import CustomizedDialogs from "component/dialog/CustomizedDialogs";
import React, {useState} from "react";

const useStyles = makeStyles((theme) => ({
  dialogContent: {
    minWidth: 480,
    minHeight: 64,
  },
  btn: { margin: "4px 8px" },
}));
export default function UpdateProblemContestDialog(props) {
  const {
    open,
    onClose,
    onUpdateInfo,
    selectedProblemId,
    selectedContestId,
    modes,
  } = props;
  const classes = useStyles();
  const [selectSubmissionMode, setSelectSubmissionMode] = useState(null);
  return (
    <CustomizedDialogs
      open={open}
      handleClose={onClose}
      title={`Update Problem Contest`}
      // contentTopDivider
      content={
        <div>
          {/*rolesApproved != null ? rolesApproved.map((r, i) => ({ r })) : ""*/}

          <TextField
            required
            id="modes"
            variant="outlined"
            size="small"
            label="Submission Mode"
            select
            value={selectSubmissionMode}
            style={{ width: 280, marginLeft: 8 }}
            onChange={(e) => {
              setSelectSubmissionMode(e.target.value);
            }}
          >
            {modes.map((p, i) => (
              <MenuItem key={p} value={p}>
                {p}
              </MenuItem>
            ))}
          </TextField>
        </div>
      }
      actions={
        <>
          <TertiaryButton className={classes.btn} onClick={onClose}>
            Huỷ
          </TertiaryButton>
          <PrimaryButton
            className={classes.btn}
            onClick={() =>
              onUpdateInfo(
                selectSubmissionMode,
                selectedProblemId,
                selectedContestId
              )
            }
          >
            Cập nhật
          </PrimaryButton>
        </>
      }
      classNames={{ content: classes.dialogContent }}
    />
  );
}
