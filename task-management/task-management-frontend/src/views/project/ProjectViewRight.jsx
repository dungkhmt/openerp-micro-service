import { Icon } from "@iconify/react";
import { TabContext, TabPanel } from "@mui/lab";
import PerfectScrollbar from "react-perfect-scrollbar";
import MuiTabList from "@mui/lab/TabList";
import { Box, Tab, styled } from "@mui/material";
import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router";
import { ProjectViewCalendar } from "./calendar/ProjectViewCalendar";
import { ProjectViewMembers } from "./member/ProjectViewMembers";
import { ProjectViewOverview } from "./overview/ProjectViewOverview";
import { ProjectViewTasks } from "./tasks/ProjectViewTasks";
import { ProjectViewGanttChart } from "./gantt-chart/ProjectViewGanttChart";
import { usePreventOverflow } from "../../hooks/usePreventOverflow";

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
  const [activeTab, setActiveTab] = useState(tab);

  const { ref, updateMaxHeight } = usePreventOverflow();

  const navigate = useNavigate();

  const handleChange = (event, value) => {
    navigate(`/project/${id}/${value}`);
  };

  useEffect(() => {
    setActiveTab(tab);
  }, [tab]);

  useEffect(() => {
    updateMaxHeight();
  }, [window?.innerHeight, ref]);

  return (
    <TabContext value={activeTab}>
      <TabList
        variant="scrollable"
        scrollButtons="auto"
        onChange={handleChange}
        sx={{
          position: "sticky",
          top: "46px",
          backgroundColor: "background.default",
          zIndex: 10,
        }}
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
              Lịch
            </Box>
          }
        />
        <Tab
          value="gantt-chart"
          label={
            <Box
              sx={{ display: "flex", alignItems: "center", "& svg": { mr: 2 } }}
            >
              <Icon fontSize={20} icon="fluent:gantt-chart-16-filled" />
              Gantt Chart
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
              <Icon fontSize={20} icon="uiw:setting" />
              Quản lý
            </Box>
          }
        />
      </TabList>
      <Box
        ref={ref}
        sx={{
          flex: 1,
          display: "flex",
          flexDirection: "column",
          overflowY: "auto",
          mt: 1.5,
        }}
      >
        <PerfectScrollbar style={{ flex: 1 }}>
          <TabPanel sx={{ p: 0 }} value="overview">
            <ProjectViewOverview />
          </TabPanel>
          <TabPanel sx={{ p: 0, pr: 2 }} value="tasks">
            <ProjectViewTasks />
          </TabPanel>
          <TabPanel sx={{ p: 0, pr: 2 }} value="timeline">
            <ProjectViewCalendar />
          </TabPanel>
          <TabPanel sx={{ p: 0, pr: 2 }} value="gantt-chart">
            <ProjectViewGanttChart />
          </TabPanel>
          <TabPanel sx={{ p: 0, pr: 2 }} value="members">
            <ProjectViewMembers />
          </TabPanel>
          <TabPanel sx={{ p: 0, pr: 2 }} value="setting">
            Quản lý
          </TabPanel>
        </PerfectScrollbar>
      </Box>
    </TabContext>
  );
};

export { ProjectViewRight };
