/* eslint-disable no-unused-vars */
/* eslint-disable react-hooks/exhaustive-deps */
import { Grid, Typography, useForkRef } from "@mui/material";
import React, { useEffect, useRef, useState } from "react";
import NavBarRoom from "./components/NavBarRoom";
import { useParams } from "react-router-dom";
import { useDispatch, useSelector } from "react-redux";
import { SOCKET_EVENTS } from "utils/constants";
import { request } from "api";

import { errorNoti, successNoti } from "utils/notification";
import { io } from "socket.io-client";
import CodeEditor from "./components/CodeEditor";
import Participants from "./components/Participants";
import { useKeycloak } from "@react-keycloak/web";
import { setParticipants } from "./reducers/codeEditorReducers";
import SplitterLayout from "react-splitter-layout";
import "react-splitter-layout/lib/index.css";
import InputOutputCard from "./components/InputOuputCard";
import "./style.css";
import Peer from "peerjs";

const CodeEditorPage = () => {
  const { id: roomId } = useParams();
  const dispatch = useDispatch();
  const { isVisibleParticipants, isMute } = useSelector((state) => state.codeEditor);
  const [roomName, setRoomName] = useState();
  const { keycloak } = useKeycloak();
  const token = keycloak.tokenParsed;
  const socketRef = useRef();
  const localAudioRef = useRef();
  const remoteAudioRef = useRef();
  const myPeer = useRef();

  useEffect(() => {
    socketRef.current = io(process.env.CODE_EDITOR_ONLINE_SERVER_DOMAIN || "http://localhost:7008");
    navigator.mediaDevices.getUserMedia({ video: false, audio: true }).then((stream) => {
      myPeer.current = new Peer();
      myPeer.current.on("open", (id) => {
        if (socketRef.current) {
          socketRef.current.emit(SOCKET_EVENTS.CONNECT_TO_EDITOR, {
            roomId: roomId,
            fullName: token.name,
            peerId: id,
          });
        }
      });
      if (socketRef.current) {
        socketRef.current.on(SOCKET_EVENTS.JOINED, ({ fullName, socketId, peerId, clients }) => {
          if (socketRef.current.id !== socketId) {
            successNoti(`${fullName} đã tham gia vào phòng`, true);
          }
          dispatch(setParticipants(clients));
          localAudioRef.current.srcObject = stream;

          myPeer.current.on("call", (call) => {
            call.answer(stream);
            call.on("stream", (userAudioStream) => {
              remoteAudioRef.current.srcObject = userAudioStream;
            });
          });

          const call = myPeer.current.call(peerId, stream);

          call.on("stream", (userVideoStream) => {
            remoteAudioRef.current.srcObject = userVideoStream;
          });
        });

        socketRef.current.on(SOCKET_EVENTS.LEAVE_ROOM, ({ fullName, socketId, clients }) => {
          errorNoti(`${fullName} đã rời phòng`, true);
          dispatch(setParticipants(clients.filter((client) => client.socketId !== socketId)));
        });
      }
    });

    return () => {
      socketRef.current.disconnect();
      socketRef.current.off(SOCKET_EVENTS.JOINED);
      socketRef.current.off(SOCKET_EVENTS.LEAVE_ROOM);
    };
  }, []);

  /**
   * Get basic information about room as id, name...
   * @param {*} id of room
   */
  const getRoomById = (id) => {
    request(
      "get",
      `/code-editor/rooms/${id}`,
      (response) => {
        if (response && response.status === 200) {
          setRoomName(response.data?.roomName);
        }
      },
      {
        400: (e) => {
          errorNoti("Id phòng không đúng. Vui lòng thử lại", true);
        },
      }
    );
  };
  useEffect(() => {
    getRoomById(roomId);
  }, [roomId]);

  useEffect(() => {
    if(localAudioRef.current.srcObject && localAudioRef.current.srcObject.getAudioTracks().length > 0){
      localAudioRef.current.srcObject.getAudioTracks()[0].enabled = !isMute;

    }
  }, [isMute]);
  return (
    <div>
      <audio ref={localAudioRef} muted autoPlay></audio>
      <audio ref={remoteAudioRef} autoPlay></audio>
      <div id="code-editor-audio"></div>
      <Typography variant="h4">{roomName}</Typography>
      <br />
      <NavBarRoom socket={socketRef} myPeer={myPeer} />
      <Grid container spacing={2}>
        <Grid
          item
          xs={isVisibleParticipants ? 10 : 12}
          sx={{ position: "relative", height: "100vh" }}
        >
          <SplitterLayout vertical>
            <CodeEditor socket={socketRef} roomId={roomId} />
            <Grid container spacing={2}>
              <Grid item xs={12}>
                <InputOutputCard />
              </Grid>
            </Grid>
          </SplitterLayout>
        </Grid>
        <Grid hidden={!isVisibleParticipants} item xs={isVisibleParticipants ? 2 : 0}>
          <Participants />
        </Grid>
      </Grid>
    </div>
  );
};

export default CodeEditorPage;
