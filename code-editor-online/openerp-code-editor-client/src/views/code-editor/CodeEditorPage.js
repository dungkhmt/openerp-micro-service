/* eslint-disable no-unused-vars */
/* eslint-disable react-hooks/exhaustive-deps */
import { Grid, Typography, useForkRef } from "@mui/material";
import React, { useEffect, useRef, useState } from "react";
import NavBarRoom from "./components/NavBarRoom";
import { useHistory, useParams } from "react-router-dom";
import { useDispatch, useSelector } from "react-redux";
import { SOCKET_EVENTS } from "utils/constants";
import { request } from "api";

import { errorNoti, successNoti } from "utils/notification";
import { io } from "socket.io-client";
import CodeEditor from "./components/CodeEditor";
import Participants from "./components/Participants";
import { useKeycloak } from "@react-keycloak/web";
import { setParticipants, setRoomName, setState } from "./reducers/codeEditorReducers";
import SplitterLayout from "react-splitter-layout";
import "react-splitter-layout/lib/index.css";
import InputOutputCard from "./components/InputOuputCard";
import "./style.css";
import Peer from "peerjs";
import NotFound from "views/errors/NotFound";
import NotAccess from "./components/NotAccess";

const CodeEditorPage = () => {
  const { id: roomId } = useParams();
  const dispatch = useDispatch();
  const {
    isVisibleParticipants,
    isMute,
    roomName,
    reloadAllowedUser,
    isPublic,
    roomAccessPermission,
    allowedUserList,
  } = useSelector((state) => state.codeEditor);
  const [roomMasterId, setRoomMasterId] = useState();
  const { keycloak } = useKeycloak();
  const token = keycloak.tokenParsed;
  const socketRef = useRef();
  const localAudioRef = useRef();
  const remoteAudioRef = useRef();
  const myPeer = useRef();
  const [isAccess, setIsAccess] = useState();

  useEffect(() => {
    if (isAccess) {
      socketRef.current = io(
        process.env.REACT_APP_CODE_EDITOR_ONLINE_SERVER_DOMAIN || "http://localhost:7008"
      );
      navigator.mediaDevices.getUserMedia({ video: false, audio: true }).then((stream) => {
        // Create a peer connection
        myPeer.current = new Peer();

        // Get id of peer connection and send to server and broadcast to others user
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
    }
  }, [isAccess]);

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

  useEffect(() => {
    const user = allowedUserList.find((item) => item.id.userId === token.preferred_username);
    if (token.preferred_username === roomMasterId) {
      setIsAccess(true);
    } else if (!isPublic && !user) {
      setIsAccess(false);
    } else {
      setIsAccess(true);
    }
  }, [isPublic, roomAccessPermission, allowedUserList, token]);

  useEffect(() => {
    if (socketRef.current) {
      socketRef.current.on(SOCKET_EVENTS.REFRESH_DELETE_PERMISSION, ({ userId }) => {
        dispatch(
          setState({
            allowedUserList: allowedUserList.filter((item) => item.id.userId !== userId),
          })
        );
      });
    }
  }, [socketRef.current]);

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
          // dispatch(setRoomName(response.data?.roomName));
          dispatch(
            setState({
              roomName: response.data?.roomName,
              roomMaster: response.data?.roomMaster,
              isPublic: response.data?.isPublic,
              roomAccessPermission: response.data?.accessPermission,
            })
          );
          setRoomMasterId(response.data?.roomMasterId);
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
    if (
      localAudioRef.current?.srcObject &&
      localAudioRef.current.srcObject?.getAudioTracks().length > 0
    ) {
      localAudioRef.current.srcObject.getAudioTracks()[0].enabled = !isMute;
    }
  }, [isMute]);
  return (
    <>
      {isAccess ? (
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
                <CodeEditor socket={socketRef} roomId={roomId} roomMasterId={roomMasterId} />
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
      ) : (
        <NotAccess />
      )}
    </>
  );
};

export default CodeEditorPage;
