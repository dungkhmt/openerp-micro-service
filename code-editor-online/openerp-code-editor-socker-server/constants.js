const SOCKET_IO_EVENTS = {
  CONNECTION: "connection",
  LEAVE_ROOM: "leave_room", // user leave room
  CONNECT_TO_EDITOR: "connect-to-editor", //
  JOINED: "joined", // user joined the room
  SEND_CODE_CHANGES: "send-code-changes", // user send code change to server
  RECEIVE_CODE_CHANGES: "receive-code-changes", // broadcast changed code to other users in the room
  UPDATE_USER_PERMISSION: "update-user-permission", // update permission of specified user
  REFRESH_USER_PERMISSION: "refresh-user-permission", // broadcast new permission to user
  UPDATE_ROOM_PERMISSION: "update-room-permission", // update permission of room
  REFRESH_ROOM_PERMISSION: "refresh-room-permission", // broadcast new permission to all users in the room
  DELETE_USER_PERMISSION: "delete-user-permission", //  delete permission of specified user
  REFRESH_DELETE_PERMISSION: "refresh-delete-permission", // broadcast user is deleted to room
  REQUEST_ON_OFF_MIC: "request-on-off-mic", // room master on or off mic of other users in the room
  ACCEPT_ON_OFF_MIC: "accept-on-off-mic", // broadcast event to all users and update status of mic for user
  REQUEST_REMOVE_PARTICIPANT: "request-remove-participant",
  ACCEPT_REMOVE_PARTICIPANT: "accept-remove-participant",
};

module.exports = {
  SOCKET_IO_EVENTS,
};
