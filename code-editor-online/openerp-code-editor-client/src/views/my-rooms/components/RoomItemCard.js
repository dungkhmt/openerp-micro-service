import { ContentCopy, Delete, Edit, Settings, Source } from "@mui/icons-material";
import {
  Button,
  Card,
  CardContent,
  Grid,
  IconButton,
  ListItemIcon,
  ListItemText,
  Menu,
  MenuItem,
  Tooltip,
  Typography,
} from "@mui/material";
import { request } from "api";
import copy from "copy-to-clipboard";
import React from "react";
import { useDispatch } from "react-redux";
import { useHistory } from "react-router-dom";
import { successNoti } from "utils/notification";
import { setIsVisibleRoomForm, setReloadData, setSelectedRoom } from "../reducers/myRoomsReducers";
const RoomItemCard = (props) => {
  const { item } = props;
  const dispatch = useDispatch();
  const history = useHistory();
  const [anchorEl, setAnchorEl] = React.useState(null);
  const open = Boolean(anchorEl);
  const handleClick = (event) => {
    setAnchorEl(event.currentTarget);
  };
  const handleClose = () => {
    setAnchorEl(null);
  };
  const handleJoinRoom = () => {
    history.push(`/code-editor/room/${item?.id}`);
  };

  const handleDeleteRoom = () => {
    request(
      "delete",
      `/code-editor/rooms/${item?.id}`,
      (response) => {
        if (response.status === 200) {
          successNoti(`Xóa phòng ${item?.roomName} thành công`, true);
          dispatch(setReloadData(Math.random()));
          handleClose();
        }
      },
      (error) => {
        console.log(error);
      }
    );
  };

  const handleEditRoom = () => {
    dispatch(setIsVisibleRoomForm(true));
    dispatch(setSelectedRoom(item));
    handleClose();
  };
  const handleCopyRoomId = () => {
    copy(item.id);
    successNoti("Copied room id to clipboard", true);
  };
  return (
    <Card sx={{ marginTop: "20px" }}>
      <CardContent>
        <Grid container spacing={4}>
          <Grid item xs={1} sx={{ alignItems: "center", display: "flex" }}>
            <Source fontSize="large" />
          </Grid>
          <Grid item xs={11} md={8}>
            <Typography variant="h5" gutterBottom>
              {item?.roomName}
            </Typography>
            <div>
              Id phòng: <strong>{item?.id}</strong>{" "}
              <Tooltip title="Copy to clipboard">
                <IconButton>
                  <ContentCopy
                    fontSize="small"
                    color="primary"
                    sx={{ cursor: "pointer" }}
                    onClick={() => {
                      handleCopyRoomId();
                    }}
                  />
                </IconButton>
              </Tooltip>
            </div>
          </Grid>
          <Grid
            item
            sx={{
              alignItems: "center",
              display: "flex",
              justifyContent: "center",
            }}
            xs={6} md={2}
          >
            <Button
              variant="contained"
              onClick={() => {
                handleJoinRoom();
              }}
            >
              Tham gia phòng
            </Button>
          </Grid>
          <Grid item sx={{ alignItems: "center", display: "flex", justifyContent:'center' }} xs={6} md={1}>
            <IconButton onClick={handleClick}>
              <Settings
                fontSize="large"
                aria-controls={open ? "basic-menu" : undefined}
                aria-haspopup="true"
                aria-expanded={open ? "true" : undefined}
              />
            </IconButton>

            <Menu
              id="basic-menu"
              anchorEl={anchorEl}
              open={open}
              onClose={handleClose}
              MenuListProps={{
                "aria-labelledby": "basic-button",
              }}
            >
              <MenuItem onClick={handleEditRoom}>
                <ListItemIcon>
                  <Edit color="primary" />
                </ListItemIcon>
                <ListItemText>Chỉnh sửa phòng</ListItemText>
              </MenuItem>
              <MenuItem onClick={handleDeleteRoom}>
                <ListItemIcon>
                  <Delete color="error" />
                </ListItemIcon>
                <ListItemText>Xóa phòng</ListItemText>
              </MenuItem>
            </Menu>
          </Grid>
        </Grid>
      </CardContent>
    </Card>
  );
};

export default RoomItemCard;
