import { useTheme } from "@emotion/react";
import { Box, Chip, Stack, Typography } from "@mui/material";
import React from "react";

const IconZero = ({ ...other }) => (
  <svg
    xmlns="http://www.w3.org/2000/svg"
    width="16"
    height="16"
    viewBox="0 0 16 16"
    fill="none"
    {...other}
  >
    <rect x="3" y="7" width="10" height="2" rx="1" fill="inherit" />
  </svg>
);
const IconArrowUpRight = ({ ...other }) => (
  <svg
    width="16"
    height="16"
    viewBox="0 0 16 16"
    fill="none"
    xmlns="http://www.w3.org/2000/svg"
    {...other}
  >
    <path
      fillRule="evenodd"
      clipRule="evenodd"
      d="M5 4C5 3.44772 5.44772 3 6 3H12C12.5523 3 13 3.44772 13 4L13 4.00197V10C13 10.5523 12.5523 11 12 11C11.4477 11 11 10.5523 11 10V6.41424L4.71102 12.7032C4.3205 13.0938 3.68733 13.0938 3.29681 12.7032C2.90628 12.3127 2.90628 11.6795 3.29681 11.289L9.58582 5H6C5.44772 5 5 4.55228 5 4Z"
      fill="inherit"
    />
  </svg>
);

const IconArrowDownRight = ({ ...other }) => (
  <svg
    width="16"
    height="16"
    viewBox="0 0 16 16"
    fill="none"
    xmlns="http://www.w3.org/2000/svg"
    {...other}
  >
    <path
      fillRule="evenodd"
      clipRule="evenodd"
      d="M5 11.9961C5 12.5484 5.44772 12.9961 6 12.9961H12C12.5523 12.9961 13 12.5484 13 11.9961L13 11.9941V5.99609C13 5.44381 12.5523 4.99609 12 4.99609C11.4477 4.99609 11 5.44381 11 5.99609V9.58185L4.71102 3.29287C4.32049 2.90234 3.68733 2.90234 3.2968 3.29287C2.90628 3.68339 2.90628 4.31656 3.2968 4.70708L9.58582 10.9961H6C5.44772 10.9961 5 11.4438 5 11.9961Z"
      fill="inherit"
    />
  </svg>
);

const Balance = ({
  value = 0,
  percent = true,
  description,
  sx,
  responsive,
  ...other
}) => {
  const theme = useTheme();

  const getColor = () => {
    const palette = {
      color: theme.palette.grey[500],
      backgroundColor: theme.palette.grey[200],
    };

    if (value > 0) {
      palette.color = theme.palette.success.main;
      palette.backgroundColor = theme.palette.additional.green.light;
    } else if (value < 0) {
      palette.color = theme.palette.error.main;
      palette.backgroundColor = theme.palette.additional.pink.light;
    }

    return palette;
  };

  // TODO: refactor this
  const getIcon = () => {
    const icon = {
      zero: (
        <Box width="16px" height="16px">
          <IconZero fill={getColor().color} />
        </Box>
      ),
      positive: (
        <Box width="16px" height="16px">
          <IconArrowUpRight fill={getColor().color} />
        </Box>
      ),
      negative: (
        <Box width="16px" height="16px">
          <IconArrowDownRight fill={getColor().color} />
        </Box>
      ),
    };

    // eslint-disable-next-line eqeqeq
    if (value == 0) return icon.zero;
    if (value > 0) return icon.positive;
    return icon.negative;
  };

  return (
    <Chip
      sx={{
        height: theme.spacing(3),
        width: "fit-content",
        backgroundColor: getColor().backgroundColor,
        p: theme.spacing(0.5, 1),
        "& .MuiChip-label": { px: 0, ml: 1 },
        "& .MuiChip-icon": { ml: 0, mb: 0.25 },
        ...sx,
      }}
      icon={getIcon()}
      label={
        <Stack direction="row" gap={0.5} alignItems={"center"}>
          <Typography
            sx={{
              typography: responsive
                ? { xs: "content2xsBold", sm: "contentXsBold" }
                : "contentXsBold",
            }}
            color={getColor().color}
          >
            {Math.abs(value)}
            {percent ? "%" : ""}
          </Typography>
          {description && (
            <Typography variant="contentXsRegular" color="grey.700">
              {value === 0 ? "same as the same period" : "over the same period"}
            </Typography>
          )}
        </Stack>
      }
      {...other}
    />
  );
};

export default Balance;
