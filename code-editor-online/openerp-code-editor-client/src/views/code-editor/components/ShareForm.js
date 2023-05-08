import {
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  TextField,
} from "@mui/material";
import React from "react";
import { useDispatch, useSelector } from "react-redux";
import { setIsVisibleShareForm } from "../reducers/codeEditorReducers";
import { successNoti } from "utils/notification";
import { useParams } from "react-router-dom";
import copy from "copy-to-clipboard";

const ShareForm = () => {
  const dispatch = useDispatch();
  const { id: roomId } = useParams();
  const { isVisibleShareForm } = useSelector((state) => state.codeEditor);
  const handleClose = () => {
    dispatch(setIsVisibleShareForm(false));
  };
  const handleShare = () => {
    copy(roomId);
    successNoti("Đã copy id phòng", true);
    handleClose();
  };
  return (
    <Dialog open={isVisibleShareForm} onClose={handleClose} maxWidth="sm" fullWidth>
      <DialogTitle>Chia sẻ id phòng</DialogTitle>
      <form>
        <DialogContent>
          <TextField
            value={roomId}
            autoFocus
            label="Id phòng"
            fullWidth
            InputProps={{
              readOnly: true,
            }}
          />
        </DialogContent>
      </form>

      <DialogActions>
        <Button variant="outlined" onClick={handleClose}>
          Hủy
        </Button>
        <Button variant="contained" onClick={handleShare}>
          Chia sẻ
        </Button>
      </DialogActions>
    </Dialog>
  );
};

export default ShareForm;
