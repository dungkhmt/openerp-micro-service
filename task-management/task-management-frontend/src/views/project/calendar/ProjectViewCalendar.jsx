import { Box, useMediaQuery } from "@mui/material";
import { useState } from "react";
import CalendarWrapper from "../../../components/calendar/CalendarWrapper";
import { AddEventSidebar } from "./AddEventSidebar";
import Calendar from "./Calendar";

const ProjectViewCalendar = () => {
  const mdAbove = useMediaQuery((theme) => theme.breakpoints.up("md"));
  const [addEventSidebarOpen, setAddEventSidebarOpen] = useState(false);

  const handleAddEventSidebarToggle = () => {
    setAddEventSidebarOpen(!addEventSidebarOpen);
  };

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
