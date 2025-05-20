// ==============
// UnassignedShiftsRow.jsx (NEW FILE)
// ==============
import React from 'react';
import { Grid, Typography, Box, IconButton, Paper } from '@mui/material';
import { addDays, format, startOfWeek } from 'date-fns';
import PushPinIcon from '@mui/icons-material/PushPin';
import PushPinOutlinedIcon from '@mui/icons-material/PushPinOutlined';
import DayCell from './DayCell.jsx'; // Assuming DayCell can handle unassigned shifts
import { WEEK_STARTS_ON, UNASSIGNED_SHIFT_USER_ID, UNASSIGNED_ROW_HEIGHT } from './ShiftScheduler.jsx';

export default function UnassignedShiftsRow({
                                              currentDate,
                                              shifts, // These are pre-filtered unassigned shifts
                                              onAddShift,
                                              onDeleteShift,
                                              onEditShift,
                                              selectedShiftIds,
                                              onToggleSelectShift,
                                              isAnyShiftSelected,
                                              isSticky,
                                              onToggleSticky,
                                              stickyTopOffset = 0,
                                            }) {
  const startDate = startOfWeek(currentDate, { weekStartsOn: WEEK_STARTS_ON });
  const daysOfWeek = Array.from({ length: 7 }).map((_, i) => addDays(startDate, i));

  return (
    <Paper
      elevation={0}
      square
      sx={{
        position: isSticky ? 'sticky' : 'relative',
        top: isSticky ? stickyTopOffset : undefined,
        zIndex: isSticky ? 12 : 1, // Above user rows if sticky, below calendar header
        bgcolor: 'grey.50',
        borderBottom: 1,
        borderColor: 'divider',
        height: UNASSIGNED_ROW_HEIGHT,
        minHeight: UNASSIGNED_ROW_HEIGHT,
        boxSizing: 'border-box',
      }}
    >
      <Grid container sx={{ height: '100%'}}>
        <Grid
          item
          sx={{
            width: 160, // Matches CalendarHeader user column width
            p: 1,
            borderRight: 1,
            borderColor: 'divider',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'space-between',
            bgcolor: 'grey.100',
          }}
        >
          <Typography variant="caption" sx={{ fontWeight: 'bold', color: 'text.secondary', textTransform: 'uppercase' }}>
            Ca chờ gán
          </Typography>
          <IconButton onClick={onToggleSticky} size="small" title={isSticky ? "Bỏ ghim hàng này" : "Ghim hàng này"}>
            {isSticky ? <PushPinIcon fontSize="small" color="primary"/> : <PushPinOutlinedIcon fontSize="small" />}
          </IconButton>
        </Grid>
        {daysOfWeek.map(day => {
          const dayString = format(day, 'yyyy-MM-dd');
          const shiftsInCell = shifts.filter(shift => shift.day === dayString);
          return (
            <DayCell
              key={day.toISOString()}
              userId={UNASSIGNED_SHIFT_USER_ID} // Special ID for unassigned DayCells
              day={day}
              shiftsInCell={shiftsInCell}
              onAddShift={onAddShift} // onAddShift(UNASSIGNED_SHIFT_USER_ID, day) will be called
              onDeleteShift={onDeleteShift}
              onEditShift={onEditShift} // onEditShift(shift)
              selectedShiftIds={selectedShiftIds}
              onToggleSelectShift={onToggleSelectShift}
              isAnyShiftSelected={isAnyShiftSelected}
            />
          );
        })}
      </Grid>
    </Paper>
  );
}