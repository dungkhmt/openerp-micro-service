import { Stack, Typography } from "@mui/material";
import React from "react";

import Spot from "./Spot";

// TODO: Refactor into a common component
const CustomTooltip = ({ active, payload, label }) => {
  if (active && payload && payload.length) {
    const spotData = payload.map((entry) => ({
      value: entry.value,
      color: entry.color,
      name: entry.dataKey,
    }));

    return (
      <Stack
        sx={{
          bgcolor: "grey.900",
          p: 1,
          borderRadius: 1,
        }}
        spacing={1}
      >
        <Typography
          sx={{
            typography: "contentXsRegular",
            color: "grey.100",
          }}
        >
          {label}
        </Typography>

        {spotData.map((data, index) => (
          <Stack key={index} direction="row" alignItems="center">
            <Spot color={data.color} />
            <Typography
              sx={{
                ml: 0.5,
                fontWeight: "600",
                fontSize: "12px",
                lineHeight: "12px",
                letterSpacing: "-0.12px",
                color: "#FCFCFC",
                fontStyle: "normal",
              }}
            >
              {data.name}: {data.value}
            </Typography>
          </Stack>
        ))}
      </Stack>
    );
  }

  return null;
};

export default CustomTooltip;
