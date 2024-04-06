import { Box, CircularProgress, Tab, Typography, styled } from "@mui/material";
import MuiTabList from "@mui/lab/TabList";
import { TabContext, TabPanel } from "@mui/lab";
import { Icon } from "@iconify/react";
import { useState } from "react";
import { useNavigate, useParams } from "react-router";
import { useEffect } from "react";
import { ProjectViewTasks } from "./tasks/ProjectViewTasks";
import { useProjectContext } from "../../hooks/useProjectContext";
import { ProjectViewMembers } from "./member/ProjectViewMembers";
import { ProjectViewOverview } from "./overview/ProjectViewOverview";

const TabList = styled(MuiTabList)(({ theme }) => ({
  "& .MuiTabs-indicator": {
    backgroundColor: theme.palette.primary.main,
    top: 0,
    height: "40px",
    zIndex: -2,
    borderRadius: theme.shape.borderRadius,
  },
  "& .Mui-selected": {
    color: `${theme.palette.common.white} !important`,
    "&:hover": {
      backgroundColor: `${theme.palette.primary.main} !important`,
    },
  },
  "& .MuiTab-root": {
    minWidth: 65,
    minHeight: 40,
    paddingTop: theme.spacing(2),
    paddingBottom: theme.spacing(2),
    borderRadius: theme.shape.borderRadius,
    [theme.breakpoints.up("md")]: {
      minWidth: 130,
    },
    "&:hover": {
      backgroundColor: theme.palette.grey[200],
    },
  },
}));

const ProjectViewRight = () => {
  const { id, tab } = useParams();
  const { isLoading: projectLoading } = useProjectContext();
  const [activeTab, setActiveTab] = useState(tab);

  const navigate = useNavigate();

  useEffect(() => {
    setActiveTab(tab);
  }, [tab]);

  const handleChange = (event, value) => {
    navigate(`/project/${id}/${value}`);
  };

  return (
    <TabContext value={activeTab}>
      <TabList
        variant="scrollable"
        scrollButtons="auto"
        onChange={handleChange}
      >
        <Tab
          value="overview"
          label={
            <Box
              sx={{ display: "flex", alignItems: "center", "& svg": { mr: 2 } }}
            >
              <Icon fontSize={20} icon="mdi:view-quilt-outline" />
              Tổng quan
            </Box>
          }
        />
        <Tab
          value="tasks"
          label={
            <Box
              sx={{ display: "flex", alignItems: "center", "& svg": { mr: 2 } }}
            >
              <Icon fontSize={20} icon="ic:baseline-task" />
              Nhiệm vụ
            </Box>
          }
        />
        <Tab
          value="timeline"
          label={
            <Box
              sx={{ display: "flex", alignItems: "center", "& svg": { mr: 2 } }}
            >
              <Icon fontSize={20} icon="mdi:calendar" />
              Timeline
            </Box>
          }
        />
        <Tab
          value="activity"
          label={
            <Box
              sx={{ display: "flex", alignItems: "center", "& svg": { mr: 2 } }}
            >
              <Icon fontSize={20} icon="ic:baseline-history" />
              Hoạt động
            </Box>
          }
        />
        <Tab
          value="members"
          label={
            <Box
              sx={{ display: "flex", alignItems: "center", "& svg": { mr: 2 } }}
            >
              <Icon fontSize={20} icon="mdi:account-group" />
              Thành viên
            </Box>
          }
        />
        <Tab
          value="setting"
          label={
            <Box
              sx={{ display: "flex", alignItems: "center", "& svg": { mr: 2 } }}
            >
              <Icon fontSize={20} icon="mdi:application-edit-outline" />
              Quản lý
            </Box>
          }
        />
      </TabList>
      <Box>
        {projectLoading ? (
          <Box
            sx={{
              mt: 6,
              display: "flex",
              alignItems: "center",
              flexDirection: "column",
            }}
          >
            <CircularProgress sx={{ mb: 4 }} />
            <Typography>Loading...</Typography>
          </Box>
        ) : (
          <>
            <TabPanel sx={{ p: 0 }} value="overview">
              <ProjectViewOverview />
            </TabPanel>
            <TabPanel sx={{ p: 0 }} value="tasks">
              <ProjectViewTasks />
            </TabPanel>
            <TabPanel sx={{ p: 0 }} value="timeline">
              Timeline
            </TabPanel>
            <TabPanel sx={{ p: 0 }} value="activity">
              History
            </TabPanel>
            <TabPanel sx={{ p: 0 }} value="members">
              <ProjectViewMembers />
            </TabPanel>
            <TabPanel sx={{ p: 0 }} value="setting">
              Quản lý
            </TabPanel>
          </>
        )}
      </Box>
    </TabContext>
  );
};

export { ProjectViewRight };
