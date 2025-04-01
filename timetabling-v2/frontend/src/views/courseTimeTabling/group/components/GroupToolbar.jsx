import { Button, Toolbar } from "@mui/material";
import AddIcon from "@mui/icons-material/Add";
import React from 'react';

export default function GroupToolbar({ onAdd }) {
  return (
    <Toolbar sx={{ justifyContent: "flex-end", px: 2 }}>
      <Button
        variant="contained"
        color="primary"
        startIcon={<AddIcon />}
        onClick={onAdd}
      >
        Thêm nhóm mới
      </Button>
    </Toolbar>
  );
}