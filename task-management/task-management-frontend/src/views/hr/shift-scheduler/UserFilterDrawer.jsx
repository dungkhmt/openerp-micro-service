import React from "react";
import {Autocomplete, Box, Button, Drawer, IconButton, TextField, Typography,} from "@mui/material";
import CloseIcon from "@mui/icons-material/Close.js";

export default function UserFilterDrawer({
                                           open,
                                           onClose,
                                           nameFilterValue,
                                           onNameFilterChange,
                                           departmentFilterValue,
                                           onDepartmentFilterChange,
                                           jobPositionFilterValue,
                                           onJobPositionFilterChange,
                                           departmentOptions,
                                           jobPositionOptions,
                                           onApply,
                                           onClear,
                                         }) {
  return (
    <Drawer anchor="right" open={open} onClose={onClose}>
      <Box
        sx={{
          width: 300,
          p: 2,
          display: "flex",
          flexDirection: "column",
          height: "100%",
          paddingTop: 6
        }}
      >
        <Box
          sx={{
            display: "flex",
            justifyContent: "space-between",
            alignItems: "center",
            mb: 2,
          }}
        >
          <Typography variant="h6">Bộ lọc</Typography>
          <IconButton onClick={onClose} size="small">
            <CloseIcon />
          </IconButton>
        </Box>

        <TextField
          label="Tên nhân viên"
          variant="outlined"
          size="small"
          fullWidth
          value={nameFilterValue}
          onChange={onNameFilterChange}
          sx={{ mb: 3 }}
        />

        <Autocomplete
          options={departmentOptions}
          getOptionLabel={(option) => option.name}
          value={departmentFilterValue}
          onChange={onDepartmentFilterChange}
          isOptionEqualToValue={(option, value) => option.code === value.code}
          className="custom-scrollbar"
          renderInput={(params) => (
            <TextField
              {...params}
              label="Phòng ban"
              variant="outlined"
              size="small"
              fullWidth
            />
          )}
          sx={{ mb: 3 }}
        />

        <Autocomplete
          options={jobPositionOptions}
          getOptionLabel={(option) => option.name}
          value={jobPositionFilterValue}
          onChange={onJobPositionFilterChange}
          isOptionEqualToValue={(option, value) => option.code === value.code}
          className="custom-scrollbar"
          renderInput={(params) => (
            <TextField
              {...params}
              label="Chức vụ"
              variant="outlined"
              size="small"
              fullWidth
            />
          )}
          sx={{ mb: 3 }}
        />

        <Box
          sx={{
            mt: "auto",
            display: "flex",
            justifyContent: "space-between",
            gap: 1,
          }}
        >
          <Button
            variant="outlined"
            onClick={onClear}
            color="inherit"
            sx={{ textTransform: "none" }}
          >
            Xóa bộ lọc
          </Button>
          <Button
            variant="contained"
            onClick={onApply}
            sx={{ textTransform: "none" }}
          >
            Áp dụng
          </Button>
        </Box>
      </Box>
    </Drawer>
  );
}