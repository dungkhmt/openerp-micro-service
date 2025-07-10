import Spot from "./Spot";
import { Stack, Typography } from "@mui/material";
import React from "react";

const Legend = ({ color, title, value }) => {
  return (
    <Stack>
      <Stack direction={"row"} alignItems={"center"}>
        <Spot color={color} />
        <Typography
          sx={{ ml: 0.5, typography: "contentSBold", color: "grey.900" }}
        >
          {title}
        </Typography>
      </Stack>
      <Typography sx={{ typography: "contentSBold", color: "grey.900" }}>
        {value}
      </Typography>
    </Stack>
  );
};

export default Legend;
