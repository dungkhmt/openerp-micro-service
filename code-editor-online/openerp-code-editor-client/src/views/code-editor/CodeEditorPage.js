/* eslint-disable no-unused-vars */
/* eslint-disable react-hooks/exhaustive-deps */
import { Card, Grid, Typography } from "@mui/material";
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
import { errorNoti, infoNoti, successNoti } from "utils/notification";
import { useHistory } from "react-router";
import CodeEditor from "./components/CodeEditor";
import InputOutputCard from "./components/InputOuputCard";
import NotAccess from "./components/NotAccess";
import Participants from "./components/Participants";
import {
  handleAddRemoteUser,
  handleOnOffMicParticipant,
  handleOnOffMicRemoteUser,
  handleRemoveRemoteUser,
  handleTurnOffCamera,
  setParticipants,
  setState,
} from "./reducers/codeEditorReducers";
import "./style.css";

const CodeEditorPage = () => {
  const { id: roomId } = useParams();
  const dispatch = useDispatch();
  const {
    isVisibleParticipants,
    roomName,
    reloadAllowedUser,
    isPublic,
    roomAccessPermission,
    allowedUserList,
    // localVideoTest,
    isVisibleInput,
  } = useSelector((state) => state.codeEditor);
  const [roomMasterId, setRoomMasterId] = useState();
  const { keycloak } = useKeycloak();
  const token = keycloak.tokenParsed;
  const socketRef = useRef();
  const myPeer = useRef();
  const [isAccess, setIsAccess] = useState();
  // const [localVideo, setLocalVideo] = useState(null);
  const [remoteVideos, setRemoteVideos] = useState([]);
  const history = useHistory();

  useEffect(() => {
    if (isAccess) {
      socketRef.current = io(
        `${process.env.REACT_APP_SOCKET_SERVER_HOST}:${process.env.REACT_APP_SOCKET_SERVER_PORT}` ||
          "http://localhost:7008",
        { debug: true }
      );
      // get audio instance of system

      navigator.mediaDevices.getUserMedia({ video: true, audio: true }).then((stream) => {
        // setLocalVideo(stream);
        dispatch(setState({ localVideo: stream }));

        // Create a peer connection
        myPeer.current = new Peer({
          host: process.env.REACT_APP_SOCKET_SERVER_HOST || "127.0.0.1",
          port: process.env.REACT_APP_SOCKET_SERVER_PORT || 7008,
          path: "/api/socket-server/peer-server",
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
          socketRef.current.on(SOCKET_EVENTS.JOINED, ({ fullName, socketId, peerId, clients }) => {
            if (socketRef.current.id !== socketId) {
              successNoti(`${fullName} đã tham gia vào phòng`, true);
            }
            dispatch(setParticipants(clients));

            myPeer.current.on("call", (call) => {
              call.answer(stream);
              call.on("stream", (userVideoStream) => {
                dispatch(handleAddRemoteUser({ peerId: call.peer, media: userVideoStream }));
                setRemoteVideos((prevStreams) => [...prevStreams, userVideoStream]);
              });
            });

            if (peerId !== myPeer.current.id) {
              const call = myPeer.current.call(peerId, stream);
              call.on("stream", (userVideoStream) => {
                dispatch(
                  handleAddRemoteUser({
                    peerId: peerId,
                    media: userVideoStream,
                    fullName: fullName,
                    socketId: socketId,
                  })
                );
                setRemoteVideos((prevStreams) => [...prevStreams, userVideoStream]);
              });
            }
          });
          // if a user leave the room, notify other users and update participant list
          socketRef.current.on(SOCKET_EVENTS.LEAVE_ROOM, ({ fullName, socketId, clients }) => {
            console.log("🚀 ~ file: CodeEditorPage.js:121 ~ socketRef.current.on ~ socketId:", socketId)
            // errorNoti(`${fullName} đã rời phòng`, true);
            infoNoti(`${fullName} đã rời phòng`, true);
            dispatch(setParticipants(clients.filter((client) => client.socketId !== socketId)));
            dispatch(handleRemoveRemoteUser({ socketId: socketId }));
          });
        }
      });

      return () => {
        if (socketRef.current) {
          socketRef.current.disconnect();
          socketRef.current.off(SOCKET_EVENTS.JOINED);
          socketRef.current.off(SOCKET_EVENTS.LEAVE_ROOM);
        }

        dispatch(handleTurnOffCamera());
        dispatch(setState({ remoteUsers: [] }));
        if (myPeer.current) {
          myPeer.current.destroy();
        }
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
    const user = allowedUserList.find((item) => item.id.userId === token.preferred_username);
    if (token.preferred_username === roomMasterId) {
      setIsAccess(true);
    } else if (!isPublic && !user) {
      setIsAccess(false);
    } else {
      setIsAccess(true);
    }
  }, [isPublic, roomAccessPermission, allowedUserList, token, roomMasterId]);

  useEffect(() => {
    if (socketRef.current) {
      // if a user is removed, update the room's list of permissions
      socketRef.current.on(SOCKET_EVENTS.REFRESH_DELETE_PERMISSION, ({ userId }) => {
        dispatch(
          setState({
            allowedUserList: allowedUserList.filter((item) => item.id.userId !== userId),
          })
        );
      });

      // if a user is on or off mic, update mic status of user in the room
      socketRef.current.on(SOCKET_EVENTS.ACCEPT_ON_OFF_MIC, ({ socketId, audio }) => {
        dispatch(handleOnOffMicParticipant({ socketId, audio }));
        dispatch(handleOnOffMicRemoteUser({ socketId, audio }));
        if (socketId === socketRef.current.id) {
          dispatch(
            setState({
              isMute: !audio,
            })
          );
        }
      });
      socketRef.current.on(SOCKET_EVENTS.ACCEPT_REMOVE_PARTICIPANT, ({ socketId }) => {
        if (socketId === socketRef.current.id) {
          socketRef.current.disconnect();
          if (myPeer.current) {
            myPeer.current.disconnect();
            myPeer.current.destroy();
          }
          infoNoti("Bạn đã bị loại ra khỏi phòng", true)
          history.push("/code-editor/create-join-room");
        }
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

  return (
    <>
      {isAccess === true && (
        <div>
          <Typography variant="body">{roomName}</Typography>
          <NavBarRoom socket={socketRef} myPeer={myPeer} />
          <Grid container spacing={1} sx={{ marginTop: "1px" }}>
            <Grid
              item
              xs={isVisibleParticipants ? 10 : 12}
              sx={{ position: "relative", minHeight: "100vh" }}
            >
              <SplitterLayout percentage vertical>
                <CodeEditor socket={socketRef} roomId={roomId} roomMasterId={roomMasterId} />
                {isVisibleInput && (
                  <Grid container spacing={2} height="100%">
                    <Grid item xs={12}>
                      <InputOutputCard />
                    </Grid>
                  </Grid>
                )}
              </SplitterLayout>
            </Grid>
            <Grid hidden={!isVisibleParticipants} item xs={isVisibleParticipants ? 2 : 0}>
              <Participants socket={socketRef} remoteVideos={remoteVideos} />
            </Grid>
          </Grid>
        </div>
      )}
      {isAccess === false && <NotAccess />}
    </>
  );
};

export default CodeEditorPage;
