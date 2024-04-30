import { Box, useMediaQuery } from "@mui/material";
import { useState, useEffect } from "react";
import { useDispatch, useSelector } from "react-redux";
import { useParams } from "react-router-dom";
import CalendarWrapper from "../../../components/calendar/CalendarWrapper";
import { fetchCalendarTasks } from "../../../store/project/calendar";
import { AddEventSidebar } from "./AddEventSidebar";
import Calendar from "./Calendar";
import dayjs from "dayjs";

const ProjectViewCalendar = () => {
  const { id: projectId } = useParams();
  const mdAbove = useMediaQuery((theme) => theme.breakpoints.up("md"));
  const [addEventSidebarOpen, setAddEventSidebarOpen] = useState(false);
  const [isInit, setIsInit] = useState(false);
  const dispatch = useDispatch();
  const { range, tasks } = useSelector((state) => state.calendar);

  const handleAddEventSidebarToggle = () => {
    setAddEventSidebarOpen(!addEventSidebarOpen);
  };

  useEffect(() => {
    // Avoid the first fetch when the component is mounted
    if (!isInit && tasks.length > 0) {
      setIsInit(true);
      return;
    }
    dispatch(
      fetchCalendarTasks({
        projectId,
        from: dayjs(range.startDate).unix(),
        to: dayjs(range.endDate).unix(),
      })
    );
  }, [dispatch, projectId, range]);

  return (
    <CalendarWrapper
      sx={{
        border: (theme) => `1px solid ${theme.palette.divider}`,
      }}
    >
      <Box
        sx={{
          p: 5,
          pb: 0,
          flexGrow: 1,
          borderRadius: 1,
          boxShadow: "none",
          backgroundColor: "background.paper",
          ...(mdAbove && { borderTopLeftRadius: 0, borderBottomLeftRadius: 0 }),
        }}
      >
        <Calendar handleAddEventSidebarToggle={handleAddEventSidebarToggle} />
      </Box>
      <AddEventSidebar
        addEventSidebarOpen={addEventSidebarOpen}
        handleAddEventSidebarToggle={handleAddEventSidebarToggle}
      />
    </CalendarWrapper>
  );
};

export { ProjectViewCalendar };
