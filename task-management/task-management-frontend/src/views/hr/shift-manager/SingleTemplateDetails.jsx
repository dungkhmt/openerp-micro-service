// src/features/rosterConfiguration/SingleTemplateDetails.jsx
import React from 'react';
import {
  Box, Typography, Paper, List, ListItem, ListItemText, Chip,
  Grid
} from '@mui/material';
import EventAvailableIcon from '@mui/icons-material/EventAvailable';
import BlockIcon from '@mui/icons-material/Block';
import BusinessHoursIcon from '@mui/icons-material/MoreTime';
import TodayIcon from '@mui/icons-material/Today';
import NightsStayIcon from '@mui/icons-material/NightsStay';
import WeekendIcon from '@mui/icons-material/Weekend';
import EventBusyIcon from '@mui/icons-material/EventBusy';
import SyncAltIcon from '@mui/icons-material/SyncAlt';
import AccessTimeFilledIcon from '@mui/icons-material/AccessTimeFilled';

export default function SingleTemplateDetails({ template }) {
  // Provide default fallbacks to prevent errors if properties are missing
  const definedShifts = template?.definedShifts || [];
  const activeHardConstraints = template?.activeHardConstraints || {};

  const constraintDisplayInfo = {
    MAX_CONSECUTIVE_WORK_DAYS: { label: "Ngày làm LT tối đa", icon: <TodayIcon sx={{mr:0.5, fontSize: '1.2rem'}}/> },
    MIN_REST_BETWEEN_SHIFTS_HOURS: { label: "Nghỉ giữa ca (giờ)", icon: <BusinessHoursIcon sx={{mr:0.5, fontSize: '1.2rem'}}/> },
    MAX_WEEKLY_WORK_HOURS: { label: "Giờ tối đa/tuần", icon: <BusinessHoursIcon sx={{mr:0.5, fontSize: '1.2rem'}}/> },
    NO_CLASHING_SHIFTS_FOR_EMPLOYEE: { label: "Không trùng ca (lịch mới)", icon: <BlockIcon sx={{mr:0.5, fontSize: '1.2rem'}}/> },
    MAX_SHIFTS_PER_DAY_FOR_EMPLOYEE: { label: "Ca tối đa/ngày (lịch mới)", icon: <EventAvailableIcon sx={{mr:0.5, fontSize: '1.2rem'}}/> },
    NO_WORK_NEXT_DAY_AFTER_NIGHT_SHIFT: { label: "Nghỉ sau ca đêm", icon: <NightsStayIcon sx={{mr:0.5, fontSize: '1.2rem'}}/> },
    MIN_WEEKEND_DAYS_OFF_PER_PERIOD: { label: "Nghỉ cuối tuần TT", icon: <WeekendIcon sx={{mr:0.5, fontSize: '1.2rem'}}/> },
    ENSURE_EMPLOYEE_APPROVED_LEAVE: { label: "Kiểm tra ngày nghỉ phép NV", icon: <EventBusyIcon sx={{mr:0.5, fontSize: '1.2rem'}} /> },
    AVOID_OVERLAPPING_EXISTING_SHIFTS: { label: "Tránh trùng ca đã có", icon: <SyncAltIcon sx={{mr:0.5, fontSize: '1.2rem'}} /> },
    MAX_DAILY_WORK_HOURS: { label: "Giờ làm tối đa/ngày", icon: <AccessTimeFilledIcon sx={{mr:0.5, fontSize: '1.2rem'}} /> }
  };

  const activeConstraintKeys = Object.keys(activeHardConstraints).filter(key => {
    const value = activeHardConstraints[key];
    return value === true || (typeof value === 'object' && value !== null);
  });

  return (
    <Paper elevation={0} sx={{ p: { xs: 1.5, sm: 2 }, mt: 1.5, backgroundColor: '#fafafa', borderRadius: 1.5, border: '1px solid #e0e0e0' }}>
      <Grid container spacing={2.5}>
        <Grid item xs={12} md={5}>
          <Typography variant="subtitle1" gutterBottom sx={{ color: 'secondary.dark', fontWeight: 'bold', fontSize:'1rem', borderBottom: '1px solid', borderColor:'divider', pb: 0.5, mb:1 }}>
            Ca Làm Việc ({definedShifts.length}) {/* Safe access to length */}
          </Typography>
          {definedShifts.length > 0 ? (
            <List dense sx={{ maxHeight: 200, overflow: 'auto', p:0 }}>
              {definedShifts.map(shift => (
                <ListItem key={shift.id} sx={{py: 0.5, pl:0, borderBottom: '1px solid #f0f0f0', '&:last-child': {borderBottom: 'none'} }}>
                  <ListItemText
                    primary={<Typography variant="body1" component="span" fontWeight="bold" color="text.primary">{shift.name}</Typography>}
                    secondary={`${shift.startTime} - ${shift.endTime} (Min: ${shift.minEmployees}, Max: ${shift.maxEmployees === null || shift.maxEmployees === 0 ? 'KGH' : shift.maxEmployees})`}
                    primaryTypographyProps={{fontSize: '0.95rem'}}
                    secondaryTypographyProps={{fontSize: '0.8rem'}}
                  />
                  {shift.isNightShift && <Chip label="Đêm" color="secondary" size="small" variant="filled" icon={<NightsStayIcon fontSize="inherit"/>} sx={{ml:1, height: '22px', fontSize: '0.75rem', borderRadius:1}}/>}
                </ListItem>
              ))}
            </List>
          ) : <Typography variant="body2" fontStyle="italic">Không có ca nào.</Typography>}
        </Grid>
        <Grid item xs={12} md={7}>
          <Typography variant="subtitle1" gutterBottom sx={{ color: 'secondary.dark', fontWeight: 'bold', fontSize:'1rem', borderBottom: '1px solid', borderColor:'divider', pb: 0.5, mb:1 }}>
            Ràng Buộc Áp Dụng ({activeConstraintKeys.length}) {/* Safe access to length */}
          </Typography>
          {activeConstraintKeys.length > 0 ? (
            <List dense sx={{ maxHeight: 200, overflow: 'auto', p:0 }}>
              {activeConstraintKeys.map((key) => {
                const constraintValue = activeHardConstraints[key];
                const displayInfo = constraintDisplayInfo[key] || { label: key.replace(/_/g, ' ') };
                let valueString = "";
                if (typeof constraintValue === 'boolean' && constraintValue) {
                  valueString = "Đang áp dụng";
                }
                else if (typeof constraintValue === 'object' && constraintValue !== null) {
                  valueString = Object.entries(constraintValue)
                    .map(([pKey, pVal]) => `${pVal}`)
                    .join('; ');
                }
                // This check is now implicitly handled by filtering activeConstraintKeys

                return (
                  <ListItem key={key} sx={{py: 0.25, pl:0, alignItems: 'center', borderBottom: '1px solid #f0f0f0', '&:last-child': {borderBottom: 'none'} }}>
                    <Box sx={{display: 'flex', alignItems: 'center', minWidth: {xs: '150px', sm:'200px'}, mr:1, color: 'text.secondary', flexShrink:0 }}>
                      {displayInfo.icon || <BlockIcon sx={{mr:0.5, fontSize: '1.2rem'}}/>}
                      <Typography variant="body1" component="span" fontWeight="500" sx={{ml:0.25}}>{displayInfo.label}:</Typography>
                    </Box>
                    <Typography variant="subtitle1" color="text.primary" sx={{flexGrow: 1, fontWeight: 'bold'}}>{valueString}</Typography>
                  </ListItem>
                );
              })}
            </List>
          ) : <Typography variant="body2" fontStyle="italic">Không có ràng buộc nào được kích hoạt.</Typography>}
        </Grid>
      </Grid>
    </Paper>
  );
}
