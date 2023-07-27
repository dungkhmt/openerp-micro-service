import { Mic, MicOff, RemoveCircleOutline } from "@mui/icons-material";
import { Avatar, IconButton, Tooltip } from "@mui/material";
import { useKeycloak } from "@react-keycloak/web";
import React from "react";
import { useEffect } from "react";
import { useRef } from "react";
import { useDispatch, useSelector } from "react-redux";
import { CHARACTER_COLOR, SOCKET_EVENTS } from "utils/constants";
import {
  handleOnOffMicParticipant,
  handleOnOffMicRemoteUser,
} from "../reducers/codeEditorReducers";

const VideoCard = (props) => {
  const { socket, stream, muted, fullName, audio, video, socketId, isLocal } = props;
  const dispatch = useDispatch();
  const remoteVideoRef = useRef(null);
  const { roomMaster } = useSelector((state) => state.codeEditor);
  const { keycloak } = useKeycloak();
  const token = keycloak.tokenParsed;
  useEffect(() => {
    remoteVideoRef.current.srcObject = stream;
  }, [stream]);
  const handleMuteRemoteMic = (socketId, audio) => {
    if (socket.current) {
      socket.current.emit(SOCKET_EVENTS.REQUEST_ON_OFF_MIC, { socketId, audio });
    }

    dispatch(
      handleOnOffMicParticipant({
        socketId: socketId,
        audio: !audio,
      })
    );
    dispatch(
      handleOnOffMicRemoteUser({
        socketId: socketId,
        audio: !audio,
      })
    );
  };
  const handleRemoveParticipant = (socketId) => {
    if (socket.current) {
      socket.current.emit(SOCKET_EVENTS.REQUEST_REMOVE_PARTICIPANT, { socketId });
    }
  };
  return (
    <div
      style={{
        position: "relative",
        width: "100%",
        backgroundColor: !video && "#000000",
        marginBottom: "3px",
      }}
    >
      <video ref={remoteVideoRef} muted={muted} autoPlay style={{ width: "100%" }}></video>
      <div
        style={{
          position: "absolute",
          bottom: "10%",
          left: "2%",
          color: "white",
          textAlign: "center",
          fontSize: "12px",
          overflow: "hidden",
          textOverflow: "ellipsis",
          whiteSpace: "wrap",
          zIndex: 2,
        }}
      >
        {fullName ? fullName : "Bạn"}
      </div>
      {!video && (
        <div
          style={{
            position: "absolute",
            backgroundColor: "#000000",
            top: 0,
            left: 0,
            height: "100%",
            width: "100%",
            display: "flex",
            justifyContent: "center",
            alignItems: "center",
          }}
        >
          <Avatar
            sx={{
              bgcolor: CHARACTER_COLOR[fullName?.split(" ").at(-1)?.toUpperCase()[0]],
            }}
          >
            {fullName?.split(" ").at(-1)?.toUpperCase()[0]}
          </Avatar>
        </div>
      )}
      {!isLocal && (
        <>
          <Tooltip
            title={
              roomMaster?.id === token?.preferred_username
                ? "Tắt mic của người này"
                : "Bạn không thể tắt mic của người này"
            }
            placement="left"
          >
            <div
              style={{
                position: "absolute",
                bottom: "5%",
                right: "5px",
                zIndex: 2,
              }}
            >
              <IconButton
                style={{
                  color: roomMaster?.id !== token?.preferred_username ? "#bdbbbb" : "#ffffff",
                }}
                color="primary"
                size="small"
                disabled={roomMaster?.id !== token?.preferred_username}
                onClick={() => {
                  handleMuteRemoteMic(socketId, !audio);
                }}
              >
                {audio ? <Mic fontSize="small" /> : <MicOff fontSize="small" />}
              </IconButton>
            </div>
          </Tooltip>
          <Tooltip
            title={
              roomMaster?.id === token?.preferred_username
                ? "Loại bỏ người này"
                : "Bạn không thể loại bỏ người này"
            }
            placement="left"
          >
            <div
              style={{
                position: "absolute",
                bottom: "25%",
                right: "5px",
                zIndex: 2,
              }}
            >
              <IconButton
                style={{
                  color: roomMaster?.id !== token?.preferred_username ? "#bdbbbb" : "#ffffff",
                }}
                size="small"
                disabled={roomMaster?.id !== token?.preferred_username}
                onClick={() => {
                  // handleMuteRemoteMic(socketId, !audio);
                  handleRemoveParticipant(socketId);
                }}
              >
                <RemoveCircleOutline />
              </IconButton>
            </div>
          </Tooltip>
        </>
      )}
    </div>
  );
};
export default VideoCard;
