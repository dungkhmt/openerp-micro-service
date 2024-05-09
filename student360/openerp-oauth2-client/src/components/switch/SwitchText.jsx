import { useTheme } from "@emotion/react";
import { Box, Button, Stack } from "@mui/material";
import React from "react";

const SwitchText = ({ active, setActive, option, sx }) => {
  const theme = useTheme();

  return (
    <Stack
      sx={{
        borderRadius: theme.spacing(3),
        backgroundColor: "grey.400",
        width: "100%",
        maxWidth: { xs: "100%", md: "250px" },
        flexDirection: "row",
        position: "relative",
        p: 0.5,
        gap: 1.5,
        ...sx,
      }}
    >
      <Box
        sx={{
          transform: `translateX(${
            active === option[0] ? 0 : `calc(100% + ${theme.spacing(1)})`
          })`,
          position: "absolute",
          width: `calc(50% - ${theme.spacing(1)})`,
          height: `calc(100% - ${theme.spacing(1)})`,
          borderRadius: theme.spacing(2.5),
          bgcolor: "grey.100",
          transition: "all 0.5s ease",
        }}
      />
      <Button
        disableRipple
        variant="text"
        sx={{
          color: "grey.800",
          typography: "contentSRegular",
          width: "50%",
          backgroundColor: "transparent !important",
        }}
        onClick={(e) => setActive(option[0])}
      >
        {option[0]}
      </Button>
      <Button
        disableRipple
        variant="text"
        sx={{
          color: "grey.800",
          typography: "contentSRegular",
          width: "50%",
          backgroundColor: "transparent !important",
        }}
        onClick={(e) => setActive(option[1])}
      >
        {option[1]}
      </Button>
    </Stack>
  );
};

export default SwitchText;
