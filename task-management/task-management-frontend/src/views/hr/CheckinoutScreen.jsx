import React from "react";
import {
  Box,
  Paper,
  Typography,
  ThemeProvider,
  CssBaseline
} from "@mui/material";
import Timesheet from "@/components/item/Timesheet.jsx"
import { theme } from "./theme";

const CheckinoutScreenInternal = () => {
  return (
    <Box sx={{ mr: 2, bgcolor: 'background.default', minHeight: 'calc(100vh - 64px)' }}>
      <Paper sx={{ p: 2, mb: 2 }}>
        <Typography variant="h4" component="h1">
          Chấm công
        </Typography>
      </Paper>
      <Timesheet />
    </Box>
  );
};

const CheckinoutScreen = () => {
  return (
    <ThemeProvider theme={theme}>
      <CssBaseline />
      <CheckinoutScreenInternal />
    </ThemeProvider>
  );
};

export default CheckinoutScreen;