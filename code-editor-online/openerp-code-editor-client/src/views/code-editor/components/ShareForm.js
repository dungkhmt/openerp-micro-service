import {
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  Grid,
  ListItem,
  ListItemAvatar,
  MenuItem,
  Select,
} from "@mui/material";
import React from "react";
import { useDispatch, useSelector } from "react-redux";
import { setState } from "../reducers/codeEditorReducers";
import { errorNoti, successNoti } from "utils/notification";
import { useParams } from "react-router-dom";
import copy from "copy-to-clipboard";
import AllowedUserList from "./AllowedUserList";
import { Lock, Public } from "@mui/icons-material";
import { ACCESS_PERMISSION } from "../utils/constants";
import { request } from "api";
import { useState } from "react";
import AddAllowedUserForm from "./AddAllowedUserForm";
import { SOCKET_EVENTS } from "utils/constants";
import { useEffect } from "react";

const ShareForm = (props) => {
  const { socket } = props;
  const dispatch = useDispatch();
  const { id: roomId } = useParams();

  const { isVisibleShareForm, roomName, isPublic, roomAccessPermission, reloadAllowedUser } =
    useSelector((state) => state.codeEditor);
  const [openAddUserForm, setOpenAddUserForm] = useState(false);
  const handleClose = () => {
    dispatch(
      setState({
        isVisibleShareForm: false,
        reloadAllowedUser: Math.random(),
      })
    );
  };
  const handleShare = (roomId) => {
    copy(roomId);
    successNoti("Đã sao chép mã phòng", true);
  };

  const getAllowedUserList = (roomId) => {
    request("get", `/code-editor/rooms/${roomId}/shared-users`, (response) => {
      if (response && response.status === 200) {
        dispatch(setState({ allowedUserList: response.data }));
      }
    });
  };
  useEffect(() => {
    getAllowedUserList(roomId);
  }, [reloadAllowedUser]);

  const handleUpdateGeneralPermission = (roomId, isPublic, accessPermission) => {
    const data = {
      isPublic,
      accessPermission,
    };
    request(
      "put",
      `/code-editor/rooms/${roomId}`,
      (response) => {
        if (response && response.status === 200) {
          successNoti("Đã cập nhật quyền truy cập", true);
          socket.current.emit(SOCKET_EVENTS.UPDATE_ROOM_PERMISSION, {
            roomId,
            isPublic,
            accessPermission,
          });
        }
      },
      (error) => {
        errorNoti("Cập nhật quyền truy cập thất bại. Vui lòng thử lại", true);
      },
      data
    );
  };
  return (
    <>
      <Dialog open={isVisibleShareForm} onClose={handleClose} maxWidth="sm" fullWidth>
        <DialogTitle>
          <Grid container justifyContent="space-between">
            <Grid item xs={9} container>
              Chia sẻ "{roomName}"
            </Grid>
            <Grid item xs={3} container justifyContent="end">
              <Button
                variant="contained"
                color="success"
                onClick={() => {
                  setOpenAddUserForm(true);
                  handleClose();
                }}
              >
                Thêm người
              </Button>
            </Grid>
          </Grid>
        </DialogTitle>
        <DialogContent>
          <b>Những người có quyền truy cập</b>
          <AllowedUserList socket={socket} />
          <b>Quyền truy cập chung</b>
          <ListItem
            secondaryAction={
              isPublic && (
                <Grid item container justifyContent="end">
                  <Select
                    size="small"
                    defaultValue={ACCESS_PERMISSION.VIEWER.value}
                    value={roomAccessPermission}
                    sx={{
                      boxShadow: "none",
                      ".MuiOutlinedInput-notchedOutline": { border: "none" },
                    }}
                    onChange={(e) => {
                      dispatch(
                        setState({
                          roomAccessPermission: e.target.value,
                        })
                      );
                      handleUpdateGeneralPermission(roomId, isPublic, e.target.value);
                    }}
                  >
                    <MenuItem value={ACCESS_PERMISSION.VIEWER.value}>
                      {ACCESS_PERMISSION.VIEWER.label}
                    </MenuItem>
                    <MenuItem value={ACCESS_PERMISSION.EDITOR.value}>
                      {ACCESS_PERMISSION.EDITOR.label}
                    </MenuItem>
                  </Select>
                </Grid>
              )
            }
          >
            <ListItemAvatar>{isPublic ? <Public /> : <Lock />}</ListItemAvatar>
            <div>
              <Select
                size="small"
                defaultValue={isPublic}
                sx={{
                  boxShadow: "none",
                  ".MuiOutlinedInput-notchedOutline": { border: "none" },
                  ".MuiOutlinedInput-input": { paddingLeft: "0px" },
                }}
                onChange={(e) => {
                  dispatch(
                    setState({
                      isPublic: e.target.value,
                      roomAccessPermission: ACCESS_PERMISSION.VIEWER.value,
                    })
                  );
                  handleUpdateGeneralPermission(roomId, e.target.value, roomAccessPermission);
                }}
              >
                <MenuItem value={false}>Hạn chế</MenuItem>
                <MenuItem value={true}>Bất kỳ ai có mã phòng</MenuItem>
              </Select>
              {isPublic ? (
                <div>Bất kỳ ai có mã phòng đều có thể xem</div>
              ) : (
                <div>Chỉ những người có quyền truy cập mới có thể tham gia phòng</div>
              )}
            </div>
          </ListItem>
        </DialogContent>

        <DialogActions sx={{ padding: "0 20px 20px 20px" }}>
          <Grid container justifyContent="space-between">
            <Button
              variant="outlined"
              onClick={() => {
                handleShare(roomId);
              }}
            >
              Sao chép mã phòng
            </Button>
            <Button
              variant="contained"
              onClick={() => {
                handleClose();
              }}
            >
              Lưu
            </Button>
          </Grid>
        </DialogActions>
      </Dialog>
      <AddAllowedUserForm open={openAddUserForm} setOpen={setOpenAddUserForm} roomId={roomId} />
    </>
  );
};

export default ShareForm;
