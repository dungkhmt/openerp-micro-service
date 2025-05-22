// ==============
// DayCell.jsx
// ==============
import {format} from "date-fns";
import {Draggable, Droppable} from "react-beautiful-dnd";
import {Grid} from "@mui/material";
import React from "react";
import EmptyShiftSlot from "./EmptyShiftSlot.jsx";
import ShiftCard from "./ShiftCard.jsx";
import { FRONTEND_UNASSIGNED_SHIFT_USER_ID } from "./ShiftScheduler.jsx"; // Import for logging

export default function DayCell({
                                  userId,
                                  day,
                                  shiftsInCell,
                                  onAddShift, // (userId, day) => handleOpenModal(userId, day) OR (UNASSIGNED_ID, day) => handleOpenModal(UNASSIGNED_ID, day)
                                  onDeleteShift,
                                  onEditShift, // (shift) => handleOpenModal(null, null, shift)
                                  selectedShiftIds,
                                  onToggleSelectShift, // (shiftId) => setSelectedShiftIds(...)
                                  isAnyShiftSelected
                                }) {
  const droppableId = `user-${userId}-day-${format(day, 'yyyy-MM-dd')}`;

  return (
    <Droppable droppableId={droppableId} type="SHIFT">
      {(provided, snapshot) => (
        <Grid
          item
          xs
          ref={provided.innerRef}
          {...provided.droppableProps}
          sx={{
            p: 0.5,
            borderRight: 1,
            borderColor: 'divider',
            '&:last-child': { borderRight: 0 },
            minHeight: 60,
            display: 'flex',
            flexDirection: 'column',
            bgcolor: snapshot.isDraggingOver ? 'action.focus' : 'transparent',
            transition: 'background-color 0.2s ease',
            overflow: 'hidden',
          }}
        >
          {shiftsInCell.length > 0 ? (
            shiftsInCell.map((shift, index) => (
              <Draggable
                key={shift.id}
                draggableId={shift.id}
                index={index}
                isDragDisabled={shift.type === 'time_off'} // Disable dragging for time_off shifts
              >
                {(providedDraggable, snapshotDraggable) => (
                  <ShiftCard
                    shift={shift}
                    shiftType={shift.type || (shift.userId === FRONTEND_UNASSIGNED_SHIFT_USER_ID ? 'unassigned' : 'regular')} // Pass shift type
                    onEditShift={onEditShift} // Passed to ShiftCard
                    // onAddAnotherShift is for the "+" button on a user's REGULAR or TIME_OFF shift card
                    // It should trigger adding a NEW REGULAR shift for that user/day.
                    // `onAddShift` (from DayCell props) is (userId, day) => handleOpenModal(userId, day)
                    onAddAnotherShift={userId !== FRONTEND_UNASSIGNED_SHIFT_USER_ID ? () => onAddShift(userId, day) : undefined}
                    // onAddShift (new name) is for the "+" button on an UNASSIGNED shift card
                    // It should trigger adding a NEW UNASSIGNED shift template for that day.
                    // `onAddShift` (from DayCell props) is (UNASSIGNED_ID, day) => handleOpenModal(UNASSIGNED_ID, day)
                    onAddShiftToUnassignedDay={userId === FRONTEND_UNASSIGNED_SHIFT_USER_ID ? () => onAddShift(FRONTEND_UNASSIGNED_SHIFT_USER_ID, day) : undefined}
                    provided={providedDraggable}
                    snapshot={snapshotDraggable}
                    isSelected={selectedShiftIds.includes(shift.id)}
                    onToggleSelect={onToggleSelectShift} // Passed to ShiftCard
                    isAnyShiftSelected={isAnyShiftSelected}
                  />
                )}
              </Draggable>
            ))
          ) : (
            !snapshot.isDraggingOver && <EmptyShiftSlot onAdd={() => onAddShift(userId, day)} />
          )}
          {provided.placeholder}
        </Grid>
      )}
    </Droppable>
  );
}