import { Icon } from "@iconify/react";
import {
  Box,
  Card,
  CardContent,
  CardHeader,
  Divider,
  Grid,
  IconButton,
  Menu,
  MenuItem,
  TextField,
  Tooltip,
  Typography,
} from "@mui/material";
import { DataGrid } from "@mui/x-data-grid";
import PropTypes from "prop-types";
import { useState } from "react";
import toast from "react-hot-toast";
import CustomAvatar from "../../components/mui/avatar/CustomAvatar";
import { useProjectContext } from "../../hooks/useProjectContext";
import { getRandomColorSkin } from "../../utils/color.util";
import { MenuAddMember } from "./MenuAddMember";

const columns = [
  {
    flex: 0.3,
    minWidth: 150,
    field: "id",
    headerName: "User",
    renderCell: ({ row }) => {
      const { firstName, lastName, id } = row.member;
      const fullName = `${firstName} ${lastName}`;
      return (
        <Box sx={{ display: "flex", alignItems: "center" }}>
          <CustomAvatar
            skin="light"
            color={getRandomColorSkin(id)}
            sx={{ mr: 3, width: 30, height: 30, fontSize: ".875rem" }}
          >
            {`${firstName?.charAt(0) ?? ""}${lastName?.charAt(0) ?? ""}`}
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
        </Box>
      );
    },
  },
  {
    flex: 0.3,
    minWidth: 150,
    field: "email",
    headerName: "Email",
    renderCell: ({ row }) => {
      return (
        <Tooltip title={row.member.email}>
          <Typography noWrap variant="body2">
            {row.member?.email}
          </Typography>
        </Tooltip>
      );
    },
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
  const { members } = useProjectContext();

  const [value, setValue] = useState("");

  return (
    <Grid container spacing={6}>
      <Grid item xs={12}>
        <Card>
          <CardContent
            sx={{ display: "flex", justifyContent: "space-between" }}
          >
            <CardHeader title="Danh sách thành viên" />
            <Box
              sx={{ display: "flex", flexWrap: "wrap", alignItems: "center" }}
            >
              <TextField
                size="small"
                value={value}
                sx={{ mr: 4 }}
                placeholder="Search User"
                onChange={(e) => setValue(e.target.value)}
              />
              <MenuAddMember />
            </Box>
          </CardContent>
          <Divider />
          <DataGrid
            rows={members.map((member) => ({
              ...member,
              id: member.member.id,
            }))}
            columns={columns}
            // checkboxSelection
            disableRowSelectionOnClick
            pageSizeOptions={[10, 25, 50]}
            sx={{ height: "60vh" }}
          />
        </Card>
      </Grid>
    </Grid>
  );
};

RowOptions.propTypes = {
  id: PropTypes.string.isRequired,
};

export { ProjectViewMembers };
