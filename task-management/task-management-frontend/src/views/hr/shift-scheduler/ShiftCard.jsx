// ==============
// ShiftCard.jsx
// ==============
import React, {useState} from "react";
import {Box, Checkbox, IconButton, Paper, Typography, Chip} from "@mui/material";
import {alpha} from "@mui/material/styles";
import CheckBoxOutlineBlankIcon from "@mui/icons-material/CheckBoxOutlineBlank.js";
import CheckBoxIcon from "@mui/icons-material/CheckBox.js";
import {parseISO} from "date-fns";
import AddIcon from "@mui/icons-material/Add.js";
import { UNASSIGNED_SHIFT_USER_ID } from "./ShiftScheduler.jsx";

const EMPLOYEE_SHIFT_TEXT_COLOR = '#673ab7';
const EMPLOYEE_SHIFT_BACKGROUND_COLOR_LIGHT = '#f3e5f5';

// Define styles for time_off cards
const TIME_OFF_CARD_BACKGROUND_COLOR = 'grey.200'; // Softer grey
const TIME_OFF_CARD_BORDER_COLOR = 'grey.400'; //
const TIME_OFF_CARD_TEXT_COLOR = 'text.secondary'; //
const TIME_OFF_CARD_NOTE_TEXT_COLOR = 'text.disabled'; //


export default function ShiftCard({
                                    shift,
                                    shiftType, // 'regular', 'unassigned', 'time_off'
                                    onEditShift,
                                    onAddAnotherShift, // For "+" on regular user shifts AND time_off shifts
                                    onAddShiftToUnassignedDay, // For "+" on unassigned shifts
                                    provided,
                                    snapshot,
                                    isSelected,
                                    onToggleSelect,
                                    isAnyShiftSelected
                                  }) {
  const [isHovered, setIsHovered] = useState(false);

  const isUnassigned = shiftType === 'unassigned';
  const isTimeOff = shiftType === 'time_off'; //

  const handleCheckboxClick = (e) => {
    e.stopPropagation();
    if (isTimeOff) return; // Time off cards are not selectable
    onToggleSelect(shift.id);
  };

  const handleCardBodyClick = (e) => {
    if (e.defaultPrevented) return;
    // Prevent action if clicking on interactive sub-elements
    if (e.target.closest('.selection-checkbox-area') ||
      e.target.closest('.add-action-button-area') ||
      e.target.closest('.add-unassigned-template-button-area')) return;

    if (isTimeOff) { // Time_off cards: no edit, no selection via body click
      return;
    }

    if (!isAnyShiftSelected) {
      onEditShift(shift); // Edit only if no other shifts are selected
    } else {
      // If other shifts are selected, clicking this card's body should toggle its selection
      // (unless it's a time_off card, which is handled above)
      onToggleSelect(shift.id);
    }
  };

  const handleAddNewShiftFromCard = (e) => { // For "+" on regular user or time_off cards
    e.stopPropagation();
    if (onAddAnotherShift) {
      onAddAnotherShift(shift.userId, parseISO(shift.day)); // Will open modal for a new REGULAR shift
    }
  };

  const handleAddNewUnassignedFromCard = (e) => { // For "+" on unassigned cards
    e.stopPropagation();
    if (onAddShiftToUnassignedDay) {
      onAddShiftToUnassignedDay(UNASSIGNED_SHIFT_USER_ID, parseISO(shift.day));
    }
  };


  // Checkbox visibility: not for time_off, not while dragging. Otherwise, on hover or if any shift is selected.
  const showCheckbox = !isTimeOff && (isHovered || isAnyShiftSelected) && !snapshot.isDragging; //

  // Visibility for the "+" button on employee shift cards or time_off cards
  const showAddButtonForUserOrTimeOff =
    (shiftType === 'regular' || isTimeOff) && // Only on regular or time_off cards
    shift.userId && shift.userId !== UNASSIGNED_SHIFT_USER_ID && // Must have a valid user ID
    isHovered && !isAnyShiftSelected && !snapshot.isDragging;

  // Visibility for the new "+" button on unassigned shift cards
  const showAddButtonForUnassignedTemplate =
    isUnassigned && isHovered && !snapshot.isDragging;


  // --- Determine Card Styling ---
  let cardSxProps = {
    my: 0.5, height: 50, minHeight: 50, boxSizing: 'border-box',
    position: 'relative', display: 'flex', alignItems: 'center',
    transition: 'background-color 0.2s ease, border 0.2s ease',
    width: '100%', maxWidth: '100%', overflow: 'hidden',
    cursor: 'grab', // Default cursor
  };

  let currentShiftTimeTextColor = isUnassigned ? (shift.muiTextColor || 'text.primary') : EMPLOYEE_SHIFT_TEXT_COLOR;
  let currentShiftNoteTextColor = isUnassigned
    ? (shift.muiTextColor ? shift.muiTextColor : 'text.secondary')
    : alpha(EMPLOYEE_SHIFT_TEXT_COLOR, 0.85);

  if (isTimeOff) {
    cardSxProps.bgcolor = TIME_OFF_CARD_BACKGROUND_COLOR;
    cardSxProps.border = `1px solid ${TIME_OFF_CARD_BORDER_COLOR}`;
    cardSxProps.cursor = 'default'; // Not draggable, not interactive for edit/select
    currentShiftTimeTextColor = TIME_OFF_CARD_TEXT_COLOR;
    currentShiftNoteTextColor = TIME_OFF_CARD_NOTE_TEXT_COLOR;
  } else if (snapshot.isDragging) {
    cardSxProps.bgcolor = 'primary.lighter';
    cardSxProps.cursor = 'grabbing';
  } else if (isSelected) { // isSelected is false for time_off due to logic in handleCheckboxClick and handleCardBodyClick
    cardSxProps.bgcolor = (theme) => alpha(isUnassigned ? theme.palette.grey[400] : theme.palette.primary.main, 0.25);
    cardSxProps.border = (theme) => `2px solid ${isUnassigned ? theme.palette.grey[600] : theme.palette.primary.main}`;
  } else {
    cardSxProps.bgcolor = isUnassigned ? 'grey.200' : EMPLOYEE_SHIFT_BACKGROUND_COLOR_LIGHT;
    cardSxProps.border = `1px dashed ${alpha(isUnassigned ? '#bdbdbd' : EMPLOYEE_SHIFT_TEXT_COLOR, 0.5)}`;
  }
  // --- End Card Styling ---


  const ACTION_BUTTON_SIZE = 28;
  const ACTION_BUTTON_RIGHT_MARGIN = '4px';
  const SLOT_CHIP_DEFAULT_RIGHT_MARGIN = '8px';
  const SLOT_CHIP_SHIFTED_RIGHT_MARGIN = `calc(${ACTION_BUTTON_RIGHT_MARGIN} + ${ACTION_BUTTON_SIZE}px + 4px)`;
  const SLOT_CHIP_WIDTH_APPROX = 24;

  let contentPaddingRight = (isUnassigned || isTimeOff) ? 1.25 : 1.5;
  if (showAddButtonForUserOrTimeOff || showAddButtonForUnassignedTemplate) {
    contentPaddingRight = (ACTION_BUTTON_SIZE + 8) / 8;
  }
  if (showAddButtonForUnassignedTemplate && typeof shift.slots === 'number') {
    contentPaddingRight = (ACTION_BUTTON_SIZE + 8 + SLOT_CHIP_WIDTH_APPROX + 4) / 8;
  } else if (isUnassigned && typeof shift.slots === 'number' && !showAddButtonForUnassignedTemplate) {
    contentPaddingRight = (SLOT_CHIP_WIDTH_APPROX + 12) / 8;
  }


  return (
    <Paper
      ref={provided.innerRef}
      {...(isTimeOff ? {} : provided.draggableProps)} // Draggable props only if not time_off
      onMouseEnter={() => setIsHovered(true)}
      onMouseLeave={() => setIsHovered(false)}
      elevation={snapshot.isDragging ? 4 : (isSelected && !isTimeOff ? 3 : 1)}
      sx={cardSxProps}
    >
      <Box /* Checkbox Area */
        className="selection-checkbox-area"
        sx={{
          display: 'flex', alignItems: 'center', justifyContent: 'center',
          width: showCheckbox ? 32 : 0, // Hidden if !showCheckbox (e.g. for time_off)
          flexShrink: 0, height: '100%',
          transition: 'width 0.15s ease-in-out', overflow: 'hidden', zIndex: 1
        }}
      >
        {showCheckbox && ( // Conditionally render checkbox itself
          <Checkbox
            size="small"
            checked={isSelected}
            onChange={handleCheckboxClick}
            onClick={(e) => e.stopPropagation()}
            icon={<CheckBoxOutlineBlankIcon fontSize="small" />}
            checkedIcon={<CheckBoxIcon fontSize="small" />}
            sx={{
              p: 0.5, opacity: 1, visibility: 'visible', // Simpler opacity/visibility
            }}
            tabIndex={0}
          />
        )}
      </Box>

      <Box /* Main content area & Drag Handle */
        {...(isTimeOff ? {} : provided.dragHandleProps)} // Drag handle props only if not time_off
        onClick={handleCardBodyClick}
        sx={{
          flexGrow: 1, minWidth: 0, height: '100%',
          display: 'flex', flexDirection: 'column', justifyContent: 'center',
          // Adjust left padding based on checkbox visibility
          pl: showCheckbox ? 0.5 : ((isUnassigned || isTimeOff) ? 1.25 : 1.5),
          pr: contentPaddingRight,
          cursor: isTimeOff ? 'default' : (snapshot.isDragging ? 'grabbing' : 'grab'), // Cursor based on type/state
          overflow: 'hidden', userSelect: 'none',
          transition: 'padding-left 0.15s ease-in-out, padding-right 0.15s ease-in-out',
        }}
      >
        <Typography variant="caption" component="div"
                    sx={{ fontWeight: 'bold', color: currentShiftTimeTextColor, fontSize: '0.68rem', lineHeight: 1.3,
                      whiteSpace: 'nowrap', overflow: 'hidden', textOverflow: 'ellipsis' }} >
          {`${shift.startTime} - ${shift.endTime}`}
          <Typography component="span" variant="caption"
                      sx={{ ml: 0.5, color: currentShiftTimeTextColor, opacity: 0.8, fontSize: '0.65rem' }}>
            ({shift.duration})
          </Typography>
        </Typography>

        {shift.note && shift.note.trim() !== '' && (
          <Typography variant="body2"
                      sx={{ fontSize: '0.65rem', color: currentShiftNoteTextColor,
                        opacity: (isUnassigned && !isTimeOff) ? (shift.muiTextColor ? 0.85 : 1.0) : 1.0,
                        lineHeight: 1.3, whiteSpace: 'nowrap', overflow: 'hidden', textOverflow: 'ellipsis' }}
                      title={shift.note} >
            {shift.note}
          </Typography>
        )}
      </Box>

      {/* Slot Count Badge for Unassigned Shifts */}
      {isUnassigned && typeof shift.slots === 'number' && ( // Only for unassigned
        <Chip
          label={shift.slots}
          size="small"
          sx={{
            position: 'absolute',
            top: '50%',
            right: showAddButtonForUnassignedTemplate ? SLOT_CHIP_SHIFTED_RIGHT_MARGIN : SLOT_CHIP_DEFAULT_RIGHT_MARGIN,
            transform: 'translateY(-50%)',
            height: '20px',
            minWidth: '20px',
            fontSize: '0.7rem',
            lineHeight: '20px',
            padding: '0 6px',
            borderRadius: '4px',
            backgroundColor: alpha('#878787', 0.75),
            color: 'white',
            zIndex: 1,
            transition: 'right 0.15s ease-in-out',
          }}
        />
      )}

      {/* "+" button for Unassigned Shift Cards */}
      {showAddButtonForUnassignedTemplate && ( // Only for unassigned
        <Box
          className="add-unassigned-template-button-area"
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
              bgcolor: 'rgba(230,230,230,0.85)',
              '&:hover': { bgcolor: 'rgba(210,210,210,1)' },
              borderRadius: '4px', width: '100%', height: '100%'
            }}
            title="Thêm ca chờ gán mới vào ngày này"
          >
            <AddIcon sx={{ fontSize: '1rem' }} color="action" />
          </IconButton>
        </Box>
      )}

      {/* "+" button for REGULAR user shifts OR TIME_OFF shifts */}
      {showAddButtonForUserOrTimeOff && ( // For regular user or time_off shifts
        <Box
          className="add-action-button-area"
          sx={{ position: 'absolute', top: '50%', right: ACTION_BUTTON_RIGHT_MARGIN, transform: 'translateY(-50%)',
            width: ACTION_BUTTON_SIZE, height: ACTION_BUTTON_SIZE, display: 'flex', alignItems: 'center', justifyContent: 'center',
            opacity: 1, zIndex: 1,
          }}
        >
          <IconButton
            size="small" onClick={handleAddNewShiftFromCard} // Updated handler
            sx={{ p: 0.3, bgcolor: 'rgba(230,230,230,0.85)', '&:hover': { bgcolor: 'rgba(210,210,210,1)' }, borderRadius: '4px', width: '100%', height: '100%'}}
            title="Thêm ca làm việc khác vào ngày này"
          > <AddIcon sx={{ fontSize: '1rem' }} color="action" />
          </IconButton>
        </Box>
      )}
    </Paper>
  );
}