// ==============
// ShiftsGrid.jsx
// ==============
import React from 'react';
import {addDays, format, startOfWeek} from 'date-fns';
import {Avatar as MuiAvatar, Box, Grid, Typography} from '@mui/material';
import AccessTimeIcon from '@mui/icons-material/AccessTime'; // For work hours icon
import DayCell from './DayCell.jsx';
import {
  DEPARTMENT_HEADER_ROW_HEIGHT,
  FRONTEND_UNASSIGNED_SHIFT_USER_ID,
  USER_ROW_MIN_HEIGHT,
  WEEK_STARTS_ON
} from "./ShiftScheduler.jsx";

export default function ShiftsGrid({
                                     currentDate,
                                     shifts,
                                     groupedUsers,
                                     onAddShift,
                                     onDeleteShift,
                                     onEditShift,
                                     selectedShiftIds,
                                     onToggleSelectShift,
                                     isAnyShiftSelected,
                                   }) {
  const startDate = startOfWeek(currentDate, { weekStartsOn: WEEK_STARTS_ON });
  const daysOfWeek = Array.from({ length: 7 }).map((_, i) => addDays(startDate, i));

  return (
    <Box>
      {groupedUsers.map(deptGroup => (
        <React.Fragment key={deptGroup.departmentCode}>
          {/* Department Header Row */}
          <Grid container sx={{
            height: DEPARTMENT_HEADER_ROW_HEIGHT,
            alignItems: 'center',
            bgcolor: 'grey.100', // Slightly lighter for department header
            borderBottom: 1,
            borderColor: 'divider',
            // position: 'sticky', // Optional: make sticky if complex scroll management is added
            // top: ... , // Required if sticky
            zIndex: 9,
          }}>
            <Grid item sx={{
              width: 160,
              p: '0px 8px', // Adjusted padding
              borderRight: 1, borderColor: 'divider',
              display: 'flex', alignItems: 'center',
              height: '100%'
            }}>
              <Typography
                variant="subtitle2"
                sx={{
                  fontWeight: '500', // Medium weight
                  fontSize: '0.75rem', // Adjusted font size
                  color: 'text.secondary',
                  textTransform: 'uppercase',
                  whiteSpace: 'nowrap',
                  overflow: 'hidden',
                  textOverflow: 'ellipsis',
                  lineHeight: `${DEPARTMENT_HEADER_ROW_HEIGHT}px` // Center text vertically
                }}
              >
                {deptGroup.departmentName}
              </Typography>
            </Grid>
            {/* Empty cells for day columns in department header - no right border */}
            {daysOfWeek.map(day => (
              <Grid item xs key={`dept-header-${deptGroup.departmentCode}-${day.toISOString()}`} sx={{ p: 1, '&:last-child': { borderRight: 0 } }} />
            ))}
          </Grid>

          {/* User Rows for this Department */}
          {deptGroup.users.map(user => {
            const userShifts = shifts.filter(s => s.userId === user.id && s.userId !== FRONTEND_UNASSIGNED_SHIFT_USER_ID);
            return (
              <Grid container key={user.id} sx={{ borderBottom: 1, borderColor: 'divider', minHeight: USER_ROW_MIN_HEIGHT, '&:last-child': { borderBottom: 0 } }}>
                <Grid item sx={{
                  width: 160, p: 0.75, borderRight: 1, borderColor: 'divider',
                  display: 'flex', flexDirection: 'column', justifyContent: 'center'
                }}>
                  <Box sx={{display: 'flex', alignItems: 'center', mb: 0.5}}>
                    <MuiAvatar
                      src={`https://ui-avatars.com/api/?name=${encodeURIComponent(user.name)}&background=random&color=fff&size=32&font-size=0.4`}
                      alt={user.name}
                      sx={{ width: 28, height: 28, mr: 1, fontSize: '0.75rem' }}
                    />
                    <Typography
                      variant="body1" // Can use body1 or adjust sx
                      sx={{
                        fontWeight: '500', // Bolder
                        fontSize: '0.875rem', // Slightly larger name
                        color: 'text.primary', // Darker (default primary text color)
                        lineHeight: 1.3
                      }}
                    >
                      {user.name}
                    </Typography>
                  </Box>
                  <Typography variant="caption" sx={{ color: 'text.secondary', fontSize: '0.7rem', ml: '36px', display: 'block', lineHeight: 1.3, mb: 0.25 }}>
                    {user.jobPositionName}
                  </Typography>
                  <Box sx={{display: 'flex', alignItems: 'center', color: 'text.secondary', ml: '36px'}}>
                    <AccessTimeIcon sx={{fontSize: '0.8rem', mr: 0.5, color: 'primary.main'}}/>
                    <Typography variant="caption" sx={{ fontSize: '0.7rem', lineHeight: 1.3 }}>
                      {user.workHoursSummary}
                    </Typography>
                  </Box>
                </Grid>
                {daysOfWeek.map(day => {
                  const shiftsInCell = userShifts.filter(s => s.day === format(day, 'yyyy-MM-dd'));
                  return (
                    <DayCell
                      key={`${user.id}-${format(day, 'yyyy-MM-dd')}`}
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
          })}
        </React.Fragment>
      ))}
    </Box>
  );
}