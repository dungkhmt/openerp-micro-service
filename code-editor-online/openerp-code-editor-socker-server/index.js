const dotenv = require("dotenv");
dotenv.config();
const express = require("express");
const cors = require("cors");

const app = express();
const http = require("http");
const { SOCKET_IO_EVENTS } = require("./constants");
const server = http.createServer(app);
app.use(cors());
const bodyParser = require("body-parser");
const { default: axios } = require("axios");
app.use(bodyParser.urlencoded({ extended: false }));
app.use(bodyParser.json());

const io = require("socket.io")(server, { cors: { origin: "*" } });

userSocketMap = {};

function getAllClients(roomId) {
  return Array.from(io.sockets.adapter.rooms.get(roomId) || []).map((socketId) => {
    return {
      socketId,
      fullName: userSocketMap[socketId]?.fullName,
      audio: userSocketMap[socketId]?.audio,
      peerId: userSocketMap[socketId]?.peerId,
      video: userSocketMap[socketId]?.video,
    };
  });
}
io.on("connection", (socket) => {
  console.log(`Connected ${socket.id}`);
  socket.on(SOCKET_IO_EVENTS.CONNECT_TO_EDITOR, ({ fullName, roomId, peerId }) => {
    socket.join(roomId);
    userSocketMap[socket.id] = { fullName, roomId, peerId, audio: true, video: true };
    const clients = getAllClients(roomId);

    clients.forEach((client) => {
      let socketId = client.socketId;
      io.to(socketId).emit(SOCKET_IO_EVENTS.JOINED, {
        fullName: fullName,
        socketId: socket.id,
        peerId,
        clients,
      });
    });

    socket.on(SOCKET_IO_EVENTS.SEND_CODE_CHANGES, ({ language, source, cursor }) => {
      socket.broadcast.to(roomId).emit(SOCKET_IO_EVENTS.RECEIVE_CODE_CHANGES, {
        language: language,
        source: source,
        cursor: cursor,
      });
    });

    socket.on(SOCKET_IO_EVENTS.UPDATE_USER_PERMISSION, ({ userId, accessPermission }) => {
      socket.broadcast.to(roomId).emit(SOCKET_IO_EVENTS.REFRESH_USER_PERMISSION, {
        userId,
        accessPermission,
      });
    });

    socket.on(SOCKET_IO_EVENTS.UPDATE_ROOM_PERMISSION, ({ roomId, isPublic, accessPermission }) => {
      socket.broadcast.to(roomId).emit(SOCKET_IO_EVENTS.REFRESH_ROOM_PERMISSION, {
        isPublic,
        accessPermission,
      });
    });

    socket.on(SOCKET_IO_EVENTS.DELETE_USER_PERMISSION, ({ userId }) => {
      socket.broadcast.to(roomId).emit(SOCKET_IO_EVENTS.REFRESH_DELETE_PERMISSION, { userId });
    });

    socket.on(SOCKET_IO_EVENTS.REQUEST_ON_OFF_MIC, ({ socketId, audio }) => {
      io.to(roomId).emit(SOCKET_IO_EVENTS.ACCEPT_ON_OFF_MIC, {
        socketId,
        audio,
      });
    });
    socket.on(SOCKET_IO_EVENTS.REQUEST_ON_OFF_CAMERA, ({ socketId, video }) => {
      io.to(roomId).emit(SOCKET_IO_EVENTS.ACCEPT_ON_OFF_CAMERA, {
        socketId,
        video,
      });
    });
    socket.on(SOCKET_IO_EVENTS.REQUEST_REMOVE_PARTICIPANT, ({ socketId }) => {
      io.to(roomId).emit(SOCKET_IO_EVENTS.ACCEPT_REMOVE_PARTICIPANT, {
        socketId,
      });
    });
  });

  socket.on("disconnecting", () => {
    const rooms = [...socket.rooms];
    const fullName = userSocketMap[socket.id]?.fullName;

    rooms.forEach((roomId) => {
      socket.in(roomId).emit(SOCKET_IO_EVENTS.LEAVE_ROOM, {
        fullName: fullName,
        socketId: socket.id,
        clients: getAllClients(roomId),
      });
    });
    delete userSocketMap[socket.id];
    socket.leave();
  });
});

const port = process.env.PORT || 7008;
server.listen(port, () => console.log(`Server is running on port ${port}`));

const { ExpressPeerServer } = require("peer");
const peerServer = ExpressPeerServer(server, {
  port: port,
});
app.use("/api/socket-server/peer-server", peerServer);
