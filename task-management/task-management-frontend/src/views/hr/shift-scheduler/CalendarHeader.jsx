import {addDays, format, startOfWeek} from "date-fns";
import {Checkbox, Grid, Typography} from "@mui/material";
import CheckBoxOutlineBlankIcon from "@mui/icons-material/CheckBoxOutlineBlank.js";
import CheckBoxIcon from "@mui/icons-material/CheckBox.js";
import IndeterminateCheckBoxIcon from "@mui/icons-material/IndeterminateCheckBox.js";
import vi from "date-fns/locale/vi";
import React from "react";
import {WEEK_STARTS_ON} from "./ShiftScheduler.jsx";

export default function CalendarHeader({
                          currentDate,
                          stickyTopOffset = 0,
                          onToggleSelectAll,
                          isAllSelectedInView,
                          isIndeterminateInView
                        }) {
  const startDate = startOfWeek(currentDate, { weekStartsOn: WEEK_STARTS_ON });
  const days = Array.from({ length: 7 }).map((_, i) => addDays(startDate, i));
  return (
    <Grid container sx={{
      bgcolor: 'grey.100', borderBottom: 1, borderColor: 'divider',
      position: 'sticky',
      top: 0, // This top will be dynamically adjusted by parent if BulkActionsBar is visible
      zIndex: 10
    }}>
      <Grid item sx={{ width: 160, p: 0.5, borderRight: 1, borderColor: 'divider', display: 'flex', alignItems: 'center' }}>
        <Checkbox
          size="small"
          checked={isAllSelectedInView}
          indeterminate={isIndeterminateInView && !isAllSelectedInView}
          onChange={onToggleSelectAll}
          icon={<CheckBoxOutlineBlankIcon fontSize="small" />}
          checkedIcon={<CheckBoxIcon fontSize="small" />}
          indeterminateIcon={<IndeterminateCheckBoxIcon fontSize="small" />}
          sx={{mr: 0.5, p:0.25}}
          title={isAllSelectedInView ? "Bỏ chọn tất cả ca trong tuần" : (isIndeterminateInView ? "Chọn tất cả ca trong tuần" : "Chọn tất cả ca trong tuần")}
        />
        <Typography variant="caption" sx={{ fontWeight: 'medium', textTransform: 'uppercase', color: 'text.secondary' }}>Nhân viên</Typography>
      </Grid>
      {days.map(day => (
        <Grid item xs key={day.toISOString()} sx={{ p: 1, textAlign: 'center', borderRight: 1, borderColor: 'divider', '&:last-child': { borderRight: 0 } }}>
          <Typography variant="caption" sx={{ color: 'text.secondary', fontWeight:'medium' }}>{format(day, 'EEE', { locale: vi }).toUpperCase()}</Typography>
          <Typography variant="h6" sx={{ fontSize: '1.1rem', color: 'text.primary', fontWeight:'medium' }}>{format(day, 'dd')}</Typography>
        </Grid>
      ))}
    </Grid>
  );
}