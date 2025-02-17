import { Box, Button, Stack, Typography } from '@mui/material';
import React from 'react';

const GroupToolbar = React.memo(({ onAdd, onManage }) => (
  <Box sx={{ p: 2, display: "flex", justifyContent: "space-between" }}>
    <Typography variant="h5">Danh sách nhóm</Typography>
    <Stack direction="row" spacing={2}>
      <Button variant="contained" onClick={onManage}>
        Chỉnh sửa nhóm
      </Button>
      <Button variant="contained" onClick={onAdd}>
        Thêm nhóm mới
      </Button>
    </Stack>
  </Box>
));

export default GroupToolbar;