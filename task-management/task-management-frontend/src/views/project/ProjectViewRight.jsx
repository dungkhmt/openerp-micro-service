import { Icon } from "@iconify/react";
import { TabContext, TabPanel } from "@mui/lab";
import MuiTabList from "@mui/lab/TabList";
import { Box, Button, Divider, Tab, styled } from "@mui/material";
import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router";
import { usePreventOverflow } from "../../hooks/usePreventOverflow";
import { ProjectBreadcrumb } from "./ProjectBreadcrumb";
import { ProjectViewCalendar } from "./calendar/ProjectViewCalendar";
import { ProjectViewGanttChart } from "./gantt-chart/ProjectViewGanttChart";
import { ProjectViewMembers } from "./member/ProjectViewMembers";
import { ProjectViewOverview } from "./overview/ProjectViewOverview";
import { ProjectViewSetting } from "./setting/ProjectViewSetting";
import { DialogAddTask } from "./tasks/DialogAddTask";
import { ProjectViewTasks } from "./tasks/ProjectViewTasks";
import { ProjectViewEvents } from "./events/ProjectViewEvents";

const TabList = styled(MuiTabList)(({ theme }) => ({
  minHeight: "34px",
  marginTop: theme.spacing(1.5),
  "& .MuiTabs-scroller": {
    paddingBottom: theme.spacing(1.5),
  },
  "& .MuiTabs-indicator": {
    backgroundColor: theme.palette.primary.main,
    height: "1.5px",
  },
  "& .Mui-selected": {
    color: `${theme.palette.text.primary} !important`,
    fontWeight: 600,
    "&:hover": {
      borderRadius: theme.shape.borderRadius,
    },
  },
  "& .MuiTab-root": {
    minWidth: "auto",
    minHeight: 30,
    borderRadius: theme.shape.borderRadius,
    "&:hover": {
      backgroundColor: theme.palette.grey[300],
    },
    textTransform: "none",
    fontSize: 14,
    fontWeight: 550,
    padding: theme.spacing(1, 1.5),
    margin: theme.spacing(0, 1),
  },
}));

const ProjectViewRight = () => {
  const { id, tab } = useParams();
  const [activeTab, setActiveTab] = useState(tab);
  const [openAddTask, setOpenAddTask] = useState(false);

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
    window.addEventListener("resize", updateMaxHeight);
    return () => window.removeEventListener("resize", updateMaxHeight);
  }, [updateMaxHeight]);

  return (
    <TabContext value={activeTab}>
      <ProjectBreadcrumb />
      <Divider sx={{ mt: 1 }} />
      <Box
        sx={{
          display: "flex",
          justifyContent: "space-between",
          alignItems: "center",
        }}
      >
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
          size="small"
        >
          <Tab
            value="overview"
            label={
              <Box
                sx={{
                  display: "flex",
                  alignItems: "center",
                  "& svg": { mr: 1, fontSize: "16px" },
                }}
              >
                <Icon fontSize={20} icon="fluent:board-20-regular" />
                Tổng quan
              </Box>
            }
            size="small"
          />
          <Tab
            value="tasks"
            label={
              <Box
                sx={{
                  display: "flex",
                  alignItems: "center",
                  "& svg": { mr: 1, fontSize: "16px" },
                }}
              >
                <Icon fontSize={20} icon="ion:list-outline" />
                Nhiệm vụ
              </Box>
            }
          />
          <Tab
            value="events"
            label={
              <Box
                sx={{
                  display: "flex",
                  alignItems: "center",
                  "& svg": { mr: 1, fontSize: "16px" },
                }}
              >
                <Icon fontSize={20} icon="ph:video-conference" />
                Sự kiện
              </Box>
            }
          />
          <Tab
            value="timeline"
            label={
              <Box
                sx={{
                  display: "flex",
                  alignItems: "center",
                  "& svg": { mr: 1, fontSize: "16px" },
                }}
              >
                <Icon fontSize={20} icon="ph:calendar" />
                Lịch
              </Box>
            }
          />
          <Tab
            value="gantt-chart"
            label={
              <Box
                sx={{
                  display: "flex",
                  alignItems: "center",
                  "& svg": { mr: 1, fontSize: "16px" },
                }}
              >
                <Icon fontSize={20} icon="fluent:gantt-chart-16-regular" />
                Gantt
              </Box>
            }
          />
          <Tab
            value="members"
            label={
              <Box
                sx={{
                  display: "flex",
                  alignItems: "center",
                  "& svg": { mr: 1, fontSize: "16px" },
                }}
              >
                <Icon fontSize={20} icon="tdesign:member" />
                Thành viên
              </Box>
            }
          />
          <Tab
            value="setting"
            label={
              <Box
                sx={{
                  display: "flex",
                  alignItems: "center",
                  "& svg": { mr: 1, fontSize: "16px" },
                }}
              >
                <Icon fontSize={20} icon="uil:setting" />
                Quản lý
              </Box>
            }
          />
        </TabList>
        <Box sx={{ mr: 4 }}>
          <Button
            variant="contained"
            size="small"
            sx={{
              textTransform: "capitalize",
              padding: (theme) => theme.spacing(0.5, 2),
            }}
            onClick={() => setOpenAddTask(true)}
          >
            Thêm việc
          </Button>
        </Box>
      </Box>
      <Divider />
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
        <TabPanel sx={{ p: 0 }} value="overview">
          <ProjectViewOverview />
        </TabPanel>
        <TabPanel sx={{ p: 0, pr: 2 }} value="tasks">
          <ProjectViewTasks />
        </TabPanel>
        <TabPanel sx={{ p: 0, pr: 2 }} value="events">
          <ProjectViewEvents />
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
          <ProjectViewSetting />
        </TabPanel>
      </Box>
      <DialogAddTask open={openAddTask} setOpen={setOpenAddTask} />
    </TabContext>
  );
};

export { ProjectViewRight };
