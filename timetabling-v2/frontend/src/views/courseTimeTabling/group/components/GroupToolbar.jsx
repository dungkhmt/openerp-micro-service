
import { Box, Button, Typography } from '@mui/material';
import React from 'react';

const GroupToolbar = React.memo(({ onAdd }) => (
  <Box sx={{ p: 2, display: "flex", justifyContent: "space-between" }}>
    <Typography variant="h5">Danh sách nhóm</Typography>
    <Button variant="contained" onClick={onAdd}>
      Thêm nhóm mới
    </Button>
  </Box>
));

export default GroupToolbar;