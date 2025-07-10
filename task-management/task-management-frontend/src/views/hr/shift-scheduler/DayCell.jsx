import {format} from "date-fns";
import {Draggable, Droppable} from "react-beautiful-dnd";
import {Grid} from "@mui/material";
import React from "react";
import EmptyShiftSlot from "./EmptyShiftSlot.jsx";
import ShiftCard from "./ShiftCard.jsx";
import {FRONTEND_UNASSIGNED_SHIFT_USER_ID} from "./ShiftScheduler.jsx";

export default function DayCell({
                                  userId,
                                  day,
                                  shiftsInCell,
                                  onAddShift,
                                  onDeleteShift,
                                  onEditShift,
                                  selectedShiftIds,
                                  onToggleSelectShift,
                                  isAnyShiftSelected,
                                  canAdmin,
                                  companyConfigs = {}
                                }) {
  const droppableId = `user-${userId}-day-${format(day, 'yyyy-MM-dd')}`;

  return (
    <Droppable droppableId={droppableId} type="SHIFT" isDropDisabled={!canAdmin}>
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
            bgcolor: snapshot.isDraggingOver && canAdmin ? 'action.focus' : 'transparent',
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
                isDragDisabled={!canAdmin || shift.type === 'time_off'}
              >
                {(providedDraggable, snapshotDraggable) => (
                  <ShiftCard
                    shift={shift}
                    shiftType={shift.type || (shift.userId === FRONTEND_UNASSIGNED_SHIFT_USER_ID ? 'unassigned' : 'regular')}
                    onEditShift={onEditShift}
                    onAddAnotherShift={userId !== FRONTEND_UNASSIGNED_SHIFT_USER_ID ? () => onAddShift(userId, day) : undefined}
                    onAddShiftToUnassignedDay={userId === FRONTEND_UNASSIGNED_SHIFT_USER_ID ? () => onAddShift(FRONTEND_UNASSIGNED_SHIFT_USER_ID, day) : undefined}
                    provided={providedDraggable}
                    snapshot={snapshotDraggable}
                    isSelected={selectedShiftIds.includes(shift.id)}
                    onToggleSelect={onToggleSelectShift}
                    isAnyShiftSelected={isAnyShiftSelected}
                    canAdmin={canAdmin}
                    companyConfigs={companyConfigs}
                  />
                )}
              </Draggable>
            ))
          ) : (
            canAdmin && !snapshot.isDraggingOver && <EmptyShiftSlot onAdd={() => onAddShift(userId, day)} />
          )}
          {provided.placeholder}
        </Grid>
      )}
    </Droppable>
  );
}