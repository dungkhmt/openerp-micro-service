const dotenv = require("dotenv");
dotenv.config();
const express = require("express");
const cors = require("cors");

const app = express();
const http = require("http");
const { SOCKET_IO_EVENTS } = require("./constants");
const server = http.createServer(app);
app.use(cors());

const io = require("socket.io")(server, { cors: { origin: "*" } });

userSocketMap = {};

function getAllClients(roomId) {
  return Array.from(io.sockets.adapter.rooms.get(roomId) || []).map((socketId) => {
    return {
      socketId,
      fullName: userSocketMap[socketId].fullName,
    };
  });
}
io.on("connection", (socket) => {
  console.log(socket.id);
  socket.on(SOCKET_IO_EVENTS.CONNECT_TO_EDITOR, ({ fullName, roomId }) => {
    console.log("🚀 ~ file: index.js:44 ~ socket.on ~ fullName:", fullName);

    socket.join(roomId);
    userSocketMap[socket.id] = { fullName, roomId };
    const clients = getAllClients(roomId);

    clients.forEach((client) => {
      let socketId = client.socketId;

      io.to(socketId).emit(SOCKET_IO_EVENTS.JOINED, {
        fullName: fullName,
        socketId: socket.id,
        clients,
      });
    });

    socket.on(SOCKET_IO_EVENTS.SEND_CODE_CHANGES, (source) => {
      socket.broadcast.to(roomId).emit(SOCKET_IO_EVENTS.RECEIVE_CODE_CHANGES, source);
    });
  });

  socket.on("disconnecting", () => {
    const rooms = [...socket.rooms];
    rooms.forEach((roomId) => {
      socket.broadcast.to(roomId).emit(SOCKET_IO_EVENTS.LEAVE_ROOM, {
        fullName: userSocketMap[socket.id]?.fullName,
        socketId: socket.id,
      });
      console.log(
        "🚀 ~ file: index.js:55 ~ socket.in ~ userSocketMap[socket.id]?.fullName:",
        userSocketMap[socket.id]?.fullName
      );
    });
    delete userSocketMap[socket.id];
    socket.leave();
  });
});

const port = process.env.PORT || 7008;
server.listen(port, () => console.log(`Server is running on port ${port}`));
