import {Typography} from "@material-ui/core";
import {makeStyles} from "@material-ui/core/styles";
import React, {useState} from "react";
import PrimaryButton from "../button/PrimaryButton";
import CustomizedDialogs from "./CustomizedDialogs";

const useStyles = makeStyles((theme) => ({
  btn: { width: 100 },
  dialogContent: { width: 550 },
  actions: { paddingRight: theme.spacing(2) },
}));

function ErrorDialog({ open }) {
  const classes = useStyles();

  //
  const [openDialog, setOpen] = useState(open);

  //
  const handleClose = () => {
    setOpen(false);
  };

  return (
    <CustomizedDialogs
      open={openDialog}
      handleClose={handleClose}
      title="Rất tiếc"
      centerTitle
      contentTopDivider
      content={
        <Typography color="textSecondary" gutterBottom style={{ padding: 8 }}>
          Đã xảy ra lỗi. Chúng tôi đang cố gắng khắc phục lỗi sớm nhất có thể.
        </Typography>
      }
      actions={
        <PrimaryButton className={classes.btn} onClick={handleClose}>
          Đã hiểu
        </PrimaryButton>
      }
      classNames={{ content: classes.dialogContent, actions: classes.actions }}
    />
  );
}

export default ErrorDialog;
