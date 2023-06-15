import { Mic, MicOff } from "@mui/icons-material";
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
  const { socket, stream, muted, fullName, audio, video, socketId } = props;
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
          whiteSpace: "nowrap",
        }}
      >
        {fullName ? fullName : "Bạn"}
      </div>
      <Tooltip
        title={
          roomMaster?.id === token?.preferred_username
            ? "Bạn có thể tắt mic của người này"
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
            // edge="end"
            style={{ color: "white" }}
            disabled={roomMaster?.id !== token?.preferred_username}
            onClick={() => {
              handleMuteRemoteMic(socketId, !audio);
            }}
          >
            {audio ? <Mic fontSize="small" /> : <MicOff fontSize="small" />}
          </IconButton>
        </div>
      </Tooltip>
    </div>
  );
};
export default VideoCard;
