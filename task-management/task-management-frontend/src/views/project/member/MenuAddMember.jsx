import { Icon } from "@iconify/react";
import {
  Box,
  CircularProgress,
  Collapse,
  FormControl,
  IconButton,
  Menu,
  MenuItem,
  MenuList,
  TextField,
  Tooltip,
  Typography,
} from "@mui/material";
import PropTypes from "prop-types";
import { useCallback, useEffect, useState } from "react";
import toast from "react-hot-toast";
import { useSelector, useDispatch } from "react-redux";
import { UserAvatar } from "../../../components/common/avatar/UserAvatar";
import { LoadingButton } from "../../../components/mui/button/LoadingButton";
import { useDebounce } from "../../../hooks/useDebounce";
import { UserService } from "../../../services/api/user.service";
import { addMember } from "../../../store/project";

function renderUserItem(user) {
  const { firstName, lastName, id } = user;
  const fullName =
    firstName || lastName ? `${user.firstName} ${user.lastName}` : " - ";

  return (
    <Box
      sx={{
        display: "flex",
        alignItems: "center",
        gap: 2,
        flex: 1,
      }}
    >
      <UserAvatar user={user} />
      <Box
        sx={{
          display: "flex",
          alignItems: "flex-start",
          flexDirection: "column",
        }}
      >
        <Tooltip title={fullName}>
          <Typography
            sx={{
              fontWeight: 600,
              fontSize: "0.9rem",
              color: (theme) => theme.palette.text.secondary,
              "&:hover": {
                color: (theme) => theme.palette.text.primary,
                cursor: "pointer",
              },
            }}
          >
            {fullName}
          </Typography>
        </Tooltip>
        <Typography noWrap variant="caption">
          {`@${id}`}
        </Typography>
      </Box>
    </Box>
  );
}

const MenuAddMember = ({ anchorEl, onClose }) => {
  const { members, project } = useSelector((state) => state.project);
  const dispatch = useDispatch();

  const [addLoading, setAddLoading] = useState(false);
  const [fetchLoading, setFetchLoading] = useState(false);
  const [users, setUsers] = useState([]);
  const [selectedUser, setSelectedUser] = useState(null);
  const [search, setSearch] = useState("");
  const searchDebounce = useDebounce(search, 500);

  const getUsers = useCallback(async () => {
    try {
      setFetchLoading(true);
      const encodedQuery = encodeURIComponent(searchDebounce).replace(
        /%20/g,
        "%1F"
      );
      const users = await UserService.getAll({
        q: searchDebounce
          ? `id:*${encodedQuery}* OR firstName:*${encodedQuery}* OR lastName:*${encodedQuery}*`
          : "",
      });
      setUsers(users);
    } catch (e) {
      console.log(e);
      toast.error("Lỗi khi lấy danh sách người dùng");
    } finally {
      setFetchLoading(false);
    }
  }, [searchDebounce]);

  const handleAddMember = async () => {
    if (!selectedUser) return;
    try {
      setAddLoading(true);
      await dispatch(
        addMember({
          projectId: project.id,
          userId: selectedUser.id,
        })
      );
      toast.success("Thêm thành viên thành công");
      handleClose();
    } catch (e) {
      console.log(e);
      toast.error("Lỗi khi thêm thành viên");
    } finally {
      setAddLoading(false);
    }
  };

  const handleClose = () => {
    onClose?.();
    setSelectedUser(null);
    setSearch("");
  };

  const addMemberOpen = Boolean(anchorEl);

  const usersNotInProject = users.filter(
    (user) => !members.some((member) => member.member.id === user.id)
  );

  useEffect(() => {
    if (searchDebounce) {
      getUsers();
    }
  }, [getUsers, searchDebounce]);

  return (
    <Menu
      keepMounted
      open={addMemberOpen}
      anchorEl={anchorEl}
      onClose={handleClose}
      anchorOrigin={{
        vertical: "bottom",
        horizontal: "right",
      }}
      transformOrigin={{
        vertical: "top",
        horizontal: "right",
      }}
      slotProps={{
        paper: {
          style: {
            minWidth: "25rem",
            marginTop: "8px",
            padding: "8px 16px",
          },
        },
      }}
    >
      <Box sx={{ display: "flex", flexDirection: "column", gap: 2 }}>
        <Box
          onClick={(e) => e.preventDefault()}
          sx={{
            display: "flex",
            justifyContent: "space-between",
            gap: 4,
            alignItems: "center",
          }}
        >
          {selectedUser ? (
            <Box
              sx={{
                display: "flex",
                border: (theme) => `1px solid ${theme.palette.divider}`,
                padding: (theme) => theme.spacing(1, 2),
                borderRadius: "4px",
                flex: 1,
              }}
            >
              {renderUserItem(selectedUser)}
              <IconButton onClick={() => setSelectedUser(null)}>
                <Icon icon="carbon:close-filled" fontSize={20} />
              </IconButton>
            </Box>
          ) : (
            <FormControl fullWidth>
              <TextField
                fullWidth
                id="select-assignee"
                value={search}
                InputProps={{
                  placeholder: "Tìm kiếm người dùng",
                }}
                onChange={(e) => setSearch(e.target.value)}
                sx={{
                  "& .MuiSelect-select": {
                    display: "flex",
                    alignItems: "center",
                  },
                }}
              />
            </FormControl>
          )}
          <LoadingButton
            loading={addLoading}
            variant="contained"
            disabled={!selectedUser}
            onClick={handleAddMember}
          >
            Thêm
          </LoadingButton>
        </Box>
        <Collapse in={searchDebounce}>
          {fetchLoading ? (
            <Box
              sx={{
                display: "flex",
                alignItems: "center",
                justifyContent: "center",
                minHeight: "10rem",
              }}
            >
              <CircularProgress variant="indeterminate" size={26} />
            </Box>
          ) : (
            <MenuList
              id="list-user-select"
              autoFocus={true}
              sx={{
                minHeight: "10rem",
                maxHeight: "15rem",
                overflowY: "auto",
                border: (theme) => `1px solid ${theme.palette.divider}`,
                padding: (theme) => theme.spacing(1, 2),
                borderRadius: "4px",
              }}
            >
              {fetchLoading ? (
                <Box
                  sx={{
                    display: "flex",
                    alignItems: "center",
                    justifyContent: "center",
                    height: "100%",
                  }}
                >
                  <CircularProgress variant="indeterminate" size={26} />
                </Box>
              ) : (
                <>
                  {usersNotInProject.map((user) => (
                    <MenuItem
                      key={user.id}
                      onClick={() => {
                        setSelectedUser(user);
                        setSearch("");
                      }}
                    >
                      {renderUserItem(user)}
                    </MenuItem>
                  ))}
                  {usersNotInProject.length <= 0 && (
                    <Typography>Không tìm thấy người dùng</Typography>
                  )}
                </>
              )}
            </MenuList>
          )}
        </Collapse>
      </Box>
    </Menu>
  );
};

MenuAddMember.propTypes = {
  anchorEl: PropTypes.object,
  onClose: PropTypes.func,
};

export { MenuAddMember };
