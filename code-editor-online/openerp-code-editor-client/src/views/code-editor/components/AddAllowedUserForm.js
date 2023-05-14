import {
  Autocomplete,
  Avatar,
  Button,
  Chip,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  Grid,
  IconButton,
  ListItem,
  ListItemAvatar,
  MenuItem,
  Select,
  TextField,
} from "@mui/material";
import React, { useEffect, useState } from "react";
import { useDispatch, useSelector } from "react-redux";
import { codeEditorSelector, setState } from "../reducers/codeEditorReducers";
import { ArrowBack } from "@mui/icons-material";
import { debounce } from "lodash";
import { request } from "api";
import { CHARACTER_COLOR } from "utils/constants";
import { ACCESS_PERMISSION } from "../utils/constants";
import { errorNoti, successNoti } from "utils/notification";

const AddAllowedUserForm = (props) => {
  const { open, setOpen, roomId } = props;
  const dispatch = useDispatch();
  const { roomName } = useSelector(codeEditorSelector);

  const [userList, setUserList] = useState([]);
  const [keyword, setKeyword] = useState();
  const [loading, setLoading] = useState(false);
  const [selectedUsers, setSelectedUsers] = useState([]);
  const [permission, setPermission] = useState(ACCESS_PERMISSION.VIEWER.value);

  useEffect(() => {
    if (keyword) {
      setLoading(true);
      request(
        "get",
        `/code-editor/users/search?keyword=${keyword}&page=0&size=10`,
        (response) => {
          console.log(response);
          if (response && response.status === 200) {
            setUserList(response.data.content);
            setLoading(false);
          }
        },
        (error) => {
          setLoading(false);
          console.log(error);
        }
      );
    } else {
      setUserList([]);
    }
  }, [keyword]);

  const handleChangeKeyword = debounce((value) => {
    setKeyword(value);
  }, 500);

  const handleClose = () => {
    setOpen(false);
    dispatch(setState({ isVisibleShareForm: true }));
  };

  const handleShare = (roomId, users, permission) => {
    const data = {
      roomId: roomId,
      userIds: users.map((user) => user.id),
      accessPermission: permission,
    };
    request(
      "put",
      `/code-editor/rooms/share`,
      (response) => {
        if (response && response.status === 200) {
          successNoti("Thêm người truy cập thành công", true);
          handleClose();
          dispatch(
            setState({
              reloadAllowedUser: Math.random(),
            })
          );
        }
      },
      (error) => {
        errorNoti("Thêm người truy cập thất bại. Vui lòng thử lại", false);
      },
      data
    );
  };
  return (
    <Dialog open={open} onClose={handleClose} maxWidth="sm" fullWidth>
      <DialogTitle>
        <Grid container justifyContent="space-between">
          <Grid item xs={9} container>
            <IconButton onClick={handleClose}>
              <ArrowBack />
            </IconButton>
            Chia sẻ "{roomName}"
          </Grid>
        </Grid>
      </DialogTitle>
      <DialogContent>
        <Grid container>
          <Grid item xs={9}>
            <Autocomplete
              multiple
              options={userList}
              loading={loading}
              filterOptions={(x) => x}
              renderOption={(props, option) => {
                return (
                  <div {...props}>
                    <ListItem key={`${option.id}`}>
                      <ListItemAvatar>
                        <Avatar
                          sx={{ bgcolor: CHARACTER_COLOR[option?.lastName?.toUpperCase()[0]] }}
                        >{`${option?.lastName?.toUpperCase()[0]}`}</Avatar>
                      </ListItemAvatar>
                      <div
                        style={{
                          overflow: "hidden",
                          textOverflow: "ellipsis",
                          whiteSpace: "nowrap",
                        }}
                      >
                        <div style={{ fontWeight: "bold" }}>
                          {option?.firstName} {option?.lastName}
                        </div>
                        <div>{option?.id}</div>
                      </div>
                    </ListItem>
                  </div>
                );
              }}
              getOptionLabel={(option) => {
                if (!option?.firstName && !option?.lastName) {
                  return option?.id;
                }
                return `${option.firstName} ${option.lastName}`;
              }}
              isOptionEqualToValue={(option, value) => {
                return option.id === value.id;
              }}
              renderInput={(params) => (
                <TextField
                  {...params}
                  variant="outlined"
                  onChange={(e) => {
                    handleChangeKeyword(e.target.value);
                  }}
                  placeholder="Thêm người"
                />
              )}
              onChange={(event, newValue) => {
                setSelectedUsers(newValue);
                setUserList([]);
              }}
            />
          </Grid>
          <Grid item xs={3}>
            <Select
              size="small"
              defaultValue={ACCESS_PERMISSION.VIEWER.value}
              sx={{ boxShadow: "none", ".MuiOutlinedInput-notchedOutline": { border: "none" } }}
              value={permission}
              onChange={(e) => {
                setPermission(e.target.value);
              }}
            >
              <MenuItem value={ACCESS_PERMISSION.VIEWER.value}>
                {ACCESS_PERMISSION.VIEWER.label}
              </MenuItem>
              <MenuItem value={ACCESS_PERMISSION.EDITOR.value}>
                {ACCESS_PERMISSION.EDITOR.label}
              </MenuItem>
            </Select>
          </Grid>
        </Grid>
      </DialogContent>
      <DialogActions>
        <Button variant="outlined" color="primary" onClick={handleClose}>
          Hủy
        </Button>
        <Button
          variant="contained"
          color="primary"
          onClick={() => {
            handleShare(roomId, selectedUsers, permission);
          }}
        >
          Lưu
        </Button>
      </DialogActions>
    </Dialog>
  );
};

export default AddAllowedUserForm;
