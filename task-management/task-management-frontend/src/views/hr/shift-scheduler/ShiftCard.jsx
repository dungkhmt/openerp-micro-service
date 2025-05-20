import React, {useState} from "react";
import {Box, Checkbox, IconButton, Paper, Typography} from "@mui/material";
import {alpha} from "@mui/material/styles";
import CheckBoxOutlineBlankIcon from "@mui/icons-material/CheckBoxOutlineBlank.js";
import CheckBoxIcon from "@mui/icons-material/CheckBox.js";
import {parseISO} from "date-fns";
import AddIcon from "@mui/icons-material/Add.js";

export default function ShiftCard({
                     shift,
                     onDeleteShift,
                     onEditShift,
                     onAddAnotherShift,
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
    if (e.defaultPrevented) {
      return;
    }
    if (e.target.closest('.selection-checkbox-area') || e.target.closest('.add-action-button-area')) {
      return;
    }

    if (!isAnyShiftSelected) {
      onEditShift(shift);
    } else {
      onToggleSelect(shift.id);
    }
  };

  const showCheckbox = (isHovered || isAnyShiftSelected) && !snapshot.isDragging;
  const showAddButtonOnly = isHovered && !isAnyShiftSelected && !snapshot.isDragging;

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
        boxSizing: 'border-box',
        bgcolor: snapshot.isDragging
          ? 'primary.lighter'
          : (isSelected ? (theme) => alpha(theme.palette.primary.main, 0.12) : 'background.paper'),
        border: isSelected
          ? (theme) => `2px solid ${theme.palette.primary.main}`
          : (theme) => `1px dashed ${alpha(theme.palette.primary.main, 0.5)}`,
        position: 'relative',
        display: 'flex',
        alignItems: 'center',
        transition: 'background-color 0.2s ease, border-color 0.2s ease, box-shadow 0.2s ease',
        width: '100%',
        maxWidth: '100%',
        overflow: 'hidden',
      }}
    >
      <Box
        className="selection-checkbox-area"
        sx={{
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
          width: showCheckbox ? 32 : 0,
          flexShrink: 0,
          height: '100%',
          transition: 'width 0.15s ease-in-out',
          overflow: 'hidden',
        }}
      >
        <Checkbox
          size="small"
          checked={isSelected}
          onChange={handleCheckboxClick}
          onClick={(e) => e.stopPropagation()}
          icon={<CheckBoxOutlineBlankIcon fontSize="small" />}
          checkedIcon={<CheckBoxIcon fontSize="small" />}
          sx={{
            p: 0.5,
            opacity: showCheckbox ? 1 : 0,
            visibility: showCheckbox ? 'visible' : 'hidden',
            transition: 'opacity 0.1s ease-in-out, visibility 0.1s ease-in-out',
          }}
          tabIndex={showCheckbox ? 0 : -1}
        />
      </Box>

      <Box
        {...provided.dragHandleProps}
        onClick={handleCardBodyClick}
        sx={{
          flexGrow: 1,
          minWidth: 0,
          height: '100%',
          display: 'flex',
          flexDirection: 'column',
          justifyContent: 'center',
          pl: showCheckbox ? 0.5 : 1.5,
          pr: showAddButtonOnly ? 4.5 : 1.5,
          cursor: snapshot.isDragging ? 'grabbing' : (isAnyShiftSelected ? 'pointer' : 'grab'),
          overflow: 'hidden',
          whiteSpace: 'nowrap',
          userSelect: 'none',
          transition: 'padding-left 0.15s ease-in-out, padding-right 0.15s ease-in-out',
        }}
      >
        <Typography
          variant="caption"
          component="div"
          sx={{
            fontWeight: 'bold',
            color: shift.muiTextColor || 'text.primary',
            fontSize: '0.68rem',
            lineHeight: 1.3,
            whiteSpace: 'nowrap',
            overflow: 'hidden',
            textOverflow: 'ellipsis'
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
              fontSize: '0.65rem',
              color: shift.muiTextColor ? shift.muiTextColor : 'text.secondary',
              opacity: shift.muiTextColor ? 0.85 : 1.0,
              lineHeight: 1.3,
              whiteSpace: 'nowrap',
              overflow: 'hidden',
              textOverflow: 'ellipsis',
            }}
            title={shift.note}
          >
            {shift.note}
          </Typography>
        )}
      </Box>

      <Box
        className="add-action-button-area"
        sx={{
          position: 'absolute',
          top: '50%',
          right: '4px',
          transform: 'translateY(-50%)',
          width: 28,
          height: 28,
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
          opacity: showAddButtonOnly ? 1 : 0,
          visibility: showAddButtonOnly ? 'visible' : 'hidden',
          transition: 'opacity 0.15s ease-in-out, visibility 0.15s ease-in-out',
          zIndex: 1,
          pointerEvents: showAddButtonOnly ? 'auto' : 'none',
        }}
      >
        <IconButton
          size="small"
          onClick={(e) => {function ShiftCard({
                                                shift,
                                                onDeleteShift,
                                                onEditShift,
                                                onAddAnotherShift,
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
              if (e.defaultPrevented) {
                return;
              }
              if (e.target.closest('.selection-checkbox-area') || e.target.closest('.add-action-button-area')) {
                return;
              }

              if (!isAnyShiftSelected) {
                onEditShift(shift);
              } else {
                onToggleSelect(shift.id);
              }
            };

            const showCheckbox = (isHovered || isAnyShiftSelected) && !snapshot.isDragging;
            const showAddButtonOnly = isHovered && !isAnyShiftSelected && !snapshot.isDragging;

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
                  boxSizing: 'border-box',
                  bgcolor: snapshot.isDragging
                    ? 'primary.lighter'
                    : (isSelected ? (theme) => alpha(theme.palette.primary.main, 0.12) : 'background.paper'),
                  border: isSelected
                    ? (theme) => `2px solid ${theme.palette.primary.main}`
                    : (theme) => `1px dashed ${alpha(theme.palette.primary.main, 0.5)}`,
                  position: 'relative',
                  display: 'flex',
                  alignItems: 'center',
                  transition: 'background-color 0.2s ease, border-color 0.2s ease, box-shadow 0.2s ease',
                  width: '100%',
                  maxWidth: '100%',
                  overflow: 'hidden',
                }}
              >
                <Box
                  className="selection-checkbox-area"
                  sx={{
                    display: 'flex',
                    alignItems: 'center',
                    justifyContent: 'center',
                    width: showCheckbox ? 32 : 0,
                    flexShrink: 0,
                    height: '100%',
                    transition: 'width 0.15s ease-in-out',
                    overflow: 'hidden',
                  }}
                >
                  <Checkbox
                    size="small"
                    checked={isSelected}
                    onChange={handleCheckboxClick}
                    onClick={(e) => e.stopPropagation()}
                    icon={<CheckBoxOutlineBlankIcon fontSize="small" />}
                    checkedIcon={<CheckBoxIcon fontSize="small" />}
                    sx={{
                      p: 0.5,
                      opacity: showCheckbox ? 1 : 0,
                      visibility: showCheckbox ? 'visible' : 'hidden',
                      transition: 'opacity 0.1s ease-in-out, visibility 0.1s ease-in-out',
                    }}
                    tabIndex={showCheckbox ? 0 : -1}
                  />
                </Box>

                <Box
                  {...provided.dragHandleProps}
                  onClick={handleCardBodyClick}
                  sx={{
                    flexGrow: 1,
                    minWidth: 0,
                    height: '100%',
                    display: 'flex',
                    flexDirection: 'column',
                    justifyContent: 'center',
                    pl: showCheckbox ? 0.5 : 1.5,
                    pr: showAddButtonOnly ? 4.5 : 1.5,
                    cursor: snapshot.isDragging ? 'grabbing' : (isAnyShiftSelected ? 'pointer' : 'grab'),
                    overflow: 'hidden',
                    whiteSpace: 'nowrap',
                    userSelect: 'none',
                    transition: 'padding-left 0.15s ease-in-out, padding-right 0.15s ease-in-out',
                  }}
                >
                  <Typography
                    variant="caption"
                    component="div"
                    sx={{
                      fontWeight: 'bold',
                      color: shift.muiTextColor || 'text.primary',
                      fontSize: '0.68rem',
                      lineHeight: 1.3,
                      whiteSpace: 'nowrap',
                      overflow: 'hidden',
                      textOverflow: 'ellipsis'
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
                        fontSize: '0.65rem',
                        color: shift.muiTextColor ? shift.muiTextColor : 'text.secondary',
                        opacity: shift.muiTextColor ? 0.85 : 1.0,
                        lineHeight: 1.3,
                        whiteSpace: 'nowrap',
                        overflow: 'hidden',
                        textOverflow: 'ellipsis',
                      }}
                      title={shift.note}
                    >
                      {shift.note}
                    </Typography>
                  )}
                </Box>

                <Box
                  className="add-action-button-area"
                  sx={{
                    position: 'absolute',
                    top: '50%',
                    right: '4px',
                    transform: 'translateY(-50%)',
                    width: 28,
                    height: 28,
                    display: 'flex',
                    alignItems: 'center',
                    justifyContent: 'center',
                    opacity: showAddButtonOnly ? 1 : 0,
                    visibility: showAddButtonOnly ? 'visible' : 'hidden',
                    transition: 'opacity 0.15s ease-in-out, visibility 0.15s ease-in-out',
                    zIndex: 1,
                    pointerEvents: showAddButtonOnly ? 'auto' : 'none',
                  }}
                >
                  <IconButton
                    size="small"
                    onClick={(e) => {
                      e.stopPropagation();
                      onAddAnotherShift(shift.userId, parseISO(shift.day));
                    }}
                    sx={{
                      p: 0.3,
                      bgcolor: 'rgba(230,230,230,0.85)',
                      '&:hover': { bgcolor: 'rgba(210,210,210,1)' },
                      borderRadius: '4px',
                      width: '100%',
                      height: '100%',
                    }}
                    title="Thêm ca làm việc khác vào ngày này"
                  >
                    <AddIcon sx={{ fontSize: '1rem' }} color="action" />
                  </IconButton>
                </Box>
              </Paper>
            );
          }
            e.stopPropagation();
            onAddAnotherShift(shift.userId, parseISO(shift.day));
          }}
          sx={{
            p: 0.3,
            bgcolor: 'rgba(230,230,230,0.85)',
            '&:hover': { bgcolor: 'rgba(210,210,210,1)' },
            borderRadius: '4px',
            width: '100%',
            height: '100%',
          }}
          title="Thêm ca làm việc khác vào ngày này"
        >
          <AddIcon sx={{ fontSize: '1rem' }} color="action" />
        </IconButton>
      </Box>
    </Paper>
  );
}