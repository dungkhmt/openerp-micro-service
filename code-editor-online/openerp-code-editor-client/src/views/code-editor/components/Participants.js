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
import React from "react";
import CircleIcon from "@mui/icons-material/Circle";
import { useDispatch, useSelector } from "react-redux";
import { Mic, MicOff } from "@mui/icons-material";
import randomColor from "randomcolor";
import { CHARACTER_COLOR, SOCKET_EVENTS } from "utils/constants";
import { useKeycloak } from "@react-keycloak/web";
import { useState } from "react";
import { handleOnOffMicParticipant, setState } from "../reducers/codeEditorReducers";

const Participants = (props) => {
  const { socket } = props;
  const { participants, roomMaster } = useSelector((state) => state.codeEditor);
  const { keycloak } = useKeycloak();
  const token = keycloak.tokenParsed;
  const dispatch = useDispatch();

  const handleMuteRemoteMic = (socketId, audio) => {
    if (socket.current) {
      socket.current.emit(SOCKET_EVENTS.REQUEST_ON_OFF_MIC, { socketId, audio });
    }

    dispatch(
      handleOnOffMicParticipant({
        socketId: socketId,
        audio: audio ,
      })
    );
  };
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
        {participants.map((value) => {
          return (
            <ListItem
              key={value.socketId}
              secondaryAction={
                roomMaster.id === token.preferred_username && (
                  <IconButton
                    edge="end"
                    onClick={() => {
                      handleMuteRemoteMic(value.socketId, !value.audio);
                    }}
                  >
                    {value.audio ? <Mic fontSize="small" /> : <MicOff fontSize="small" />}
                  </IconButton>
                )
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
        })}
      </List>
    </Paper>
  );
};

export default Participants;
