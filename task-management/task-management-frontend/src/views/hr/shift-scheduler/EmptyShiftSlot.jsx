import React, {useState} from "react";
import {Grid, Paper} from "@mui/material";
import AddIcon from "@mui/icons-material/Add.js";
import {format} from "date-fns";
import {Draggable, Droppable} from "react-beautiful-dnd";

export default function EmptyShiftSlot({ onAdd }) {
  const [isHovered, setIsHovered] = useState(false);

  return (
    <Paper
      elevation={0}
      onMouseEnter={() => setIsHovered(true)}
      onMouseLeave={() => setIsHovered(false)}
      sx={{
        minHeight: 52,
        m: 0.5,
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        borderWidth: 1,
        borderStyle: 'dashed',
        borderColor: isHovered ? 'grey.500' : 'transparent',
        bgcolor: isHovered ? 'grey.100' : 'transparent',
        cursor: 'pointer',
        flexGrow: 1,
        transition: 'background-color 0.2s ease-in-out, border-color 0.2s ease-in-out',
      }}
      onClick={onAdd}
      title="Thêm ca làm việc mới"
    >
      <AddIcon
        color="action"
        sx={{
          transition: 'opacity 0.2s ease-in-out',
          opacity: isHovered ? 1 : 0,
        }}
      />
    </Paper>
  );
}

//
