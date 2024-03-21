import {
  Box,
  Button,
  CircularProgress,
  FormControl,
  InputLabel,
  Menu,
  MenuItem,
  Select,
  Tooltip,
  Typography,
} from "@mui/material";
import { useState } from "react";
import toast from "react-hot-toast";
import CustomAvatar from "../../components/mui/avatar/CustomAvatar";
import { useProjectContext } from "../../hooks/useProjectContext";
import { UserService } from "../../services/api/user.service";
import { getRandomColorSkin } from "../../utils/color.util";
import { ProjectService } from "../../services/api/project.service";

const MenuAddMember = () => {
  const { members, project, setIsUpdate } = useProjectContext();
  const [anchorEl, setAnchorEl] = useState(null);
  const [users, setUsers] = useState([]);
  const [selectedUser, setSelectedUser] = useState("");
  const [loading, setLoading] = useState(false);

  const getUsers = async () => {
    if (users.length > 0) return;
    try {
      const users = await UserService.getAll();
      setUsers(users);
    } catch (e) {
      console.log(e);
      toast.error("Lỗi khi lấy danh sách người dùng");
    }
  };

  const handleAddMenuClick = (event) => {
    setAnchorEl(event.currentTarget);
    getUsers();
  };

  const handleAddMember = async () => {
    try {
      setLoading(true);
      await ProjectService.addMember({
        projectId: project.id,
        userId: selectedUser,
      });
      toast.success("Thêm thành viên thành công");
      setIsUpdate((prev) => !prev);
      handleClose();
    } catch (e) {
      console.log(e);
      toast.error("Lỗi khi thêm thành viên");
    } finally {
      setLoading(false);
    }
  };

  const handleClose = () => {
    setAnchorEl(null);
    setSelectedUser("");
  };

  const addMemberOpen = Boolean(anchorEl);

  const usersNotInProject = users.filter(
    (user) => !members.some((member) => member.member.id === user.id)
  );

  return (
    <>
      <Button variant="outlined" onClick={handleAddMenuClick}>
        Thêm thành viên
      </Button>
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
        <Box
          onClick={(e) => e.preventDefault()}
          sx={{
            display: "flex",
            justifyContent: "space-between",
            gap: 4,
            alignItems: "center",
          }}
        >
          <FormControl fullWidth>
            <InputLabel id="user-select">User</InputLabel>
            <Select
              fullWidth
              id="select-assignee"
              label="User"
              labelId="assignee-select"
              inputProps={{ placeholder: "User" }}
              value={selectedUser}
              onChange={(e) => setSelectedUser(e.target.value)}
              sx={{
                "& .MuiSelect-select": {
                  display: "flex",
                  alignItems: "center",
                },
              }}
            >
              {usersNotInProject?.map((user) => {
                const { firstName, lastName, id } = user;
                const fullName =
                  firstName && lastName
                    ? `${firstName} ${lastName}`
                    : "Không xác định";
                return (
                  <MenuItem
                    key={id}
                    sx={{ display: "flex", alignItems: "center" }}
                    value={id}
                    onKeyDown={(e) => e.preventDefault()}
                  >
                    <CustomAvatar
                      skin="light"
                      color={getRandomColorSkin(id)}
                      sx={{ mr: 3, width: 30, height: 30, fontSize: ".875rem" }}
                    >
                      {`${firstName?.charAt(0) ?? ""}${
                        lastName?.charAt(0) ?? ""
                      }`}
                    </CustomAvatar>
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
                  </MenuItem>
                );
              })}
            </Select>
          </FormControl>
          <Button
            variant="contained"
            disabled={!selectedUser || loading}
            onClick={handleAddMember}
          >
            Thêm
            {loading && (
              <Box
                sx={{
                  position: "absolute",
                  display: "flex",
                  alignItems: "center",
                  justifyContent: "center",
                  width: "100%",
                  height: "100%",
                  top: 0,
                  left: 0,
                }}
              >
                <CircularProgress size={24} />
              </Box>
            )}
          </Button>
        </Box>
      </Menu>
    </>
  );
};

export { MenuAddMember };
