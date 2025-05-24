// ==============
// UnassignedShiftsRow.jsx
// ==============
import React from 'react';
import {Grid, IconButton, Paper, Typography} from '@mui/material';
import {addDays, format, startOfWeek} from 'date-fns';
import PushPinIcon from '@mui/icons-material/PushPin';
import PushPinOutlinedIcon from '@mui/icons-material/PushPinOutlined';
import DayCell from './DayCell.jsx';
import {
  CALENDAR_HEADER_HEIGHT,
  FRONTEND_UNASSIGNED_SHIFT_USER_ID,
  UNASSIGNED_ROW_HEIGHT,
  WEEK_STARTS_ON
} from './ShiftScheduler.jsx';

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
                                            }) {
  const startDate = startOfWeek(currentDate, { weekStartsOn: WEEK_STARTS_ON });
  const daysOfWeek = Array.from({ length: 7 }).map((_, i) => addDays(startDate, i));

  return (
    <Paper
      elevation={0}
      square
      sx={{
        position: isSticky ? 'sticky' : 'relative',
        top: isSticky ? CALENDAR_HEADER_HEIGHT : undefined, // Sticks below CalendarHeader
        zIndex: isSticky ? 12 : 1, // Ensure it's above user rows if sticky, below calendar header
        bgcolor: 'grey.50', // A slightly different background for the unassigned row
        borderBottom: 1,
        borderColor: 'divider',
        minHeight: UNASSIGNED_ROW_HEIGHT, // Maintain a minimum height
        height: 'auto', // Allow height to grow based on content (tallest DayCell)
        boxSizing: 'border-box',
      }}
    >
      <Grid container sx={{ height: '100%' /* Grid container tries to fill Paper's height */ }}>
        <Grid
          item
          sx={{
            width: 160, // Matches CalendarHeader user/title column width
            p: 1,
            borderRight: 1,
            borderColor: 'divider',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'space-between',
            bgcolor: 'grey.100', // Distinct background for the title cell of this row
            alignSelf: 'stretch', // Ensures this title cell stretches vertically with the row
          }}
        >
          <Typography
            variant="caption"
            sx={{
              fontWeight: 'bold',
              color: 'text.secondary',
              textTransform: 'uppercase'
            }}
          >
            Ca chờ gán
          </Typography>
          <IconButton
            onClick={onToggleSticky}
            size="small"
            title={isSticky ? "Bỏ ghim hàng này" : "Ghim hàng này"}
          >
            {isSticky ? <PushPinIcon fontSize="small" color="primary"/> : <PushPinOutlinedIcon fontSize="small" />}
          </IconButton>
        </Grid>
        {daysOfWeek.map(day => {
          const dayString = format(day, 'yyyy-MM-dd');
          // Filter shifts for the current day within this row's already filtered unassigned shifts
          const shiftsInCell = shifts.filter(shift => shift.day === dayString);
          return (
            <DayCell
              key={`${FRONTEND_UNASSIGNED_SHIFT_USER_ID}-${day.toISOString()}`} // Ensure a unique key
              userId={FRONTEND_UNASSIGNED_SHIFT_USER_ID}
              day={day}
              shiftsInCell={shiftsInCell}
              onAddShift={onAddShift}
              onDeleteShift={onDeleteShift}
              onEditShift={onEditShift}
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
