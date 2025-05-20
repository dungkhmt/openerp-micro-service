import {format} from "date-fns";
import {Draggable, Droppable} from "react-beautiful-dnd";
import {Grid} from "@mui/material";
import React from "react";
import EmptyShiftSlot from "./EmptyShiftSlot.jsx";
import ShiftCard from "./ShiftCard.jsx";

export default function DayCell({ userId, day, shiftsInCell, onAddShift, onDeleteShift, onEditShift, selectedShiftIds, onToggleSelectShift, isAnyShiftSelected }) {
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
              <Draggable key={shift.id} draggableId={shift.id} index={index}>
                {(providedDraggable, snapshotDraggable) => (
                  <ShiftCard
                    shift={shift}
                    onDeleteShift={onDeleteShift}
                    onEditShift={onEditShift}
                    onAddAnotherShift={onAddShift} // Pass onAddShift for adding another shift for the specific user/day
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