import { Icon } from "@iconify/react";
import {
  Box,
  capitalize,
  Card,
  Divider,
  IconButton,
  Link,
  Menu,
  MenuItem,
  Select,
  TextField,
  Tooltip,
  Typography,
} from "@mui/material";
import { DataGrid } from "@mui/x-data-grid";
import PropTypes from "prop-types";
import { useEffect, useState } from "react";
import toast from "react-hot-toast";
import { useSelector, useDispatch } from "react-redux";
import { UserAvatar } from "../../../components/common/avatar/UserAvatar";
import { usePreventOverflow } from "../../../hooks/usePreventOverflow";
import { MenuAddMember } from "./MenuAddMember";
import { DialogMemberTasks } from "./DialogMemberTasks";
import { fetchTasksForMember } from "../../../store/project/tasks";
import { deleteMember, updateMemberRole } from "../../../store/project";
import { useNavigate, useParams } from "react-router";
import ConfirmationDialog from "../../../components/mui/dialog/ConfirmationDialog";
import { CircularProgressLoading } from "../../../components/common/loading/CircularProgressLoading";

const columns = (handleMemberClick, projectId) => {
  const baseColumns = [
    {
      flex: 0.3,
      minWidth: 150,
      field: "id",
      headerName: "User",
      renderCell: ({ row }) => {
        const { firstName, lastName, id } = row.member;
        const fullName =
          firstName || lastName
            ? `${firstName ?? ""} ${lastName ?? ""}`
            : " - ";
        return (
          <Box sx={{ display: "flex", alignItems: "center", gap: 2 }}>
            <UserAvatar user={row.member} />
            <Box
              sx={{
                display: "flex",
                alignItems: "flex-start",
                flexDirection: "column",
              }}
            >
              <Tooltip title={`Xem nhiệm vụ được giao cho ${fullName}`}>
                <Typography
                  onClick={() => handleMemberClick(projectId, row.member)}
                  sx={{
                    fontWeight: 650,
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
      },
      display: "flex",
    },
    {
      flex: 0.3,
      minWidth: 150,
      field: "email",
      headerName: "Email",
      renderCell: ({ row }) => {
        return (
          <Tooltip title={`Gửi mail tới: ${row.member.email}`}>
            <Typography
              noWrap
              variant="body2"
              component={Link}
              href={`mailto:${row.member.email}`}
              sx={{ textDecoration: "none", color: "text.secondary" }}
            >
              {row.member?.email}
            </Typography>
          </Tooltip>
        );
      },
      display: "flex",
    },
    {
      flex: 0.15,
      minWidth: 100,
      field: "role",
      headerName: "Role",
      renderCell: ({ row }) => <RoleSelector member={row} />,
    },
    {
      flex: 0.1,
      minWidth: 60,
      sortable: false,
      field: "actions",
      headerName: "Actions",
      renderCell: ({ row }) => <RowOptions member={row} />,
    },
  ];

  return baseColumns;
};

const getContent = (user, member, project) => {
  const fullName = `${member?.member.firstName ?? ""} ${
    member?.member.lastName ?? ""
  }`.trim();
  if (member.id === user.id) {
    return (
      <>
        Bạn có chắc chắn muốn rời khỏi dự án{" "}
        <span style={{ fontWeight: "bold" }}>{project.name}</span>? Hành động
        này không thể hoàn tác.
      </>
    );
  }
  return (
    <>
      Bạn có chắc chắn muốn xoá{" "}
      <span style={{ fontWeight: "bold" }}>{fullName}</span> khỏi dự án{" "}
      <span style={{ fontWeight: "bold" }}>{project.name}</span>? Hành động này
      không thể hoàn tác.
    </>
  );
};

const RowOptions = ({ member }) => {
  const { id } = useParams();
  const dispatch = useDispatch();
  const navigate = useNavigate();
  const { myRole, project } = useSelector((state) => state.project);
  const { user, fetchLoading } = useSelector((state) => state.myProfile);

  const [anchorEl, setAnchorEl] = useState(null);
  const rowOptionsOpen = Boolean(anchorEl);
  const [isConfirmDialogOpen, setIsConfirmDialogOpen] = useState(false);
  const [memberToDelete, setMemberToDelete] = useState(null);

  if (fetchLoading) return <CircularProgressLoading />;

  const handleRowOptionsClick = (event) => {
    setAnchorEl(event.currentTarget);
  };

  const handleRowOptionsClose = () => {
    setAnchorEl(null);
  };

  const handleDeleteClick = (member) => {
    setMemberToDelete(member);
    setIsConfirmDialogOpen(true);
  };

  const handleConfirmDelete = async () => {
    if (!memberToDelete) return;
    try {
      await dispatch(
        deleteMember({
          projectId: id,
          memberId: memberToDelete.id,
          roleId: memberToDelete.roleId,
        })
      ).unwrap();
      if (user.id === memberToDelete.id) navigate(`/projects`);

      if (user.id !== memberToDelete.id)
        toast.success("Xóa thành viên thành công");
      else toast.success("Rời dự án thành công");
    } catch (e) {
      console.error(e);
    } finally {
      setIsConfirmDialogOpen(false);
      setMemberToDelete(null);
      handleRowOptionsClose();
    }
  };

  const handleCancelDelete = () => {
    setIsConfirmDialogOpen(false);
    setMemberToDelete(null);
  };

  const canDelete = (targetMember) => {
    if (user.id === targetMember.id) return true; // Everyone can delete themselves
    if (myRole === "owner") return true; // Owners can delete anyone
    if (myRole === "maintainer" && targetMember.roleId === "member")
      return true; // Maintainers can delete members
    return false;
  };

  return (
    <>
      {canDelete(member) && (
        <>
          <IconButton size="small" onClick={handleRowOptionsClick}>
            <Icon icon="mdi:dots-vertical" />
          </IconButton>
          <Menu
            keepMounted
            anchorEl={anchorEl}
            open={rowOptionsOpen}
            onClose={handleRowOptionsClose}
            anchorOrigin={{
              vertical: "bottom",
              horizontal: "right",
            }}
            transformOrigin={{
              vertical: "top",
              horizontal: "right",
            }}
            slotProps={{
              paper: { style: { minWidth: "8rem" } },
            }}
          >
            <MenuItem
              onClick={() => handleDeleteClick(member)}
              sx={{ "& svg": { mr: 2 } }}
            >
              <Icon icon="mdi:delete-outline" fontSize={20} />
              {member.id === user?.id ? `Rời dự án` : `Xoá thành viên`}
            </MenuItem>
          </Menu>
        </>
      )}

      <ConfirmationDialog
        open={isConfirmDialogOpen}
        onClose={handleCancelDelete}
        onConfirm={handleConfirmDelete}
        title="Xoá thành viên"
        content={getContent(user, member, project)}
      />
    </>
  );
};

const RoleSelector = ({ member }) => {
  const [role, setRole] = useState(member.roleId);
  const roles = ["owner", "maintainer", "member"];
  const { id } = useParams();
  const dispatch = useDispatch();
  const { myRole } = useSelector((state) => state.project);
  const { user, fetchLoading } = useSelector((state) => state.myProfile);

  if (fetchLoading) return <CircularProgressLoading />;
  if (myRole !== "owner" || member.id === user.id) {
    return (
      <Box
        sx={{
          display: "flex",
          alignItems: "center",
          height: "100%",
          cursor: "default",
        }}
      >
        <Typography
          sx={{
            color: "text.secondary",
            textTransform: "capitalize",
          }}
        >
          {role}
        </Typography>
      </Box>
    );
  }

  const handleRoleChange = async (memberId, newRole) => {
    try {
      await dispatch(
        updateMemberRole({
          projectId: id,
          userId: memberId,
          roleId: newRole,
        })
      ).unwrap();

      toast.success("Cập nhật vai trò thành công");
    } catch (e) {
      console.error(e);
    }
  };

  const handleChange = (event) => {
    const newRole = event.target.value;
    if (newRole === role) return;
    setRole(newRole);
    handleRoleChange(member.id, newRole);
  };

  return (
    <Select
      value={role}
      onChange={handleChange}
      variant="standard"
      sx={{
        "&.MuiInputBase-root::before, &.MuiInputBase-root::after, &.MuiInputBase-root:hover:not(.Mui-disabled)::before":
          {
            borderBottom: "none !important",
          },
        "& .MuiSelect-select:focus": {
          backgroundColor: "transparent",
        },
        color: "text.secondary",
        textTransform: "capitalize",
      }}
    >
      {roles.map((roleOption) => {
        const displayText = capitalize(roleOption);
        return (
          <MenuItem
            key={roleOption}
            value={roleOption}
            sx={{ textTransform: "capitalize" }}
          >
            {displayText}
          </MenuItem>
        );
      })}
    </Select>
  );
};

const ProjectViewMembers = () => {
  const { members } = useSelector((state) => state.project);
  const { memberTasks } = useSelector((state) => state.tasks);
  const { id: projectId } = useParams();
  const { ref, updateHeight } = usePreventOverflow();
  const [filterMembers, setFilterMembers] = useState(members);
  const [addMemberAnchorEl, setAddMemberAnchorEl] = useState(null);
  const [search, setSearch] = useState("");
  const [openDialog, setOpenDialog] = useState(false);
  const [loading, setLoading] = useState(false);

  const handleAddMenuClick = (event) => {
    setAddMemberAnchorEl(event.currentTarget);
  };

  const handleAddMenuClose = () => {
    setAddMemberAnchorEl(null);
  };

  const dispatch = useDispatch();
  const handleMemberClick = async (projectId, member) => {
    setOpenDialog(true);
    setLoading(true);
    const res = await dispatch(
      fetchTasksForMember({
        projectId: projectId,
        assigneeId: member.id,
      })
    );
    if (res.error) {
      throw res.error;
    }
    setLoading(false);
  };

  const handleDialogClose = () => {
    setOpenDialog(false);
  };

  useEffect(() => {
    setFilterMembers(
      members.filter((member) => {
        const firstName = member.member.firstName ?? "";
        const lastName = member.member.lastName ?? "";
        if (!firstName && !lastName) {
          return false;
        }

        const fullName = `${firstName} ${lastName}`.toLowerCase();
        const id = member.member.id.toLowerCase();
        const email = member.member.email?.toLowerCase() ?? "";
        return (
          fullName.includes(search.toLowerCase()) ||
          id.includes(search.toLowerCase()) ||
          email.includes(search.toLowerCase())
        );
      })
    );
  }, [search, members]);

  useEffect(() => {
    updateHeight(20);
  }, [updateHeight]);

  return (
    <Box>
      <Box
        sx={{ display: "flex", justifyContent: "space-between", mb: 2, px: 2 }}
      >
        <Typography variant="h6">{filterMembers.length} thành viên</Typography>
        <Box
          sx={{
            display: "flex",
            flexWrap: "wrap",
            alignItems: "center",
            gap: 3,
          }}
        >
          <IconButton
            variant="outlined"
            onClick={handleAddMenuClick}
            sx={{
              border: (theme) => `3px dashed ${theme.palette.divider}`,
            }}
            title="Add Member"
          >
            <Icon icon="mdi:account-plus" />
          </IconButton>
          <TextField
            size="small"
            value={search}
            sx={{
              "& .MuiInputBase-root": {
                height: "34px",
                fontSize: "14px",
              },
            }}
            placeholder="Search User"
            onChange={(e) => setSearch(e.target.value)}
          />
          <MenuAddMember
            anchorEl={addMemberAnchorEl}
            onClose={handleAddMenuClose}
          />
        </Box>
      </Box>
      <Card>
        <Divider />
        <div ref={ref}>
          <DataGrid
            rows={filterMembers.map((member) => ({
              ...member,
              id: member.member.id,
            }))}
            columns={columns(handleMemberClick, projectId)}
            // checkboxSelection
            disableRowSelectionOnClick
            pageSizeOptions={[10, 25, 50]}
          />
        </div>
      </Card>
      {memberTasks && (
        <DialogMemberTasks
          open={openDialog}
          onClose={handleDialogClose}
          tasks={memberTasks}
          projectId={projectId}
          loading={loading}
        />
      )}
    </Box>
  );
};

RowOptions.propTypes = {
  member: PropTypes.object.isRequired,
};

RoleSelector.propTypes = {
  member: PropTypes.object.isRequired,
};

export { ProjectViewMembers };
