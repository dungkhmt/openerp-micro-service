/* eslint-disable react-hooks/exhaustive-deps */
/* eslint-disable no-unused-vars */
import {
  Avatar,
  Grid,
  IconButton,
  List,
  ListItem,
  ListItemAvatar,
  ListItemText,
  Paper,
  Tooltip,
} from "@mui/material";
import React, { useEffect } from "react";
import CircleIcon from "@mui/icons-material/Circle";
import { useDispatch, useSelector } from "react-redux";
import { Mic, MicOff } from "@mui/icons-material";
import randomColor from "randomcolor";
import { CHARACTER_COLOR, SOCKET_EVENTS } from "utils/constants";
import { useKeycloak } from "@react-keycloak/web";
import { useState } from "react";
import {
  handleOnOffCamera,
  handleOnOffMic,
  handleOnOffMicParticipant,
  setState,
} from "../reducers/codeEditorReducers";
import VideoCard from "./VideoCard";

const Participants = (props) => {
  const { socket } = props;
  const { isMute, isShowCamera, remoteUsers, localVideo } = useSelector(
    (state) => state.codeEditor
  );
  const dispatch = useDispatch();

  useEffect(() => {
    // if (localVideo && localVideo?.getAudioTracks().length > 0) {
    //   localVideo.getAudioTracks()[0].enabled = !isMute;
    // }
    dispatch(handleOnOffMic({ isMute: !isMute }));
  }, [isMute]);
  useEffect(() => {
    // if (isShowCamera && localVideo) {
    //   localVideo.getVideoTracks().forEach((track) => {
    //     track.enabled = true;
    //   });
    // } else if (!isShowCamera && localVideo) {
    //   localVideo.getVideoTracks().forEach((track) => {
    //     track.enabled = false;
    //     // track.stop();
    //   });
    // }
    if (localVideo) {
      dispatch(handleOnOffCamera({ isShowCamera: isShowCamera }));
    }
  }, [isShowCamera, localVideo]);
  return (
    <Paper elevation={3} sx={{ marginTop: "8px" }}>
      <Grid container spacing={1} justifyContent="center" alignItems="center" flexWrap="nowrap">
        <CircleIcon color="success" fontSize="10px" />
        <strong
          style={{
            fontSize: "1rem",
            overflow: "hidden",
            textOverflow: "ellipsis",
            whiteSpace: "nowrap",
          }}
        >
          Người tham gia
        </strong>
      </Grid>

      <List>
        {/* <video ref={localVideo} muted autoPlay style={{ width: "100%" }}></video> */}
        <VideoCard
          socket={socket}
          stream={localVideo}
          muted={true}
          audio={!isMute}
          video={isShowCamera}
          socketId={socket.current?.id}
        />
        {/* <VideoCard
          socket={socket}
          stream={localVideo}
          muted={true}
          audio={!isMute}
          video={isShowCamera}
          socketId={socket.current?.id}
        /> */}
        {remoteUsers?.map((user) => {
          return (
            <VideoCard
              socket={socket}
              stream={user?.media}
              muted={false}
              fullName={user?.fullName}
              audio={user?.audio}
              video={user?.video}
              socketId={user?.socketId}
            />
          );
        })}
        {/* {remoteVideos?.filter((value, index, self) => {
            const firstIndex = self.findIndex((item) => item.id === value.id);
            return index === firstIndex;
          })
          ?.map((remoteVideo) => {
            return <VideoCard stream={remoteVideo} muted={false} />;
          })} */}
        {/* {participants.map((value) => {
          return (
            <ListItem
              key={value.socketId}
              secondaryAction={
                <Tooltip
                  title={
                    roomMaster.id === token.preferred_username
                      ? "Bạn có thể tắt mic của người này"
                      : "Bạn không thể tắt mic của người này"
                  }
                  placement="left"
                >
                  <IconButton
                    edge="end"
                    disabled={roomMaster.id !== token.preferred_username}
                    onClick={() => {
                      handleMuteRemoteMic(value.socketId, !value.audio);
                    }}
                  >
                    {value.audio ? <Mic fontSize="small" /> : <MicOff fontSize="small" />}
                  </IconButton>
                </Tooltip>
              }
            >
              <ListItemAvatar>
                <Avatar
                  sx={{
                    bgcolor: CHARACTER_COLOR[value.fullName.split(" ").at(-1)?.toUpperCase()[0]],
                  }}
                >{`${value.fullName.split(" ").at(-1)?.toUpperCase()[0]}`}</Avatar>
              </ListItemAvatar>
              <div style={{ overflow: "hidden", textOverflow: "ellipsis", whiteSpace: "nowrap" }}>
                {value.fullName}
              </div>
            </ListItem>
          );
        })} */}
      </List>
    </Paper>
  );
};

export default Participants;
