/* eslint-disable no-unused-vars */
/* eslint-disable react-hooks/exhaustive-deps */
import { Grid, Typography } from "@mui/material";
import React, { useEffect, useRef, useState } from "react";
import NavBarRoom from "./components/NavBarRoom";
import { useParams } from "react-router-dom";
import { useDispatch, useSelector } from "react-redux";
import { SOCKET_EVENTS } from "utils/constants";
import Peer from "simple-peer";
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

const CodeEditorPage = () => {
  const { id: roomId } = useParams();
  const dispatch = useDispatch();
  const { isVisibleParticipants } = useSelector((state) => state.codeEditor);
  const [roomName, setRoomName] = useState();
  const { keycloak } = useKeycloak();
  const token = keycloak.tokenParsed;
  const socketRef = useRef();
  useEffect(() => {
    socketRef.current = io(process.env.CODE_EDITOR_ONLINE_SERVER_DOMAIN || "http://localhost:7008");
    if (socketRef.current) {
      socketRef.current.on(SOCKET_EVENTS.JOINED, ({ fullName, socketId, clients }) => {
        if (socketRef.current.id !== socketId) {
          successNoti(`${fullName} đã tham gia vào phòng`, true);
        }
        dispatch(setParticipants(clients));
      });

      socketRef.current.on(SOCKET_EVENTS.LEAVE_ROOM, ({ fullName, socketId, clients }) => {
        errorNoti(`${fullName} đã rời phòng`, true);
        dispatch(setParticipants(clients.filter((client) => client.socketId !== socketId)));
      });
    }
    return () => {
      socketRef.current.disconnect();
      socketRef.current.off(SOCKET_EVENTS.JOINED);
      socketRef.current.off(SOCKET_EVENTS.LEAVE_ROOM);
    };
  }, []);

  useEffect(() => {
    if (socketRef.current) {
      socketRef.current.emit(SOCKET_EVENTS.CONNECT_TO_EDITOR, {
        roomId: roomId,
        fullName: token.name,
      });
    }
  }, [roomId, token]);

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
  return (
    <div>
      <Typography variant="h4">{roomName}</Typography>
      <br />
      <NavBarRoom socket={socketRef} />
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
