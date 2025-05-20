import React, {useEffect, useState} from "react";
import {addDays, addWeeks, format, parseISO, startOfWeek} from "date-fns";
import {
  Box, Button,
  Checkbox, CircularProgress,
  IconButton,
  List,
  ListItem,
  ListItemButton,
  ListItemIcon, ListItemText,
  Modal,
  Typography
} from "@mui/material";
import CloseIcon from "@mui/icons-material/Close.js";
import {WEEK_STARTS_ON} from "./ShiftScheduler.jsx";

const copyModalStyle = {
  position: 'absolute',
  top: '50%',
  left: '50%',
  transform: 'translate(-50%, -50%)',
  width: { xs: '90%', sm: 400 },
  maxHeight: '80vh',
  bgcolor: 'background.paper',
  border: '1px solid #ccc',
  boxShadow: 24,
  p: 3,
  borderRadius: 1,
  display: 'flex',
  flexDirection: 'column'
};

export default function CopyShiftsModal({ isOpen, onClose, onConfirmCopy, currentDate, numSelectedShifts }) {
  const [selectedWeeks, setSelectedWeeks] = useState([]);
  const [weekOptions, setWeekOptions] = useState([]);
  const [isLoading, setIsLoading] = useState(false);


  useEffect(() => {
    if (isOpen) {
      const options = [];
      const startOfNextWeek = startOfWeek(addDays(currentDate, 7), { weekStartsOn: WEEK_STARTS_ON });
      for (let i = 0; i < 12; i++) {
        const weekStartDate = addWeeks(startOfNextWeek, i);
        const weekEndDate = addDays(weekStartDate, 6);
        options.push({
          value: weekStartDate.toISOString(),
          label: `Tuần ${format(weekStartDate, 'dd/MM')} - ${format(weekEndDate, 'dd/MM/yyyy')}`,
        });
      }
      setWeekOptions(options);
      setSelectedWeeks([]);
    }
  }, [isOpen, currentDate]);

  const handleToggleWeek = (weekValue) => {
    setSelectedWeeks((prev) =>
      prev.includes(weekValue)
        ? prev.filter((w) => w !== weekValue)
        : [...prev, weekValue]
    );
  };

  const handleConfirm = async () => {
    if (selectedWeeks.length === 0) {
      alert("Vui lòng chọn ít nhất một tuần để sao chép.");
      return;
    }
    setIsLoading(true);
    const targetWeekStartDates = selectedWeeks.map(isoString => parseISO(isoString));
    await onConfirmCopy(targetWeekStartDates);
    setIsLoading(false);
    onClose();
  };

  if (!isOpen) return null;

  return (
    <Modal open={isOpen} onClose={onClose} aria-labelledby="copy-shifts-modal-title">
      <Box sx={copyModalStyle}>
        <Box sx={{display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 2, flexShrink: 0}}>
          <Typography id="copy-shifts-modal-title" variant="h6" component="h2">
            Sao chép {numSelectedShifts} ca
          </Typography>
          <IconButton onClick={onClose} size="small" disabled={isLoading}><CloseIcon /></IconButton>
        </Box>

        <Typography variant="body2" sx={{mb:1}}>Chọn (các) tuần muốn sao chép đến:</Typography>

        <Box sx={{ overflowY: 'auto', mb: 2, flexGrow: 1 }}>
          <List dense>
            {weekOptions.map((option) => (
              <ListItem key={option.value} disablePadding>
                <ListItemButton onClick={() => handleToggleWeek(option.value)} dense disabled={isLoading}>
                  <ListItemIcon sx={{minWidth: 32}}>
                    <Checkbox
                      edge="start"
                      checked={selectedWeeks.includes(option.value)}
                      tabIndex={-1}
                      disableRipple
                      size="small"
                      disabled={isLoading}
                    />
                  </ListItemIcon>
                  <ListItemText primary={option.label} primaryTypographyProps={{fontSize: '0.9rem'}}/>
                </ListItemButton>
              </ListItem>
            ))}
          </List>
        </Box>

        <Box sx={{ mt: 'auto', display: 'flex', justifyContent: 'flex-end', gap: 1, flexShrink: 0, pt:1, borderTop: '1px solid', borderColor:'divider' }}>
          <Button onClick={onClose} variant="outlined" color="inherit" disabled={isLoading}>Hủy</Button>
          <Button
            onClick={handleConfirm}
            variant="contained"
            color="primary"
            disabled={isLoading || selectedWeeks.length === 0}
            startIcon={isLoading ? <CircularProgress size={16} color="inherit"/> : null}
          >
            {isLoading ? "Đang sao chép..." : `Sao chép (${selectedWeeks.length} tuần)`}
          </Button>
        </Box>
      </Box>
    </Modal>
  );
}