import { Box, Tooltip, Typography, Tab, Tabs, Grid, Card } from "@mui/material";
import { DataGrid } from "@mui/x-data-grid";
import { useCallback, useEffect, useState } from "react";
import { useDispatch, useSelector } from "react-redux";
import toast from "react-hot-toast";
import { UserAvatar } from "../../components/common/avatar/UserAvatar";
import { usePreventOverflow } from "../../hooks/usePreventOverflow";
import {
  fetchAllUsers,
  setCurrentUser,
  setTabValue,
} from "../../store/user-management";
import { TabUserProjects } from "./TabUserProjects";
import { TabUserTasks } from "./TabUserTasks";
import PropTypes from "prop-types";
import SearchField from "../../components/mui/search/SearchField";

const columns = [
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

const UserManagementPage = () => {
  const { usersCache, fetchLoading, currentUser, tabValue } = useSelector(
    (state) => state.userManagement
  );
  const { currentOrganization } = useSelector((state) => state.organization);
  const [filterUsers, setFilterUsers] = useState(usersCache);
  const [search, setSearch] = useState("");
  const { ref, updateHeight } = usePreventOverflow();
  const dispatch = useDispatch();

  const handleChange = (event, newValue) => {
    dispatch(setTabValue(newValue));
  };

  const getUsers = useCallback(async () => {
    if (usersCache?.length > 0) return;
    try {
      await dispatch(fetchAllUsers(currentOrganization.id));
    } catch (e) {
      console.error(e);
      toast.error("Lỗi khi lấy danh sách người dùng");
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [dispatch]);

  useEffect(() => {
    getUsers();
  }, [getUsers]);

  useEffect(() => {
    const filtered = usersCache.filter((user) => {
      const fullName = `${user.firstName ?? ""} ${
        user.lastName ?? ""
      }`.toLowerCase();
      const id = user.id.toLowerCase();
      const email = user.email?.toLowerCase() ?? "";

      return (
        fullName.includes(search.toLowerCase()) ||
        id.includes(search.toLowerCase()) ||
        email.includes(search.toLowerCase())
      );
    });

    const sorted = filtered.sort((a, b) => {
      const aHasInfo = a.firstName || a.lastName || a.email;
      const bHasInfo = b.firstName || b.lastName || b.email;
      return aHasInfo === bHasInfo ? 0 : aHasInfo ? -1 : 1;
    });

    setFilterUsers(sorted);
  }, [search, usersCache]);

  useEffect(() => {
    updateHeight(15);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [window.innerHeight]);

  return (
    <Grid container spacing={3}>
      <Grid item md={4} xs={4}>
        <Card
          sx={{
            display: "flex",
            flexDirection: "column",
            borderRadius: "20px",
            mb: -5,
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
              <SearchField
                value={search}
                onChange={(e) => setSearch(e.target.value)}
                onClear={() => setSearch("")}
                inputSx={{
                  flexGrow: 1,
                  maxWidth: { xs: "100%", sm: "250px" },
                  "& .MuiInputBase-root": {
                    height: "34px",
                    fontSize: "14px",
                    borderRadius: "20px",
                    width: "100%",
                  },
                }}
              />
            </Box>
          </Box>

          <Box ref={ref} sx={{pb: 3}}>
            <DataGrid
              rows={filterUsers}
              columns={columns}
              getRowId={(row) => row.id}
              loading={fetchLoading}
              rowSelectionModel={currentUser ? [currentUser.id] : []}
              onRowSelectionModelChange={(newSelection) => {
                const selectedUser = filterUsers.find(
                  (user) => user.id === newSelection[0]
                );
                if (selectedUser) {
                  dispatch(setCurrentUser(selectedUser));
                }
              }}
              columnHeaderHeight={0}
              hideFooterSelectedRowCount
            />
          </Box>
        </Card>
      </Grid>
      <Grid item md={8} xs={8}>
        <Card
          sx={{
            height: "100%",
            display: "flex",
            flexDirection: "column",
            borderRadius: "20px",
          }}
        >
          {currentUser ? (
            <Box>
              <Box sx={{ borderBottom: 1, borderColor: "divider" }}>
                <Tabs value={tabValue} onChange={handleChange}>
                  <Tab label="Nhiệm vụ" {...a11yProps(0)} />
                  <Tab label="Dự án" {...a11yProps(1)} />
                </Tabs>
              </Box>
              <CustomTabPanel value={tabValue} index={0}>
                <TabUserTasks />
              </CustomTabPanel>
              <CustomTabPanel value={tabValue} index={1}>
                <TabUserProjects />
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
                Chọn một người dùng
              </Typography>
              <Typography variant="body1" color="textSecondary">
                Vui lòng chọn một người dùng để hiển thị các nhiệm vụ được giao
                và dự án của họ
              </Typography>
            </Box>
          )}
        </Card>
      </Grid>
    </Grid>
  );
};

export default UserManagementPage;
