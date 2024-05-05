import { useTheme } from "@emotion/react";
import { Stack } from "@mui/material";
import React from "react";

const Spot = ({ color = "#FA6464" }) => {
  const theme = useTheme();

  return (
    <Stack sx={{ width: theme.spacing(1.5), height: theme.spacing(1.5) }}>
      <svg
        xmlns="http://www.w3.org/2000/svg"
        width="12"
        height="12"
        viewBox="0 0 12 12"
        fill="none"
      >
        <rect width="12" height="12" rx="4" fill={color} />
      </svg>
    </Stack>
  );
};

export default Spot;
