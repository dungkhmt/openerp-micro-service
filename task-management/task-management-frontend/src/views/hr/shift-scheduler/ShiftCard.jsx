// ==============
// ShiftCard.jsx
// ==============
import React, {useState} from "react";
import {Box, Checkbox, IconButton, Paper, Typography, Chip} from "@mui/material";
import {alpha} from "@mui/material/styles";
import CheckBoxOutlineBlankIcon from "@mui/icons-material/CheckBoxOutlineBlank.js";
import CheckBoxIcon from "@mui/icons-material/CheckBox.js";
import {parseISO} from "date-fns";
import AddIcon from "@mui/icons-material/Add.js"; // Icon for adding
// Removed ControlPointIcon as we'll use AddIcon for unassigned too
import { UNASSIGNED_SHIFT_USER_ID } from "./ShiftScheduler.jsx";

const EMPLOYEE_SHIFT_TEXT_COLOR = '#673ab7';
const EMPLOYEE_SHIFT_BACKGROUND_COLOR_LIGHT = '#f3e5f5';

export default function ShiftCard({
                                    shift,
                                    onEditShift,
                                    onAddAnotherShift,
                                    onAddShift, // For the new "+" on unassigned cards
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
      e.target.closest('.add-action-button-area') || // For employee shifts
      e.target.closest('.add-unassigned-template-button-area')) return; // For unassigned shifts

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
  const isUnassigned = shift.userId === UNASSIGNED_SHIFT_USER_ID;

  // Visibility for the "+" button on employee shift cards
  const showAddButtonForUserShift = isHovered && !isAnyShiftSelected && !snapshot.isDragging && !isUnassigned;
  // Visibility for the new "+" button on unassigned shift cards
  const showAddButtonForUnassignedTemplate = isUnassigned && isHovered && !snapshot.isDragging;

  let backgroundColor = isUnassigned ? 'grey.200' : EMPLOYEE_SHIFT_BACKGROUND_COLOR_LIGHT;
  if (snapshot.isDragging) {
    backgroundColor = 'primary.lighter';
  } else if (isSelected) {
    backgroundColor = (theme) => alpha(isUnassigned ? theme.palette.grey[400] : theme.palette.primary.main, 0.25);
  }

  const timeTextColor = isUnassigned
    ? (shift.muiTextColor || 'text.primary')
    : EMPLOYEE_SHIFT_TEXT_COLOR;
  const noteTextColor = isUnassigned
    ? (shift.muiTextColor ? shift.muiTextColor : 'text.secondary')
    : alpha(EMPLOYEE_SHIFT_TEXT_COLOR, 0.85);

  // Define constants for button and chip sizes/positions for easier management
  const ACTION_BUTTON_SIZE = 28; //px
  const ACTION_BUTTON_RIGHT_MARGIN = '4px';
  const SLOT_CHIP_DEFAULT_RIGHT_MARGIN = '8px';
  const SLOT_CHIP_SHIFTED_RIGHT_MARGIN = `calc(${ACTION_BUTTON_RIGHT_MARGIN} + ${ACTION_BUTTON_SIZE}px + 4px)`; // chip right = button right + button width + gap
  const SLOT_CHIP_WIDTH_APPROX = 24; // Approximate width of the slot chip for padding calculation

  // Calculate right padding for the main content text area
  let contentPaddingRight = isUnassigned ? 1.25 : 1.5; // Default
  if (showAddButtonForUserShift || showAddButtonForUnassignedTemplate) {
    contentPaddingRight = (ACTION_BUTTON_SIZE + 8) / 8; // approx 4.5, space for one button
  }
  if (showAddButtonForUnassignedTemplate && typeof shift.slots === 'number') {
    // If add button AND slot chip are visible (slot chip shifted)
    contentPaddingRight = (ACTION_BUTTON_SIZE + 8 + SLOT_CHIP_WIDTH_APPROX + 4) / 8; // approx 7.5-8, space for shifted chip + button
  } else if (isUnassigned && typeof shift.slots === 'number' && !showAddButtonForUnassignedTemplate) {
    // Only slot chip visible
    contentPaddingRight = (SLOT_CHIP_WIDTH_APPROX + 12) / 8; // approx 4.0-4.5
  }


  return (
    <Paper
      ref={provided.innerRef}
      {...provided.draggableProps}
      onMouseEnter={() => setIsHovered(true)}
      onMouseLeave={() => setIsHovered(false)}
      elevation={snapshot.isDragging ? 4 : (isSelected ? 3 : 1)}
      sx={{
        my: 0.5, height: 50, minHeight: 50, boxSizing: 'border-box',
        bgcolor: backgroundColor,
        border: isSelected
          ? (theme) => `2px solid ${isUnassigned ? theme.palette.grey[600] : theme.palette.primary.main}`
          : `1px dashed ${alpha(isUnassigned ? '#bdbdbd' : EMPLOYEE_SHIFT_TEXT_COLOR, 0.5)}`,
        position: 'relative', display: 'flex', alignItems: 'center',
        transition: 'all 0.2s ease', width: '100%', maxWidth: '100%', overflow: 'hidden',
      }}
    >
      <Box /* Checkbox Area */
        className="selection-checkbox-area"
        sx={{ display: 'flex', alignItems: 'center', justifyContent: 'center', width: showCheckbox ? 32 : 0,
          flexShrink: 0, height: '100%', transition: 'width 0.15s ease-in-out', overflow: 'hidden', zIndex: 1, }}
      >
        <Checkbox size="small" checked={isSelected} onChange={handleCheckboxClick} onClick={(e) => e.stopPropagation()}
                  icon={<CheckBoxOutlineBlankIcon fontSize="small" />} checkedIcon={<CheckBoxIcon fontSize="small" />}
                  sx={{ p: 0.5, opacity: showCheckbox ? 1 : 0, visibility: showCheckbox ? 'visible' : 'hidden', transition: 'opacity 0.1s ease-in-out, visibility 0.1s ease-in-out' }}
                  tabIndex={showCheckbox ? 0 : -1} />
      </Box>

      <Box /* Main content area & Drag Handle */
        {...provided.dragHandleProps}
        onClick={handleCardBodyClick}
        sx={{
          flexGrow: 1, minWidth: 0, height: '100%',
          display: 'flex', flexDirection: 'column', justifyContent: 'center',
          pl: showCheckbox ? 0.5 : (isUnassigned ? 1.25 : 1.5),
          pr: contentPaddingRight,
          cursor: snapshot.isDragging ? 'grabbing' : 'grab',
          overflow: 'hidden', userSelect: 'none',
          transition: 'padding-left 0.15s ease-in-out, padding-right 0.15s ease-in-out',
        }}
      >
        <Typography variant="caption" component="div"
                    sx={{ fontWeight: 'bold', color: timeTextColor, fontSize: '0.68rem', lineHeight: 1.3,
                      whiteSpace: 'nowrap', overflow: 'hidden', textOverflow: 'ellipsis' }} >
          {`${shift.startTime} - ${shift.endTime}`}
          <Typography component="span" variant="caption"
                      sx={{ ml: 0.5, color: timeTextColor, opacity: 0.8, fontSize: '0.65rem' }}>
            ({shift.duration})
          </Typography>
        </Typography>

        {shift.note && shift.note.trim() !== '' && (
          <Typography variant="body2"
                      sx={{ fontSize: '0.65rem', color: noteTextColor,
                        opacity: isUnassigned ? (shift.muiTextColor ? 0.85 : 1.0) : 1.0,
                        lineHeight: 1.3, whiteSpace: 'nowrap', overflow: 'hidden', textOverflow: 'ellipsis' }}
                      title={shift.note} >
            {shift.note}
          </Typography>
        )}
      </Box>

      {/* Slot Count Badge for Unassigned Shifts (more rectangular) */}
      {isUnassigned && typeof shift.slots === 'number' && (
        <Chip
          label={shift.slots}
          size="small"
          sx={{
            position: 'absolute',
            top: '50%',
            right: showAddButtonForUnassignedTemplate ? SLOT_CHIP_SHIFTED_RIGHT_MARGIN : SLOT_CHIP_DEFAULT_RIGHT_MARGIN,
            transform: 'translateY(-50%)',
            height: '20px', // Slightly taller for rectangular feel
            minWidth: '20px', // Ensure it can be a square
            fontSize: '0.7rem',
            lineHeight: '20px', // Match height
            padding: '0 6px', // Adjust padding for content
            borderRadius: '4px', // More rectangular
            backgroundColor: alpha('#878787', 0.75), // Neutral grey, adjust as needed
            color: 'white',
            zIndex: 1,
            transition: 'right 0.15s ease-in-out', // Animate right position change
          }}
        />
      )}

      {/* New "+" button for Unassigned Shift Cards, styled like user shift add button */}
      {showAddButtonForUnassignedTemplate && (
        <Box
          className="add-unassigned-template-button-area" // Changed class name for clarity
          sx={{
            position: 'absolute', top: '50%', right: ACTION_BUTTON_RIGHT_MARGIN, transform: 'translateY(-50%)',
            width: ACTION_BUTTON_SIZE, height: ACTION_BUTTON_SIZE,
            display: 'flex', alignItems: 'center', justifyContent: 'center',
            zIndex: 2,
          }}
        >
          <IconButton
            size="small"
            onClick={handleAddNewUnassignedFromCard}
            sx={{
              p: 0.3,
              bgcolor: 'rgba(230,230,230,0.85)', // Match user shift add button
              '&:hover': { bgcolor: 'rgba(210,210,210,1)' }, // Match user shift add button
              borderRadius: '4px', width: '100%', height: '100%'
            }}
            title="Thêm ca chờ gán mới vào ngày này"
          >
            <AddIcon sx={{ fontSize: '1rem' }} color="action" /> {/* Match user shift add button icon */}
          </IconButton>
        </Box>
      )}

      {/* "Add another shift" button for REGULAR user shifts */}
      {showAddButtonForUserShift && (
        <Box
          className="add-action-button-area"
          sx={{ position: 'absolute', top: '50%', right: ACTION_BUTTON_RIGHT_MARGIN, transform: 'translateY(-50%)',
            width: ACTION_BUTTON_SIZE, height: ACTION_BUTTON_SIZE, display: 'flex', alignItems: 'center', justifyContent: 'center',
            opacity: 1, zIndex: 1, }}
        >
          <IconButton
            size="small" onClick={(e) => { e.stopPropagation(); onAddAnotherShift(shift.userId, parseISO(shift.day)); }}
            sx={{ p: 0.3, bgcolor: 'rgba(230,230,230,0.85)', '&:hover': { bgcolor: 'rgba(210,210,210,1)' }, borderRadius: '4px', width: '100%', height: '100%'}}
            title="Thêm ca làm việc khác vào ngày này"
          > <AddIcon sx={{ fontSize: '1rem' }} color="action" />
          </IconButton>
        </Box>
      )}
    </Paper>
  );
}
