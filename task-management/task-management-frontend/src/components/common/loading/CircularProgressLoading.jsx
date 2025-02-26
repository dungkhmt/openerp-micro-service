import { Box, CircularProgress, Typography } from "@mui/material";

const CircularProgressLoading = () => (
  <Box
    sx={{
      mt: 14,
      display: "flex",
      alignItems: "center",
      flexDirection: "column",
    }}
  >
    <CircularProgress sx={{ mb: 4 }} />
    <Typography>Đang tải...</Typography>
  </Box>
);

export { CircularProgressLoading };
