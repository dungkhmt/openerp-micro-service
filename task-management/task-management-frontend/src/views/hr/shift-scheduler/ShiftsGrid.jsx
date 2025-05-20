import {Box} from "@mui/material";
import React from "react";
import UserRow from "./UserRow.jsx";

export default function ShiftsGrid({ currentDate, shifts, users, onAddShift, onDeleteShift, onEditShift, selectedShiftIds, onToggleSelectShift, isAnyShiftSelected }) {
  return (
    <Box>
      {users.map(user => (
        <UserRow
          key={user.id}
          user={user}
          currentDate={currentDate}
          shifts={shifts}
          onAddShift={onAddShift}
          onDeleteShift={onDeleteShift}
          onEditShift={onEditShift}
          selectedShiftIds={selectedShiftIds}
          onToggleSelectShift={onToggleSelectShift}
          isAnyShiftSelected={isAnyShiftSelected}
        />
      ))}
    </Box>
  );
}