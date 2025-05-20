// ==============
// ShiftCard.jsx
// ==============
import React, {useState} from "react";
import {Box, Checkbox, IconButton, Paper, Typography, Chip} from "@mui/material";
import {alpha} from "@mui/material/styles"; // Ensure this import is correct
import CheckBoxOutlineBlankIcon from "@mui/icons-material/CheckBoxOutlineBlank.js";
import CheckBoxIcon from "@mui/icons-material/CheckBox.js";
import {parseISO} from "date-fns";
import AddIcon from "@mui/icons-material/Add.js";
import ControlPointIcon from '@mui/icons-material/ControlPoint';
import { UNASSIGNED_SHIFT_USER_ID } from "./ShiftScheduler.jsx";

export default function ShiftCard({
                                    shift,
                                    onEditShift,
                                    onAddAnotherShift,
                                    onAddShift,
                                    provided,
                                    snapshot,
                                    isSelected,
                                    onToggleSelect,
                                    isAnyShiftSelected
                                  }) {
  const [isHovered, setIsHovered] = useState(false);

  const handleCheckboxClick = (e) => {
    e.stopPropagation();
    onToggleSelect(shift.id);
  };

  const handleCardBodyClick = (e) => {
    if (e.defaultPrevented) return;
    if (e.target.closest('.selection-checkbox-area') ||
      e.target.closest('.add-action-button-area') ||
      e.target.closest('.add-unassigned-button-area')) return;

    if (!isAnyShiftSelected) {
      onEditShift(shift);
    } else {
      onToggleSelect(shift.id);
    }
  };

  const handleAddNewUnassignedFromCard = (e) => {
    e.stopPropagation();
    if (onAddShift) {
      onAddShift(UNASSIGNED_SHIFT_USER_ID, parseISO(shift.day));
    }
  };

  const showCheckbox = (isHovered || isAnyShiftSelected) && !snapshot.isDragging;
  const showAddButtonForUserShift = isHovered && !isAnyShiftSelected && !snapshot.isDragging && shift.userId !== UNASSIGNED_SHIFT_USER_ID;
  const isUnassigned = shift.userId === UNASSIGNED_SHIFT_USER_ID;
  const showAddButtonForUnassignedShift = isUnassigned && isHovered && !snapshot.isDragging;

  return (
    <Paper
      ref={provided.innerRef}
      {...provided.draggableProps}
      onMouseEnter={() => setIsHovered(true)}
      onMouseLeave={() => setIsHovered(false)}
      elevation={snapshot.isDragging ? 4 : (isSelected ? 3 : 1)}
      sx={{
        my: 0.5,
        height: 50,
        minHeight: 50,
        boxSizing: 'border-box',
        bgcolor: snapshot.isDragging
          ? 'primary.lighter' // Direct theme path for sx, OK
          : (isSelected ? (theme) => alpha(theme.palette.primary.main, 0.12) // Correct use of alpha
            : (isUnassigned ? 'grey.200' : 'background.paper')), // Direct theme paths for sx, OK
        border: isSelected
          ? (theme) => `2px solid ${theme.palette.primary.main}` // Direct theme color, OK
          : (theme) => `1px dashed ${alpha(isUnassigned ? theme.palette.grey[500] : theme.palette.primary.main, 0.5)}`, // Correct use of alpha
        position: 'relative',
        display: 'flex',
        alignItems: 'center',
        transition: 'all 0.2s ease',
        width: '100%',
        maxWidth: '100%',
        overflow: 'hidden',
      }}
    >
      <Box
        className="selection-checkbox-area"
        sx={{
          display: 'flex', alignItems: 'center', justifyContent: 'center',
          width: showCheckbox ? 32 : 0,
          flexShrink: 0, height: '100%',
          transition: 'width 0.15s ease-in-out', overflow: 'hidden', zIndex: 1,
        }}
      >
        <Checkbox
          size="small" checked={isSelected} onChange={handleCheckboxClick} onClick={(e) => e.stopPropagation()}
          icon={<CheckBoxOutlineBlankIcon fontSize="small" />} checkedIcon={<CheckBoxIcon fontSize="small" />}
          sx={{ p: 0.5, opacity: showCheckbox ? 1 : 0, visibility: showCheckbox ? 'visible' : 'hidden', transition: 'opacity 0.1s ease-in-out, visibility 0.1s ease-in-out' }}
          tabIndex={showCheckbox ? 0 : -1}
        />
      </Box>

      <Box
        {...provided.dragHandleProps}
        onClick={handleCardBodyClick}
        sx={{
          flexGrow: 1, minWidth: 0, height: '100%',
          display: 'flex', flexDirection: 'column', justifyContent: 'center',
          pl: showCheckbox ? 0.5 : (isUnassigned ? 1.25 : 1.5),
          pr: showAddButtonForUserShift ? 4.5 : (showAddButtonForUnassignedShift ? 4.0 : (isUnassigned ? (shift.slots !== undefined ? 3.5 : 1.25) : 1.5)),
          cursor: snapshot.isDragging ? 'grabbing' : 'grab',
          overflow: 'hidden', userSelect: 'none',
          transition: 'padding-left 0.15s ease-in-out, padding-right 0.15s ease-in-out',
        }}
      >
        <Typography
          variant="caption" component="div"
          sx={{
            fontWeight: 'bold', color: shift.muiTextColor || 'text.primary',
            fontSize: '0.68rem', lineHeight: 1.3,
            whiteSpace: 'nowrap', overflow: 'hidden', textOverflow: 'ellipsis'
          }}
        >
          {`${shift.startTime} - ${shift.endTime}`}
          <Typography component="span" variant="caption" sx={{ ml: 0.5, color: shift.muiTextColor, opacity: 0.8, fontSize: '0.65rem' }}>
            ({shift.duration})
          </Typography>
        </Typography>

        {shift.note && shift.note.trim() !== '' && (
          <Typography
            variant="body2"
            sx={{
              fontSize: '0.65rem', color: shift.muiTextColor ? shift.muiTextColor : 'text.secondary',
              opacity: shift.muiTextColor ? 0.85 : 1.0, lineHeight: 1.3,
              whiteSpace: 'nowrap', overflow: 'hidden', textOverflow: 'ellipsis',
            }}
            title={shift.note}
          >
            {shift.note}
          </Typography>
        )}
      </Box>

      {isUnassigned && typeof shift.slots === 'number' && !showAddButtonForUnassignedShift && (
        <Chip
          label={shift.slots}
          size="small"
          sx={{
            position: 'absolute',
            top: '50%',
            right: '8px',
            transform: 'translateY(-50%)',
            height: '18px',
            fontSize: '0.65rem',
            lineHeight: '18px',
            padding: '0 4px',
            minWidth: '18px',
            // MODIFIED: Using a hardcoded hex color with alpha for reliability
            backgroundColor: alpha('#1976d2', 0.7), // Example: MUI primary blue with 70% opacity
            // You can change '#1976d2' to your desired hex color.
            // Common MUI primary blue: '#1976d2'
            // Common MUI secondary pink: '#d32f2f' (for error-like) or '#9c27b0' (purple)
            // Neutral grey: alpha('#616161', 0.7) (grey[700])
            color: 'white',
            zIndex: 1,
            transition: 'opacity 0.15s ease-in-out, right 0.15s ease-in-out',
          }}
        />
      )}

      {showAddButtonForUnassignedShift && (
        <Box
          className="add-unassigned-button-area"
          sx={{
            position: 'absolute',
            top: '50%',
            right: '4px',
            transform: 'translateY(-50%)',
            zIndex: 2,
          }}
        >
          <IconButton
            size="small"
            onClick={handleAddNewUnassignedFromCard}
            sx={{
              p: 0.3,
              bgcolor: 'rgba(0,0,0,0.08)',
              '&:hover': { bgcolor: 'rgba(0,0,0,0.12)' },
              borderRadius: '4px',
            }}
            title="Thêm ca chờ gán mới vào ngày này"
          >
            <ControlPointIcon sx={{ fontSize: '1.1rem' }} color="action" />
          </IconButton>
        </Box>
      )}

      {showAddButtonForUserShift && (
        <Box
          className="add-action-button-area"
          sx={{
            position: 'absolute', top: '50%', right: '4px', transform: 'translateY(-50%)',
            width: 28, height: 28, display: 'flex', alignItems: 'center', justifyContent: 'center',
            opacity: 1, zIndex: 1,
          }}
        >
          <IconButton
            size="small"
            onClick={(e) => { e.stopPropagation(); onAddAnotherShift(shift.userId, parseISO(shift.day)); }}
            sx={{ p: 0.3, bgcolor: 'rgba(230,230,230,0.85)', '&:hover': { bgcolor: 'rgba(210,210,210,1)' }, borderRadius: '4px', width: '100%', height: '100%'}}
            title="Thêm ca làm việc khác vào ngày này"
          >
            <AddIcon sx={{ fontSize: '1rem' }} color="action" />
          </IconButton>
        </Box>
      )}
    </Paper>
  );
}