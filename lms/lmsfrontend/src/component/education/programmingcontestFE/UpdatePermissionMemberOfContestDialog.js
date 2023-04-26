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
export default function UpdatePermissionMemberOfContestDialog(props) {
  const { open, onClose, onUpdateInfo, selectedUserRegisId, permissionIds } =
    props;
  const classes = useStyles();
  const [selectPermision, setSelectPermision] = useState(null);
  return (
    <CustomizedDialogs
      open={open}
      handleClose={onClose}
      title={`Update Permission`}
      // contentTopDivider
      content={
        <div>
          {/*rolesApproved != null ? rolesApproved.map((r, i) => ({ r })) : ""*/}

          <TextField
            required
            id="PERMISSION"
            variant="outlined"
            size="small"
            label="Permission"
            select
            value={selectPermision}
            style={{ width: 280, marginLeft: 8 }}
            onChange={(e) => {
              setSelectPermision(e.target.value);
            }}
          >
            {permissionIds.map((p, i) => (
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
            onClick={() => onUpdateInfo(selectPermision, selectedUserRegisId)}
          >
            Cập nhật
          </PrimaryButton>
        </>
      }
      classNames={{ content: classes.dialogContent }}
    />
  );
}
