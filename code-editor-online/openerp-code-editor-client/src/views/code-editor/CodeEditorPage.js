/* eslint-disable no-unused-vars */
/* eslint-disable react-hooks/exhaustive-deps */
import { Grid, Typography } from "@mui/material";
import { request } from "api";
import { useEffect, useRef, useState } from "react";
import { useDispatch, useSelector } from "react-redux";
import { useParams } from "react-router-dom";
import { SOCKET_EVENTS } from "utils/constants";
import NavBarRoom from "./components/NavBarRoom";

import { useKeycloak } from "@react-keycloak/web";
import Peer from "peerjs";
import SplitterLayout from "react-splitter-layout";
import "react-splitter-layout/lib/index.css";
import { io } from "socket.io-client";
import { errorNoti, successNoti } from "utils/notification";
import CodeEditor from "./components/CodeEditor";
import InputOutputCard from "./components/InputOuputCard";
import NotAccess from "./components/NotAccess";
import Participants from "./components/Participants";
import {
  handleOnOffMicParticipant,
  setParticipants,
  setState,
} from "./reducers/codeEditorReducers";
import "./style.css";

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
        `${process.env.REACT_APP_SOCKET_SERVER_HOST}:${process.env.REACT_APP_SOCKET_SERVER_PORT}` ||
          "http://localhost:7008",
        { debug: true }
      );
      // get audio instance of system
      navigator.mediaDevices
        .getUserMedia({ video: false, audio: true })
        .then((stream) => {
          // Create a peer connection
          myPeer.current = new Peer({
            host: process.env.REACT_APP_SOCKET_SERVER_HOST || "127.0.0.1",
            port: process.env.REACT_APP_SOCKET_SERVER_PORT || 7008,
            path: "/api/code-editor/peer-server",
          });

          // Get id of peer connection and send to server and broadcast to others user
          myPeer.current.on("open", (id) => {
            if (socketRef.current) {
              // Send peerId to other users in ther room
              socketRef.current.emit(SOCKET_EVENTS.CONNECT_TO_EDITOR, {
                roomId: roomId,
                fullName: token.name,
                peerId: id,
              });
            }
          });
          if (socketRef.current) {
            // if a new user joined then update participants
            socketRef.current.on(
              SOCKET_EVENTS.JOINED,
              ({ fullName, socketId, peerId, clients }) => {
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
              }
            );
            // if a user leave the room, notify other users and update participant list
            socketRef.current.on(
              SOCKET_EVENTS.LEAVE_ROOM,
              ({ fullName, socketId, clients }) => {
                errorNoti(`${fullName} đã rời phòng`, true);
                dispatch(
                  setParticipants(
                    clients.filter((client) => client.socketId !== socketId)
                  )
                );
              }
            );
          }
        });

      return () => {
        socketRef.current.disconnect();
        socketRef.current.off(SOCKET_EVENTS.JOINED);
        socketRef.current.off(SOCKET_EVENTS.LEAVE_ROOM);
      };
    }
  }, [isAccess]);

  // get the list of users who have access to the current room
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

  // check if the current user has access to the current room
  useEffect(() => {
    const user = allowedUserList.find(
      (item) => item.id.userId === token.preferred_username
    );
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
      // if a user is removed, update the room's list of permissions
      socketRef.current.on(
        SOCKET_EVENTS.REFRESH_DELETE_PERMISSION,
        ({ userId }) => {
          dispatch(
            setState({
              allowedUserList: allowedUserList.filter(
                (item) => item.id.userId !== userId
              ),
            })
          );
        }
      );

      // if a user is on or off mic, update mic status of user in the room
      socketRef.current.on(
        SOCKET_EVENTS.ACCEPT_ON_OFF_MIC,
        ({ socketId, audio }) => {
          dispatch(handleOnOffMicParticipant({ socketId, audio }));
          if (socketId === socketRef.current.id) {
            dispatch(
              setState({
                isMute: !audio,
              })
            );
          }
        }
      );
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

  // On or off local mic
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
      {isAccess === true && (
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
              sx={{ position: "relative", minHeight: "77vh" }}
            >
              <SplitterLayout vertical>
                <CodeEditor
                  socket={socketRef}
                  roomId={roomId}
                  roomMasterId={roomMasterId}
                />
                <Grid container spacing={2} height="100%">
                  <Grid item xs={12}>
                    <InputOutputCard />
                  </Grid>
                </Grid>
              </SplitterLayout>
            </Grid>
            <Grid
              hidden={!isVisibleParticipants}
              item
              xs={isVisibleParticipants ? 2 : 0}
            >
              <Participants socket={socketRef} />
            </Grid>
          </Grid>
        </div>
      )}
      {isAccess === false && <NotAccess />}
    </>
  );
};

export default CodeEditorPage;
