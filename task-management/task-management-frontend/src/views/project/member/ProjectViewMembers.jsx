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
import { useSelector } from "react-redux";
import { UserAvatar } from "../../../components/common/avatar/UserAvatar";
import { useDebounce } from "../../../hooks/useDebounce";
import { usePreventOverflow } from "../../../hooks/usePreventOverflow";
import { MenuAddMember } from "./MenuAddMember";

/**
 * @type {import("@mui/x-data-grid").GridColDef[]}
 */
const columns = [
  {
    flex: 0.3,
    minWidth: 150,
    field: "id",
    headerName: "User",
    renderCell: ({ row }) => {
      const { firstName, lastName, id } = row.member;
      const fullName =
        firstName || lastName ? `${firstName ?? ""} ${lastName ?? ""}` : " - ";
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
            <Tooltip title={fullName}>
              <Typography
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
            "& svg": { mr: 3, color: "#f00" },
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
  {
    flex: 0.1,
    minWidth: 60,
    sortable: false,
    field: "actions",
    headerName: "Actions",
    renderCell: ({ row }) => <RowOptions id={row.id} />,
  },
];

const RowOptions = ({ id }) => {
  const [anchorEl, setAnchorEl] = useState(null);

  const rowOptionsOpen = Boolean(anchorEl);

  const handleRowOptionsClick = (event) => {
    setAnchorEl(event.currentTarget);
  };
  const handleRowOptionsClose = () => {
    setAnchorEl(null);
  };

  const handleDelete = () => {
    console.log("Delete user with id: ", id);
    toast.error("Delete user feature is not available yet!");
    handleRowOptionsClose();
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
        <MenuItem onClick={handleDelete} sx={{ "& svg": { mr: 2 } }}>
          <Icon icon="mdi:delete-outline" fontSize={20} />
          Xóa
        </MenuItem>
      </Menu>
    </>
  );
};

const ProjectViewMembers = () => {
  const { members } = useSelector((state) => state.project);
  const { ref, updateHeight } = usePreventOverflow();

  const [filterMembers, setFilterMembers] = useState(members);

  const [addMemberAnchorEl, setAddMemberAnchorEl] = useState(null);
  const [search, setSearch] = useState("");
  const searchDebounce = useDebounce(search, 500);

  const handleAddMenuClick = (event) => {
    setAddMemberAnchorEl(event.currentTarget);
  };

  const handleAddMenuClose = () => {
    setAddMemberAnchorEl(null);
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
          fullName.includes(searchDebounce.toLowerCase()) ||
          id.includes(searchDebounce.toLowerCase()) ||
          email.includes(searchDebounce.toLowerCase())
        );
      })
    );
  }, [searchDebounce, members]);

  useEffect(() => {
    updateHeight(10);
  }, []);

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
            columns={columns}
            // checkboxSelection
            disableRowSelectionOnClick
            pageSizeOptions={[10, 25, 50]}
          />
        </div>
      </Card>
    </Box>
  );
};

RowOptions.propTypes = {
  id: PropTypes.string.isRequired,
};

export { ProjectViewMembers };
