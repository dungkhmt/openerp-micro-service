import { Typography } from "@mui/material";
import { useState } from "react";
import PrimaryButton from "../button/PrimaryButton";
import CustomizedDialogs from "./CustomizedDialogs";

const styles = {
  btn: { width: 100 },
  dialogContent: (theme) => ({ width: 550 }),
  actions: (theme) => ({ paddingRight: theme.spacing(2) }),
};

function ErrorDialog({ open }) {
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
        <PrimaryButton sx={styles.btn} onClick={handleClose}>
          Đã hiểu
        </PrimaryButton>
      }
      styles={{ content: styles.dialogContent, actions: styles.actions }}
    />
  );
}

export default ErrorDialog;
