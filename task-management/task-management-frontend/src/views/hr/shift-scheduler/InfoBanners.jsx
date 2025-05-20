import {addDays, startOfWeek} from "date-fns";
import {Box, Grid, Paper, Typography} from "@mui/material";
import StarRateIcon from "@mui/icons-material/StarRate.js";
import React from "react";
import {
  AVAILABLE_SHIFTS_BANNER_HEIGHT,
  PROJECTED_SALES_BANNER_HEIGHT,
  TOP_BAR_HEIGHT,
  WEEK_STARTS_ON
} from "./ShiftScheduler.jsx";

export default function InfoBanners({ currentDate, stickyTopOffset = 0 }) {
  const startDate = startOfWeek(currentDate, { weekStartsOn: WEEK_STARTS_ON });
  const days = Array.from({ length: 7 }).map((_, i) => addDays(startDate, i));
  return (
    <Box sx={{position: 'sticky', top: TOP_BAR_HEIGHT + stickyTopOffset, zIndex: 15}}>
      <Paper elevation={0} square sx={{ p: 1, bgcolor: 'green.50', borderBottom: 1, borderColor: 'divider', display: 'flex', alignItems: 'center', height: AVAILABLE_SHIFTS_BANNER_HEIGHT }}>
        <StarRateIcon fontSize="small" sx={{ mr: 1, color: 'orange.400' }} />
        <Typography variant="caption" sx={{ fontWeight: 'medium', color: 'green.800' }}>CA CÓ SẴN</Typography>
      </Paper>
      <Grid container sx={{ bgcolor: 'grey.200', borderBottom: 1, borderColor: 'divider', height: PROJECTED_SALES_BANNER_HEIGHT }}>
        <Grid item sx={{ width: 160, p: 1, borderRight: 1, borderColor: 'divider', display: 'flex', alignItems: 'center' }}>
          <Typography variant="caption" sx={{ fontWeight: 'medium', textTransform: 'uppercase', color: 'text.secondary' }}>Doanh số dự kiến</Typography>
        </Grid>
        {days.map(day => (
          <Grid item xs key={`sales-${day.toISOString()}`} sx={{ p: 1, textAlign: 'center', borderRight: 1, borderColor: 'divider', '&:last-child': { borderRight: 0 }, display: 'flex', alignItems: 'center', justifyContent:'center' }}>
            <Typography variant="caption" color="text.secondary">$0.00</Typography>
          </Grid>
        ))}
      </Grid>
    </Box>
  );
}