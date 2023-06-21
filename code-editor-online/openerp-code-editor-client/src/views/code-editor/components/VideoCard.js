import { Mic, MicOff, RemoveCircleOutline } from "@mui/icons-material";
import { IconButton, Tooltip } from "@mui/material";
import { useKeycloak } from "@react-keycloak/web";
import React from "react";
import { useEffect } from "react";
import { useRef } from "react";
import { useDispatch, useSelector } from "react-redux";
import { SOCKET_EVENTS } from "utils/constants";
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
    <div style={{ position: "relative", width: "100%" }}>
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
        }}
      >
        {fullName ? fullName : "Bạn"}
      </div>
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
                right: "1%",
              }}
            >
              <IconButton
                style={{
                  color: roomMaster?.id !== token?.preferred_username ? "#bdbbbb" : "#ffffff",
                }}
                color="primary"
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
                bottom: "22%",
                right: "1%",
              }}
            >
              <IconButton
                style={{
                  color: roomMaster?.id !== token?.preferred_username ? "#bdbbbb" : "#ffffff",
                }}
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
