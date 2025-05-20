import {AppBar, Box, Button, IconButton, Toolbar, Typography} from "@mui/material";
import ChevronLeftIcon from "@mui/icons-material/ChevronLeft.js";
import EventNoteIcon from "@mui/icons-material/EventNote.js";
import ChevronRightIcon from "@mui/icons-material/ChevronRight.js";
import {format} from "date-fns";
import vi from "date-fns/locale/vi";
import FilterAltIcon from "@mui/icons-material/FilterAlt.js";
import FileDownloadIcon from "@mui/icons-material/FileDownload.js";
import SettingsIcon from "@mui/icons-material/Settings.js";
import {TOP_BAR_HEIGHT} from "./ShiftScheduler.jsx";

export default function TopBar({ currentDate, onPrevWeek, onNextWeek, onToday }) {
  return (
    <AppBar position="sticky" color="default" elevation={1} sx={{zIndex: 20, height: TOP_BAR_HEIGHT}}>
      <Toolbar variant="dense">
        <Typography variant="h6" component="div" sx={{ flexGrow: 0.1, mr:1, fontSize:'1.1rem', color:'text.primary' }}>Lịch làm việc</Typography>
        <IconButton onClick={onPrevWeek} size="small" aria-label="Previous week"><ChevronLeftIcon /></IconButton>
        <Button onClick={onToday} size="small" variant="outlined" color="inherit" startIcon={<EventNoteIcon />} sx={{ mx: 1, fontSize:'0.75rem', py:0.3}}>Hôm nay</Button>
        <IconButton onClick={onNextWeek} size="small" aria-label="Next week"><ChevronRightIcon /></IconButton>
        <Typography variant="subtitle1" component="div" sx={{ ml: 2, color:'text.secondary', fontWeight:'medium' }}>
          {format(currentDate, 'MMMM yyyy', { locale: vi })}
        </Typography>
        <Box sx={{ flexGrow: 1 }} />
        <Button size="small" variant="text" color="inherit" startIcon={<FilterAltIcon />} sx={{textTransform:'none', fontSize:'0.8rem'}}>Lọc</Button>
        <Button size="small" variant="text" color="inherit" startIcon={<FileDownloadIcon />} sx={{textTransform:'none', fontSize:'0.8rem'}}>Xuất</Button>
        <IconButton color="inherit" size="small"><SettingsIcon /></IconButton>
      </Toolbar>
    </AppBar>
  );
}