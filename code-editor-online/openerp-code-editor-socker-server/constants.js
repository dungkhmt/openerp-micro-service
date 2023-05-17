const SOCKET_IO_EVENTS = {
  CONNECTION: "connection",
  LEAVE_ROOM: "leave_room",
  CONNECT_TO_EDITOR: "connect-to-editor",
  JOINED: "joined",
  SEND_CODE_CHANGES: "send-code-changes",
  RECEIVE_CODE_CHANGES: "receive-code-changes",
  UPDATE_USER_PERMISSION: "update-user-permission",
  REFRESH_USER_PERMISSION: "refresh-user-permission",
  UPDATE_ROOM_PERMISSION: "update-room-permission",
  REFRESH_ROOM_PERMISSION: "refresh-room-permission",
  DELETE_USER_PERMISSION: "delete-user-permission",
  REFRESH_DELETE_PERMISSION: "refresh-delete-permission",
  REQUEST_ON_OFF_MIC: "request-on-off-mic",
  ACCEPT_ON_OFF_MIC: "accept-on-off-mic",
};

module.exports = {
  SOCKET_IO_EVENTS,
};
