// ==============
// TopBar.jsx
// ==============
import React from "react";
import {AppBar, Box, Button, IconButton, Toolbar, Tooltip, Typography} from "@mui/material";
import {format} from "date-fns";
import vi from "date-fns/locale/vi";
import KeyboardArrowLeftIcon from "@mui/icons-material/KeyboardArrowLeft.js";
import KeyboardArrowRightIcon from "@mui/icons-material/KeyboardArrowRight.js";
import TodayIcon from "@mui/icons-material/Today.js";
import FilterListIcon from "@mui/icons-material/FilterList.js"; // Import FilterListIcon
import {TOP_BAR_HEIGHT} from "./ShiftScheduler.jsx";

export default function TopBar({ currentDate, onPrevWeek, onNextWeek, onToday, onOpenFilterDrawer }) { // Add onOpenFilterDrawer prop
  return (
    <AppBar
      position="sticky"
      color="default"
      elevation={1}
      sx={{ height: TOP_BAR_HEIGHT, bgcolor: 'background.paper', zIndex: 20, borderBottom: theme => `1px solid ${theme.palette.divider}`}}
    >
      <Toolbar variant="dense" sx={{ justifyContent: 'space-between', alignItems: 'center', height: '100%' }}>
        <Box sx={{ display: 'flex', alignItems: 'center' }}>
          <Tooltip title="Tuần trước">
            <IconButton onClick={onPrevWeek} size="small" sx={{mr: 0.5}}>
              <KeyboardArrowLeftIcon />
            </IconButton>
          </Tooltip>
          <Tooltip title="Tuần sau">
            <IconButton onClick={onNextWeek} size="small" sx={{mr: 1}}>
              <KeyboardArrowRightIcon />
            </IconButton>
          </Tooltip>
          <Button onClick={onToday} variant="outlined" size="small" startIcon={<TodayIcon />} sx={{textTransform: 'none', color: 'text.primary', borderColor: 'grey.400'}}>
            Hôm nay
          </Button>
        </Box>

        <Typography variant="h6" component="div" sx={{ fontWeight: 'medium', color:'text.primary' }}>
          {format(currentDate, "'Tháng' M, yyyy", { locale: vi })}
        </Typography>

        <Box sx={{ display: 'flex', alignItems: 'center' }}>
          {/* Add other buttons here if needed */}
          <Tooltip title="Lọc nhân viên">
            <IconButton onClick={onOpenFilterDrawer} size="small" sx={{ml: 1}}>
              <FilterListIcon />
            </IconButton>
          </Tooltip>
        </Box>
      </Toolbar>
    </AppBar>
  );
}