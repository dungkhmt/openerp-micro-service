import { Typography } from "@mui/material";
import React from "react";

const ChartTitle = ({ children, sx }) => (
  <Typography
    sx={{
      typography: { xs: "contentMBold", sm: "h5" },
      color: "grey.900",
      ...sx,
    }}
  >
    {children}
  </Typography>
);

export default ChartTitle;
