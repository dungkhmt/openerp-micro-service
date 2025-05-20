import {AppBar, Box, Button, Toolbar, Typography} from "@mui/material";
import DeleteForeverIcon from "@mui/icons-material/DeleteForever.js";
import ContentCopyIcon from "@mui/icons-material/ContentCopy.js";
import React from "react";
import {BULK_ACTIONS_BAR_HEIGHT, TOP_BAR_HEIGHT} from "./ShiftScheduler.jsx";

export default function BulkActionsBar({
                          selectedCount,
                          onDeleteSelected,
                          onOpenCopyModal,
                          onDeselectAll,
                        }) {
  if (selectedCount === 0) {
    return null;
  }

  return (
    <AppBar
      position="sticky"
      color="default"
      elevation={2}
      sx={{
        top: TOP_BAR_HEIGHT,
        height: BULK_ACTIONS_BAR_HEIGHT,
        zIndex: 19,
        bgcolor: 'primary.lighter',
        borderBottom: theme => `1px solid ${theme.palette.divider}`
      }}
    >
      <Toolbar variant="dense" sx={{ justifyContent: 'space-between', alignItems: 'center', height: '100%' }}>
        <Box sx={{ display: 'flex', alignItems: 'center' }}>
          <Typography variant="subtitle2" sx={{ mr: 2, color: 'primary.darkerText', fontWeight:'bold' }}>
            Đã chọn: {selectedCount} ca
          </Typography>
          <Button
            size="small"
            variant="contained"
            color="error"
            startIcon={<DeleteForeverIcon />}
            onClick={onDeleteSelected}
            sx={{ mr: 1, color:'white' }}
          >
            Xóa ({selectedCount})
          </Button>
          <Button
            size="small"
            variant="contained"
            color="secondary"
            startIcon={<ContentCopyIcon />}
            onClick={onOpenCopyModal}
            sx={{ mr: 1, color:'white' }}
            title="Sao chép các ca đã chọn"
          >
            Sao chép
          </Button>
        </Box>
        <Button
          size="small"
          variant="outlined"
          onClick={onDeselectAll}
          color="primary"
          sx={{borderColor: 'primary.dark', color:'primary.dark'}}
        >
          Bỏ chọn tất cả
        </Button>
      </Toolbar>
    </AppBar>
  );
}
