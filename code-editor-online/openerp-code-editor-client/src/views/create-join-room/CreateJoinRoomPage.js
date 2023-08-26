/* eslint-disable no-unused-vars */
// import { LoadingButton } from "@mui/lab";
import { LoadingButton } from "@mui/lab";
import { Button, Card, CardActionArea, Grid, TextField, Typography } from "@mui/material";
import { request } from "api";
import React, { useEffect } from "react";
import { useState } from "react";
import { useForm } from "react-hook-form";
import { useHistory } from "react-router-dom";
import { errorNoti, successNoti } from "utils/notification";

const CreateJoinRoomPage = () => {
  const history = useHistory();
  const [loadingJoinRoom, setLoadingJoinRoom] = useState(false);
  const [loadingCreateRoom, setLoadingCreateRoom] = useState(false);
  const {
    handleSubmit: handleSubmitCreateRoom,
    formState: { errors: errorsCreateRoom },
    register: registerCreateRoom,
  } = useForm();
  const {
    handleSubmit: handleSubmitJoinRoom,
    formState: { errors: errorsJoinRoom },
    register: registerJoinRoom,
  } = useForm();
  const handleCreateRoom = (data) => {
    setLoadingCreateRoom(true);
    request(
      "post",
      "/code-editor/rooms",
      (response) => {
        if (response && response.status === 200) {
          successNoti("Tạo phòng thành công", true);
          history.push(`/code-editor/room/${response.data.id}`);
        }
      },
      (error) => {
        console.log(error);
      },
      data
    );
  };
  const handleJoinRoom = (data) => {
    setLoadingJoinRoom(true);
    request(
      "get",
      `/code-editor/rooms/${data.roomId}`,
      (response) => {
        setLoadingJoinRoom(false);
        if (response && response.status === 200) {
          history.push(`/code-editor/room/${response.data.id}`);
        }
      },
      {
        400: (e) => {
          setLoadingJoinRoom(false);
          errorNoti("Id phòng không đúng. Vui lòng thử lại", true);
        },
      }
    );
  };
  const syncUser = () => {
    request("get", `/code-editor/users/sync-user`);
  };
  useEffect(() => {
    syncUser();
  },[]);
  return (
    <div>
      <Grid
        container
        sx={{ height: "80vh" }}
        spacing={2}
        justifyContent="center"
        alignContent="center"
      >
        <Grid item xs={12} md={6} container justifyContent="center">
          <Card sx={{ padding: "2rem" }}>
            <Typography variant="h5" textAlign="center">
              Tạo phòng mới
            </Typography>
            <br />
            <form id="create-code-editor-room" onSubmit={handleSubmitCreateRoom(handleCreateRoom)}>
              <TextField
                {...registerCreateRoom("roomName", { required: "Vui lòng nhập tên phòng" })}
                error={!!errorsCreateRoom?.roomName}
                aria-invalid={errorsCreateRoom?.roomName ? "true" : "false"}
                helperText={errorsCreateRoom?.roomName?.message}
                fullWidth
                label="Tên phòng"
              />
              <LoadingButton
                variant="contained"
                fullWidth
                type="submit"
                sx={{ marginTop: "20px" }}
                loading={loadingCreateRoom}
              >
                Tạo phòng ngay
              </LoadingButton>
            </form>
          </Card>
        </Grid>
        <Grid item xs={12} md={6} container justifyContent="center">
          <Card sx={{ padding: "2rem" }}>
            <Typography variant="h5" textAlign="center">
              Tham gia phòng
            </Typography>
            <br />
            <form id="create-code-editor-room" onSubmit={handleSubmitJoinRoom(handleJoinRoom)}>
              <TextField
                {...registerJoinRoom("roomId", { required: "Vui lòng nhập id phòng" })}
                error={!!errorsJoinRoom?.roomId}
                aria-invalid={errorsCreateRoom?.roomId ? "true" : "false"}
                helperText={errorsJoinRoom?.roomId?.message}
                fullWidth
                label="Id phòng"
              />
              <LoadingButton
                variant="contained"
                fullWidth
                type="submit"
                sx={{ marginTop: "20px" }}
                loading={loadingJoinRoom}
              >
                Tham gia ngay
              </LoadingButton>
            </form>
          </Card>
        </Grid>
      </Grid>
    </div>
  );
};

export default CreateJoinRoomPage;
