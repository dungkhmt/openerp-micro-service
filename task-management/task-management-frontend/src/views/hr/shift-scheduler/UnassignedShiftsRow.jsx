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
                                              shifts,
                                              onAddShift,
                                              onDeleteShift,
                                              onEditShift,
                                              selectedShiftIds,
                                              onToggleSelectShift,
                                              isAnyShiftSelected,
                                              isSticky,
                                              onToggleSticky,
                                              canAdmin
                                            }) {
  const startDate = startOfWeek(currentDate, { weekStartsOn: WEEK_STARTS_ON });
  const daysOfWeek = Array.from({ length: 7 }).map((_, i) => addDays(startDate, i));

  return (
    <Paper
      elevation={0}
      square
      sx={{
        position: isSticky ? 'sticky' : 'relative',
        top: isSticky ? CALENDAR_HEADER_HEIGHT : undefined,
        zIndex: isSticky ? 12 : 1,
        bgcolor: 'grey.50',
        borderBottom: 1,
        borderColor: 'divider',
        minHeight: UNASSIGNED_ROW_HEIGHT,
        height: 'auto',
        boxSizing: 'border-box',
      }}
    >
      <Grid container sx={{ height: '100%' }}>
        <Grid
          item
          sx={{
            width: 160,
            p: 1,
            borderRight: 1,
            borderColor: 'divider',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'space-between',
            bgcolor: 'grey.100',
            alignSelf: 'stretch',
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
          const shiftsInCell = shifts.filter(shift => shift.day === dayString);
          return (
            <DayCell
              key={`${FRONTEND_UNASSIGNED_SHIFT_USER_ID}-${day.toISOString()}`}
              userId={FRONTEND_UNASSIGNED_SHIFT_USER_ID}
              day={day}
              shiftsInCell={shiftsInCell}
              onAddShift={onAddShift}
              onDeleteShift={onDeleteShift}
              onEditShift={onEditShift}
              selectedShiftIds={selectedShiftIds}
              onToggleSelectShift={onToggleSelectShift}
              isAnyShiftSelected={isAnyShiftSelected}
              canAdmin={canAdmin}
            />
          );
        })}
      </Grid>
    </Paper>
  );
}