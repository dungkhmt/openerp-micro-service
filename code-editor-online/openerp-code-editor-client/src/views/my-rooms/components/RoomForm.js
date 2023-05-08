import {
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  TextField,
} from "@mui/material";
import React from "react";
import { useForm } from "react-hook-form";
import { useDispatch, useSelector } from "react-redux";
import { setIsVisibleRoomForm, setReloadData, setSelectedRoom } from "../reducers/myRoomsReducers";
import { request } from "api";
import { successNoti } from "utils/notification";
import { useHistory } from "react-router-dom";

const RoomForm = () => {
  const { isVisibleRoomForm, selectedRoom } = useSelector((state) => state.myRooms);
  const dispatch = useDispatch();
  const history = useHistory();
  const handleClose = () => {
    dispatch(setSelectedRoom(null));
    reset();
    dispatch(setIsVisibleRoomForm(false));
  };
  const {
    handleSubmit,
    register,
    formState: { errors },
    reset,
  } = useForm();
  const handleSave = (data) => {
    if (selectedRoom?.id) {
      request(
        "put",
        `/code-editor/rooms/${selectedRoom.id}`,
        (response) => {
          if (response && response.status === 200) {
            successNoti("Chỉnh sửa thông tin phòng thành công", true);
            handleClose();
            dispatch(setReloadData(Math.random()));
          }
        },
        (error) => {
          console.log(error);
        },
        data
      );
    } else {
      request(
        "post",
        "/code-editor/rooms",
        (response) => {
          if (response && response.status === 200) {
            successNoti("Tạo phòng thành công", true);
            history.push(`/code-editor/room/${response.data.id}`);
            handleClose();
          }
        },
        (error) => {
          console.log(error);
        },
        data
      );
    }
  };
  return (
    <Dialog open={isVisibleRoomForm} onClose={handleClose} maxWidth="sm" fullWidth>
      <DialogTitle>{selectedRoom?.id ? "Chỉnh sửa thông tin phòng" : "Thêm mới phòng"}</DialogTitle>
      <form onSubmit={handleSubmit(handleSave)}>
        <DialogContent>
          <TextField
            {...register("roomName", { required: "Vui lòng nhập tên phòng" })}
            error={!!errors.roomName}
            helperText={errors.roomName?.message}
            defaultValue={selectedRoom?.roomName}
            fullWidth
            label="Tên phòng"
          />
        </DialogContent>
        <DialogActions>
          <Button variant="outlined" onClick={handleClose}>
            Hủy
          </Button>
          <Button variant="contained" type="submit">
            Lưu
          </Button>
        </DialogActions>
      </form>
    </Dialog>
  );
};

export default RoomForm;
