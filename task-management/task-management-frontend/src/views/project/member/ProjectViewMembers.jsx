import { Icon } from "@iconify/react";
import {
  Box,
  Card,
  Divider,
  IconButton,
  Link,
  Menu,
  MenuItem,
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
import { deleteMember } from "../../../store/project";
import { useParams } from "react-router";
import ConfirmationDialog from "../../../components/mui/dialog/ConfirmationDialog";

const columns = (handleMemberClick, projectId, myRole) => {
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
      renderCell: ({ row }) => {
        return (
          <Box
            sx={{
              display: "flex",
              alignItems: "center",
              "& svg": { mr: 3, color: "rgba(255, 0, 0, 0.8)" },
            }}
          >
            <Icon icon="mdi:account" fontSize={20} />
            <Typography
              noWrap
              sx={{ color: "text.secondary", textTransform: "capitalize" }}
            >
              {row.roleId}
            </Typography>
          </Box>
        );
      },
      display: "flex",
    },
  ];

  // Conditionally add the "Actions" field based on the role
  if (myRole === "owner") {
    baseColumns.push({
      flex: 0.1,
      minWidth: 60,
      sortable: false,
      field: "actions",
      headerName: "Actions",
      renderCell: ({ row }) => <RowOptions member={row} />,
    });
  }

  return baseColumns;
};

const getContent = (user, member, project) => {
  const fullName = `${member?.firstName ?? ""} ${
    member?.lastName ?? ""
  }`.trim();
  if (member.roleId === user.id) {
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
  const { project } = useSelector((state) => state.project);
  const { user } = useSelector((state) => state.myProfile);
  const [anchorEl, setAnchorEl] = useState(null);
  const rowOptionsOpen = Boolean(anchorEl);

  const handleRowOptionsClick = (event) => {
    setAnchorEl(event.currentTarget);
  };
  const handleRowOptionsClose = () => {
    setAnchorEl(null);
  };

  const [isConfirmDialogOpen, setIsConfirmDialogOpen] = useState(false);
  const [memberToDelete, setMemberToDelete] = useState(null);

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
      );
      toast.success("Xóa thành viên thành công");
    } catch (e) {
      console.log(e);
      toast.error("Lỗi khi xóa thành viên");
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

  return (
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
        <MenuItem onClick={handleRowOptionsClose} sx={{ "& svg": { mr: 2 } }}>
          <Icon icon="mdi:pencil-outline" fontSize={20} />
          Sửa quyền
        </MenuItem>
        <MenuItem
          onClick={() => handleDeleteClick(member)}
          sx={{ "& svg": { mr: 2 } }}
        >
          <Icon icon="mdi:delete-outline" fontSize={20} />
          {member.id === user.id ? `Rời dự án` : `Xóa`}
        </MenuItem>
      </Menu>

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

const ProjectViewMembers = () => {
  const { members, myRole} = useSelector((state) => state.project);
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
        const fullName =
          `${member.member.firstName ?? ""} ${
            member.member.lastName ?? ""
          }`?.toLowerCase() ?? "";
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
        <Typography variant="h6">{members.length} thành viên</Typography>
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
            columns={columns(handleMemberClick, projectId, myRole)}
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

export { ProjectViewMembers };
