// ==============
// InfoBanners.jsx
// ==============
import {startOfWeek} from "date-fns";
import {Box, Paper, Typography} from "@mui/material";
import StarRateIcon from "@mui/icons-material/StarRate.js";
import React from "react";
import {AVAILABLE_SHIFTS_BANNER_HEIGHT, TOP_BAR_HEIGHT, WEEK_STARTS_ON} from "./ShiftScheduler.jsx";

export default function InfoBanners({ currentDate, stickyTopOffset = 0 }) {
  const startDate = startOfWeek(currentDate, { weekStartsOn: WEEK_STARTS_ON });

  return (
    <Box sx={{position: 'sticky', top: TOP_BAR_HEIGHT + stickyTopOffset, zIndex: 15}}>
      <Paper elevation={0} square sx={{ p: 1, bgcolor: 'green.50', borderBottom: 1, borderColor: 'divider', display: 'flex', alignItems: 'center', height: AVAILABLE_SHIFTS_BANNER_HEIGHT }}>
        <StarRateIcon fontSize="small" sx={{ mr: 1, color: 'orange.400' }} />
        <Typography variant="caption" sx={{ fontWeight: 'medium', color: 'green.800' }}>LỊCH LÀM VIỆC</Typography>
      </Paper>
    </Box>
  );
}