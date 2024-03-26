import {
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogContentText,
  DialogTitle,
} from "@mui/material";

const styles = {
  title: {
    fontSize: 25,
  },
  content: {
    color: "black",
  },
};

const DeleteDialog = ({ open, handleDelete, handleClose }) => {
  return (
    <Dialog open={open} onClose={handleClose}>
      <DialogTitle style={styles.title}>Xóa dữ liệu</DialogTitle>
      <DialogContent>
        <DialogContentText style={styles.content}>
          Bạn có chắc muốn xóa dữ liệu này? Hành động này không thể hoàn tác.
        </DialogContentText>
      </DialogContent>
      <DialogActions>
        <Button color="primary" onClick={handleClose}>
          Hủy
        </Button>
        <Button variant="contained" color="error" onClick={handleDelete}>
          Xóa
        </Button>
      </DialogActions>
    </Dialog>
  );
};

export default DeleteDialog;
