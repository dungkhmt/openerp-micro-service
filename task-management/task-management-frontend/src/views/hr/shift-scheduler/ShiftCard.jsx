import React, {useState} from "react";
import {Box, Checkbox, Chip, IconButton, Paper, Typography} from "@mui/material";
import {alpha} from "@mui/material/styles";
import CheckBoxOutlineBlankIcon from "@mui/icons-material/CheckBoxOutlineBlank.js";
import CheckBoxIcon from "@mui/icons-material/CheckBox.js";
import {parseISO} from "date-fns";
import AddIcon from "@mui/icons-material/Add.js";
import {FRONTEND_UNASSIGNED_SHIFT_USER_ID} from "./ShiftScheduler.jsx";

const EMPLOYEE_SHIFT_TEXT_COLOR = '#673ab7';
const EMPLOYEE_SHIFT_BACKGROUND_COLOR_LIGHT = '#f3e5f5';

const TIME_OFF_CARD_BACKGROUND_COLOR = '#ef9a9a';
const TIME_OFF_CARD_BORDER_COLOR = '#e57373';
const TIME_OFF_CARD_TEXT_COLOR = '#000000';
const TIME_OFF_CARD_NOTE_TEXT_COLOR = 'rgba(0, 0, 0, 0.8)';


export default function ShiftCard({
                                    shift,
                                    shiftType,
                                    onEditShift,
                                    onAddAnotherShift,
                                    onAddShiftToUnassignedDay,
                                    provided,
                                    snapshot,
                                    isSelected,
                                    onToggleSelect,
                                    isAnyShiftSelected,
                                    canAdmin
                                  }) {
  const [isHovered, setIsHovered] = useState(false);

  const isUnassigned = shiftType === 'unassigned';
  const isTimeOff = shiftType === 'time_off';

  const handleCheckboxClick = (e) => {
    e.stopPropagation();
    if (!canAdmin || isTimeOff) return;
    onToggleSelect(shift.id);
  };

  const handleCardBodyClick = (e) => {
    if (e.defaultPrevented) return;
    if (e.target.closest('.selection-checkbox-area') ||
      e.target.closest('.add-action-button-area') ||
      e.target.closest('.add-unassigned-template-button-area')) return;

    if (!canAdmin || isTimeOff) {
      return;
    }

    if (!isAnyShiftSelected) {
      onEditShift(shift);
    } else {
      onToggleSelect(shift.id);
    }
  };

  const handleAddNewShiftFromCard = (e) => {
    e.stopPropagation();
    if (!canAdmin || !onAddAnotherShift) return;
    onAddAnotherShift(shift.userId, parseISO(shift.day));
  };

  const handleAddNewUnassignedFromCard = (e) => {
    e.stopPropagation();
    if (!canAdmin || !onAddShiftToUnassignedDay) return;
    onAddShiftToUnassignedDay(FRONTEND_UNASSIGNED_SHIFT_USER_ID, parseISO(shift.day));
  };

  const showCheckbox = canAdmin && !isTimeOff && (isHovered || isAnyShiftSelected) && !snapshot.isDragging;

  const showAddButtonForUserOrTimeOff =
    canAdmin &&
    (shiftType === 'regular' || isTimeOff) &&
    shift.userId && shift.userId !== FRONTEND_UNASSIGNED_SHIFT_USER_ID &&
    isHovered && !isAnyShiftSelected && !snapshot.isDragging;

  const showAddButtonForUnassignedTemplate =
    canAdmin &&
    isUnassigned && isHovered && !snapshot.isDragging;

  let cardSxProps = {
    my: 0.5, height: 50, minHeight: 50, boxSizing: 'border-box',
    position: 'relative', display: 'flex', alignItems: 'center',
    transition: 'background-color 0.2s ease, border 0.2s ease',
    width: '100%', maxWidth: '100%', overflow: 'hidden',
    cursor: (!canAdmin || isTimeOff) ? 'default' : (snapshot.isDragging ? 'grabbing' : 'grab'),
  };

  let currentShiftTimeTextColor = isUnassigned ? (shift.muiTextColor || 'text.primary') : EMPLOYEE_SHIFT_TEXT_COLOR;
  let currentShiftNoteTextColor = isUnassigned
    ? (shift.muiTextColor ? shift.muiTextColor : 'text.secondary')
    : alpha(EMPLOYEE_SHIFT_TEXT_COLOR, 0.85);

  if (isTimeOff) {
    cardSxProps.bgcolor = TIME_OFF_CARD_BACKGROUND_COLOR;
    cardSxProps.border = `1px solid ${TIME_OFF_CARD_BORDER_COLOR}`;
    cardSxProps.cursor = 'default';
    currentShiftTimeTextColor = TIME_OFF_CARD_TEXT_COLOR;
    currentShiftNoteTextColor = TIME_OFF_CARD_NOTE_TEXT_COLOR;
  } else if (!canAdmin) {
    cardSxProps.bgcolor = isUnassigned ? 'grey.200' : EMPLOYEE_SHIFT_BACKGROUND_COLOR_LIGHT;
    cardSxProps.border = `1px dashed ${alpha(isUnassigned ? '#e0e0e0' : EMPLOYEE_SHIFT_TEXT_COLOR, 0.3)}`;
    cardSxProps.cursor = 'default';
  } else if (snapshot.isDragging) {
    cardSxProps.bgcolor = 'primary.lighter';
    cardSxProps.cursor = 'grabbing';
  } else if (isSelected) {
    cardSxProps.bgcolor = (theme) => alpha(isUnassigned ? theme.palette.grey[400] : theme.palette.primary.main, 0.25);
    cardSxProps.border = (theme) => `2px solid ${isUnassigned ? theme.palette.grey[600] : theme.palette.primary.main}`;
  } else {
    cardSxProps.bgcolor = isUnassigned ? 'grey.200' : EMPLOYEE_SHIFT_BACKGROUND_COLOR_LIGHT;
    cardSxProps.border = `1px dashed ${alpha(isUnassigned ? '#bdbdbd' : EMPLOYEE_SHIFT_TEXT_COLOR, 0.5)}`;
  }

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
      {...((!canAdmin || isTimeOff) ? {} : provided.draggableProps)}
      onMouseEnter={() => setIsHovered(true)}
      onMouseLeave={() => setIsHovered(false)}
      elevation={snapshot.isDragging && canAdmin ? 4 : (isSelected && canAdmin && !isTimeOff ? 3 : 1)}
      sx={cardSxProps}
    >
      <Box
        className="selection-checkbox-area"
        sx={{
          display: 'flex', alignItems: 'center', justifyContent: 'center',
          width: showCheckbox ? 32 : 0,
          flexShrink: 0, height: '100%',
          transition: 'width 0.15s ease-in-out', overflow: 'hidden', zIndex: 1
        }}
      >
        {showCheckbox && (
          <Checkbox
            size="small"
            checked={isSelected}
            onChange={handleCheckboxClick}
            onClick={(e) => e.stopPropagation()}
            icon={<CheckBoxOutlineBlankIcon fontSize="small" />}
            checkedIcon={<CheckBoxIcon fontSize="small" />}
            sx={{
              p: 0.5, opacity: 1, visibility: 'visible',
            }}
            tabIndex={0}
          />
        )}
      </Box>

      <Box
        {...((!canAdmin || isTimeOff) ? {} : provided.dragHandleProps)}
        onClick={handleCardBodyClick}
        sx={{
          flexGrow: 1, minWidth: 0, height: '100%',
          display: 'flex', flexDirection: 'column', justifyContent: 'center',
          pl: showCheckbox ? 0.5 : ((isUnassigned || isTimeOff || !canAdmin) ? 1.25 : 1.5),
          pr: contentPaddingRight,
          cursor: (!canAdmin || isTimeOff) ? 'default' : (snapshot.isDragging ? 'grabbing' : 'grab'),
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

      {isUnassigned && typeof shift.slots === 'number' && (
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

      {showAddButtonForUnassignedTemplate && (
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

      {showAddButtonForUserOrTimeOff && (
        <Box
          className="add-action-button-area"
          sx={{ position: 'absolute', top: '50%', right: ACTION_BUTTON_RIGHT_MARGIN, transform: 'translateY(-50%)',
            width: ACTION_BUTTON_SIZE, height: ACTION_BUTTON_SIZE, display: 'flex', alignItems: 'center', justifyContent: 'center',
            opacity: 1, zIndex: 1,
          }}
        >
          <IconButton
            size="small" onClick={handleAddNewShiftFromCard}
            sx={{ p: 0.3, bgcolor: 'rgba(230,230,230,0.85)', '&:hover': { bgcolor: 'rgba(210,210,210,1)' }, borderRadius: '4px', width: '100%', height: '100%'}}
            title="Thêm ca làm việc khác vào ngày này"
          > <AddIcon sx={{ fontSize: '1rem' }} color="action" />
          </IconButton>
        </Box>
      )}
    </Paper>
  );
}