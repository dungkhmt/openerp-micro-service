import { Avatar, Divider, ListItem, ListItemAvatar, MenuItem, Select } from "@mui/material";
import React, { useEffect, useState } from "react";
import { ACCESS_PERMISSION } from "../utils/constants";
import { codeEditorSelector, setState } from "../reducers/codeEditorReducers";
import { useDispatch, useSelector } from "react-redux";
import { request } from "api";
import { CHARACTER_COLOR, SOCKET_EVENTS } from "utils/constants";
import { errorNoti, successNoti } from "utils/notification";

const AllowedUserList = (props) => {
  const { socket } = props;
  const { roomMaster, allowedUserList } = useSelector(codeEditorSelector);
  const dispatch = useDispatch();

  /**
   * handle change permission to edit source code
   * @param {*} user
   * @param {*} permission
   * @param {*} userList
   */
  const handleChangePermission = (user, permission, userList) => {
    if (permission !== "DELETE") {
      const data = {
        roomId: user.id.roomId,
        userIds: [user.id.userId],
        accessPermission: permission,
      };
      request(
        "put",
        "/code-editor/rooms/share",
        (response) => {
          if (response && response.status === 200) {
            successNoti("Đã cập nhật quyền truy cập", true);
            socket.current.emit(SOCKET_EVENTS.UPDATE_USER_PERMISSION, {
              userId: user.id.userId,
              accessPermission: permission,
            });
          }
        },
        (error) => {
          errorNoti("Cập nhật quyền truy cập thất bại. Vui lòng thử lại", true);
        },
        data
      );
    } else {
      request(
        "delete",
        `/code-editor/rooms/${user.id.roomId}/access-permission/${user.id.userId}`,
        (response) => {
          if (response && response.status === 200) {
            dispatch(setState({ allowedUserList: userList.filter((item) => item !== user) }));
            successNoti("Đã cập nhật quyền truy cập", true);
            socket.current.emit(SOCKET_EVENTS.DELETE_USER_PERMISSION, { userId: user.id.userId });
          }
        },
        (error) => {
          errorNoti("Cập nhật quyền truy cập thất bại. Vui lòng thử lại", true);
        }
      );
    }
  };
  return (
    <div>
      <ListItem key={`${roomMaster.id}`} secondaryAction={<div>Chủ sở hữu</div>}>
        <ListItemAvatar>
          <Avatar sx={{ bgcolor: CHARACTER_COLOR[roomMaster?.lastName.toUpperCase()[0]] }}>{`${
            roomMaster?.lastName.toUpperCase()[0]
          }`}</Avatar>
        </ListItemAvatar>
        <div
          style={{
            overflow: "hidden",
            textOverflow: "ellipsis",
            whiteSpace: "nowrap",
          }}
        >
          <div style={{ fontWeight: "bold" }}>
            {roomMaster.firstName} {roomMaster.lastName}
          </div>
          <div>{roomMaster.email}</div>
        </div>
      </ListItem>
      {allowedUserList?.map((item) => {
        if (item.user.id !== roomMaster.id) {
          return (
            <ListItem
              key={`${item.user.id}`}
              secondaryAction={
                <Select
                  size="small"
                  defaultValue={item.accessPermission}
                  sx={{ boxShadow: "none", ".MuiOutlinedInput-notchedOutline": { border: "none" } }}
                  onChange={(e) => {
                    handleChangePermission(item, e.target.value, allowedUserList);
                  }}
                >
                  <MenuItem value={ACCESS_PERMISSION.VIEWER.value}>
                    {ACCESS_PERMISSION.VIEWER.label}
                  </MenuItem>
                  <MenuItem value={ACCESS_PERMISSION.EDITOR.value}>
                    {ACCESS_PERMISSION.EDITOR.label}
                  </MenuItem>
                  <Divider />
                  <MenuItem value="DELETE">Xóa quyền truy cập</MenuItem>
                </Select>
              }
            >
              <ListItemAvatar>
                <Avatar
                  sx={{ bgcolor: CHARACTER_COLOR[item.user?.lastName?.toUpperCase()[0]] }}
                >{`${item.user?.lastName?.toUpperCase()[0]}`}</Avatar>
              </ListItemAvatar>
              <div
                style={{
                  overflow: "hidden",
                  textOverflow: "ellipsis",
                  whiteSpace: "nowrap",
                }}
              >
                <div style={{ fontWeight: "bold" }}>
                  {item.user?.firstName} {item.user?.lastName}
                </div>
                <div>{item.user?.email}</div>
              </div>
            </ListItem>
          );
        }
        return null;
      })}
    </div>
  );
};

export default AllowedUserList;
