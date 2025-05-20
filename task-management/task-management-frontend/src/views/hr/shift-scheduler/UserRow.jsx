import {addDays, format, isSameDay, isValid, parseISO, startOfWeek} from "date-fns";
import {Avatar, Box, Grid, Typography} from "@mui/material";
import React from "react";
import {WEEK_STARTS_ON} from "./ShiftScheduler.jsx";
import DayCell from "./DayCell.jsx";

const getInitials = (name) => {
  if (!name) return '??';
  const parts = name.split(' ');
  if (parts.length === 1 && parts[0].length > 0) return parts[0].substring(0, 2).toUpperCase();
  if (parts.length > 1 && parts[0].length > 0 && parts[parts.length -1].length > 0) {
    return (parts[0][0] + parts[parts.length - 1][0]).toUpperCase();
  }
  return name.substring(0,2).toUpperCase();
};

export default function UserRow({ user, currentDate, shifts, onAddShift, onDeleteShift, onEditShift, selectedShiftIds, onToggleSelectShift, isAnyShiftSelected }) {
  const startDate = startOfWeek(currentDate, { weekStartsOn: WEEK_STARTS_ON });
  const daysOfWeek = Array.from({ length: 7 }).map((_, i) => addDays(startDate, i));

  return (
    <Grid container sx={{ borderBottom: 1, borderColor: 'divider', '&:hover': { bgcolor: 'action.hover' } }}>
      <Grid item sx={{ width: 160, p: 1, borderRight: 1, borderColor: 'divider', bgcolor: 'grey.50', display: 'flex', flexDirection: 'column', justifyContent: 'center' }}>
        <Box sx={{ display: 'flex', alignItems: 'center', mb: 0.5 }}>
          <Avatar sx={{ width: 28, height: 28, fontSize: '0.8rem', mr: 1, bgcolor: user.avatarBgColor || 'primary.main' }}>
            {user.avatarLetter || getInitials(user.name)}
          </Avatar>
          <Typography variant="body2" sx={{ fontWeight: 'medium', lineHeight: 1.2 }} title={user.name}>{user.name}</Typography>
        </Box>
        <Typography variant="caption" color="text.secondary" sx={{ ml: '36px' }}>{user.summary}</Typography>
      </Grid>
      {daysOfWeek.map(day => {
        const shiftsInCell = shifts.filter(shift =>
          shift.userId === user.id && shift.day && isValid(parseISO(shift.day)) && isSameDay(parseISO(shift.day), day)
        );
        shiftsInCell.sort((a, b) => (a.startTime || "").localeCompare(b.startTime || ""));

        return (
          <DayCell
            key={format(day, 'yyyy-MM-dd')}
            userId={user.id}
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
  );
}