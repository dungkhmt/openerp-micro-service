// ==============
// DayCell.jsx
// ==============
import {format} from "date-fns";
import {Draggable, Droppable} from "react-beautiful-dnd";
import {Grid} from "@mui/material";
import React from "react";
import EmptyShiftSlot from "./EmptyShiftSlot.jsx";
import ShiftCard from "./ShiftCard.jsx";
import { UNASSIGNED_SHIFT_USER_ID } from "./ShiftScheduler.jsx"; // Import for logging

export default function DayCell({
                                  userId,
                                  day,
                                  shiftsInCell,
                                  onAddShift,
                                  onDeleteShift,
                                  onEditShift,
                                  selectedShiftIds,
                                  onToggleSelectShift,
                                  isAnyShiftSelected
                                }) {
  const droppableId = `user-${userId}-day-${format(day, 'yyyy-MM-dd')}`;

  // DEBUGGING LOG FOR EMPTY SHIFT SLOT IN UNASSIGNED ROW
  if (userId === UNASSIGNED_SHIFT_USER_ID) {
    // This will log for each day cell in the unassigned row
    console.log(
      `[DayCell UNASSIGNED] Day: ${format(day, 'yyyy-MM-dd')}, shiftsInCell Count: ${shiftsInCell.length}`,
      // shiftsInCell // Uncomment to see the actual shifts objects
    );
  }

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
            minHeight: 60, // From original
            display: 'flex',
            flexDirection: 'column',
            bgcolor: snapshot.isDraggingOver ? 'action.focus' : 'transparent',
            transition: 'background-color 0.2s ease',
            overflow: 'hidden', // To prevent content from pushing cell height excessively
          }}
        >
          {shiftsInCell.length > 0 ? (
            shiftsInCell.map((shift, index) => (
              <Draggable key={shift.id} draggableId={shift.id} index={index}>
                {(providedDraggable, snapshotDraggable) => (
                  <ShiftCard
                    shift={shift}
                    onDeleteShift={onDeleteShift} // onDeleteShift prop on ShiftCard is not standard, DayCell receives it.
                    onEditShift={onEditShift}
                    onAddAnotherShift={onAddShift}
                    provided={providedDraggable}
                    snapshot={snapshotDraggable}
                    isSelected={selectedShiftIds.includes(shift.id)}
                    onToggleSelect={onToggleSelectShift}
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