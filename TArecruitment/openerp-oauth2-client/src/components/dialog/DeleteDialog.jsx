import { Typography, Button } from "@mui/material";
import PrimaryButton from "../button/PrimaryButton";
import CustomizedDialogs from "./CustomizedDialogs";

const styles = {
  btn: { width: 120, marginLeft: "8px" },
  dialogContent: (theme) => ({ width: 550 }),
  actions: (theme) => ({ paddingRight: theme.spacing(2) }),
};

function DeleteDialog({ open, handleDelete, handleClose }) {
  return (
    <CustomizedDialogs
      open={open}
      handleClose={handleClose}
      title="Xóa dữ liệu"
      centerTitle
      contentTopDivider
      content={
        <Typography
          color="textSecondary"
          gutterBottom
          style={{
            padding: 8,
            display: "flex",
            justifyContent: "center",
            alignItems: "center",
            fontSize: 20,
          }}
        >
          Bạn có chắc chắn muốn xóa dữ liệu này không?
        </Typography>
      }
      actions={
        <div>
          <Button variant="contained" sx={styles.btn} onClick={handleClose}>
            Hủy
          </Button>
          <Button
            variant="contained"
            color="error"
            sx={styles.btn}
            onClick={handleDelete}
          >
            Xác nhận
          </Button>
        </div>
      }
      styles={{ content: styles.dialogContent, actions: styles.actions }}
    />
  );
}

export default DeleteDialog;
