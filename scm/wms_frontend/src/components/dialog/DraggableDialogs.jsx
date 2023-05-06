import {
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogContentText,
  DialogTitle,
  Paper,
} from "@mui/material";
import Draggable from "react-draggable";
import { AppColors } from "../../shared/AppColors";

function PaperComponent(props) {
  return (
    <Draggable
      handle="#draggable-dialog-title"
      cancel={'[class*="MuiDialogContent-root"]'}
    >
      <Paper {...props} />
    </Draggable>
  );
}
/**
 * @typedef Prop
 * @property {boolean} open
 * @property {Function} handleOpen
 * @property {Function} callBack
 * @property {boolean} disable
 * @property {string} title
 * @property {string} message
 * @param {Prop} props
 */
export default function DraggableDeleteDialog(props) {
  const { open, handleOpen, title, message, callback, disable } = props;
  return (
    <Dialog
      open={open}
      PaperComponent={PaperComponent}
      aria-labelledby="draggable-dialog-title"
    >
      <DialogTitle
        style={{ cursor: "move", color: AppColors.secondary }}
        id="draggable-dialog-title"
      >
        {title ? title : "Xác nhận xóa"}
      </DialogTitle>
      <DialogContent>
        <DialogContentText>
          {message ? message : "Bạn có chắn chắn muốn xóa bản ghi này?"}
        </DialogContentText>
      </DialogContent>
      <DialogActions>
        <Button
          disabled={disable}
          autoFocus
          onClick={() => {
            handleOpen();
            callback(false);
          }}
        >
          Hủy
        </Button>
        <Button
          disabled={disable}
          onClick={() => {
            callback(true);
          }}
        >
          Xác nhận
        </Button>
      </DialogActions>
    </Dialog>
  );
}
