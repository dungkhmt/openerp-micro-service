import { Icon } from "@iconify/react";
import {
  Box,
  IconButton,
  TextField,
  Tooltip,
  Typography,
  Tab,
  Tabs,
  Grid,
  Card,
} from "@mui/material";
import { DataGrid } from "@mui/x-data-grid";
import { useCallback, useEffect, useState } from "react";
import { useDispatch, useSelector } from "react-redux";
import toast from "react-hot-toast";
import { UserAvatar } from "../../components/common/avatar/UserAvatar";
import { usePreventOverflow } from "../../hooks/usePreventOverflow";
import { fetchAllUsers } from "../../store/user-management";
import { CircularProgressLoading } from "../../components/common/loading/CircularProgressLoading";
import { TabUserProjects } from "./TabUserProjects";
import { TabUserTasks } from "./TabUserTasks";
import PropTypes from "prop-types";

const columns = (setSelectedUser) => [
  {
    flex: 0.4,
    minWidth: 150,
    field: "id",
    headerName: "User",
    renderCell: ({ row }) => {
      const { firstName, lastName, email } = row;
      const fullName =
        firstName || lastName ? `${firstName ?? ""} ${lastName ?? ""}` : " - ";
      return (
        <Box sx={{ display: "flex", alignItems: "center", gap: 2 }}>
          <UserAvatar user={row} />
          <Box
            sx={{
              display: "flex",
              alignItems: "flex-start",
              flexDirection: "column",
            }}
          >
            <Tooltip title={fullName}>
              <Typography
                onClick={() => setSelectedUser(row)}
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
              {`${email ? email : ""}`}
            </Typography>
          </Box>
        </Box>
      );
    },
    display: "flex",
  },
];

function CustomTabPanel(props) {
  const { children, value, index, ...other } = props;

  return (
    <div
      role="tabpanel"
      hidden={value !== index}
      id={`simple-tabpanel-${index}`}
      aria-labelledby={`simple-tab-${index}`}
      {...other}
    >
      {value === index && <Box sx={{ p: 3 }}>{children}</Box>}
    </div>
  );
}

CustomTabPanel.propTypes = {
  children: PropTypes.node,
  index: PropTypes.number.isRequired,
  value: PropTypes.number.isRequired,
};

function a11yProps(index) {
  return {
    id: `simple-tab-${index}`,
    "aria-controls": `simple-tabpanel-${index}`,
  };
}

const UserManagementView = () => {
  const { usersCache } = useSelector((state) => state.userManagement);
  const [filterUsers, setFilterUsers] = useState(usersCache);
  const [search, setSearch] = useState("");
  const [loading, setLoading] = useState(true);
  const [selectedUser, setSelectedUser] = useState();
  const [value, setValue] = useState(0);
  const { ref, updateHeight } = usePreventOverflow();

  const dispatch = useDispatch();

  const handleChange = (event, newValue) => {
    setValue(newValue);
  };

  const getUsers = useCallback(async () => {
    if (usersCache?.length > 0) {
      setLoading(false);
      return;
    }
    try {
      await dispatch(fetchAllUsers());
    } catch (e) {
      console.log(e);
      toast.error("Lỗi khi lấy danh sách người dùng");
    } finally {
      setLoading(false);
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [loading]);

  useEffect(() => {
    setFilterUsers(
      usersCache.filter((user) => {
        const fullName =
          `${user.firstName ?? ""} ${user.lastName ?? ""}`?.toLowerCase() ?? "";
        const id = user.id.toLowerCase();
        const email = user.email?.toLowerCase() ?? "";
        // Filter out all users don't have name and email
        if (!user.firstName && !user.lastName && !user.email) return false;
        return (
          fullName.includes(search.toLowerCase()) ||
          id.includes(search.toLowerCase()) ||
          email.includes(search.toLowerCase())
        );
      })
    );
  }, [search, usersCache]);

  useEffect(() => {
    updateHeight(10);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [window?.innerHeight, ref]);

  useEffect(() => {
    getUsers();
  }, [getUsers]);

  if (loading) {
    return <CircularProgressLoading />;
  }

  return (
    <Grid container spacing={3} sx={{ height: "92vh" }}>
      <Grid
        item
        md={4}
        xs={4}
        sx={{ height: "100%", display: "flex", flexDirection: "column" }}
      >
        <Card
          sx={{
            height: "100%",
            display: "flex",
            flexDirection: "column",
            borderRadius: "20px",
          }}
        >
          <Box
            sx={{
              display: "flex",
              flexDirection: { xs: "column", sm: "row" },
              justifyContent: "space-between",
              alignItems: { xs: "flex-start", sm: "center" },
              mb: 3,
              mt: 3,
              px: 2,
              gap: 2,
            }}
          >
            {/* Title Section */}
            <Typography variant="h6" sx={{ ml: { xs: 0, sm: 2 } }}>
              <span>All users</span>
              <span style={{ color: "grey" }}> {filterUsers.length}</span>
            </Typography>

            {/* Search Bar */}
            <Box
              sx={{
                display: "flex",
                alignItems: "center",
                width: { xs: "100%", sm: "auto" }, 
              }}
            >
              <TextField
                size="small"
                value={search}
                sx={{
                  flexGrow: 1, 
                  maxWidth: { xs: "100%", sm: "250px" }, 
                  "& .MuiInputBase-root": {
                    height: "34px",
                    fontSize: "14px",
                    borderRadius: "20px",
                    width: "100%", 
                  },
                }}
                placeholder="Search User"
                onChange={(e) => setSearch(e.target.value)}
                InputProps={{
                  endAdornment: (
                    <IconButton
                      size="small"
                      title="Clear"
                      aria-label="Clear"
                      onClick={() => setSearch("")}
                      sx={{ padding: 0, marginRight: "-4px" }} 
                    >
                      <Icon icon="mdi:close" fontSize={20} />
                    </IconButton>
                  ),
                }}
              />
            </Box>
          </Box>

          <DataGrid
            rows={filterUsers.map((user) => ({
              ...user,
              id: user.id,
            }))}
            columns={columns(setSelectedUser)}
            columnHeaderHeight={0}
            hideFooter
          />
        </Card>
      </Grid>
      <Grid
        item
        md={8}
        xs={8}
        sx={{ height: "100%", display: "flex", flexDirection: "column" }}
      >
        <Card
          sx={{
            height: "100%",
            display: "flex",
            flexDirection: "column",
            borderRadius: "20px",
          }}
        >
          {selectedUser ? (
            <Box>
              <Box sx={{ borderBottom: 1, borderColor: "divider" }}>
                <Tabs value={value} onChange={handleChange}>
                  <Tab label="Nhiệm vụ" {...a11yProps(0)} />
                  <Tab label="Dự án" {...a11yProps(1)} />
                </Tabs>
              </Box>
              <CustomTabPanel value={value} index={0}>
                <TabUserTasks user={selectedUser} />
              </CustomTabPanel>
              <CustomTabPanel value={value} index={1}>
                <TabUserProjects user={selectedUser} />
              </CustomTabPanel>
            </Box>
          ) : (
            <Box
              display="flex"
              justifyContent="center"
              alignItems="center"
              height="100%"
              flexDirection="column"
              textAlign="center"
            >
              <Typography variant="h5" color="textPrimary" gutterBottom>
                Select a User
              </Typography>
              <Typography variant="body1" color="textSecondary">
                Please select a user to display their assigned tasks and
                projects
              </Typography>
            </Box>
          )}
        </Card>
      </Grid>
    </Grid>
  );
};

export { UserManagementView };
