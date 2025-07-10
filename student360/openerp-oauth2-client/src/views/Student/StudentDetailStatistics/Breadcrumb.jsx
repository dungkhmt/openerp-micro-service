// import { IconSVG } from "@/components";
import { Link, Stack, Typography } from "@mui/material";
import React from "react";
import { Link as RouterLink } from "react-router-dom";
import HomeIcon from "@mui/icons-material/Home";
import ArrowForwardIosIcon from "@mui/icons-material/ArrowForwardIos";

// TODO: Migrate to common component
const Breadcrumb = (props) => (
  <Stack direction="row" alignItems="center" gap={1} {...props}>
    <Link
      component={RouterLink}
      to="/students"
      sx={{
        width: "max-content",
        textDecoration: "none",
      }}
    >
      <Stack direction="row" alignItems="center" color="#000" gap={1}>
        <HomeIcon />
        <Typography fontSize="1rem">Student List</Typography>
        <ArrowForwardIosIcon />
      </Stack>
    </Link>
    {/* <IconSVG name="arrow-right" /> */}
    <Typography fontSize="1rem" fontWeight="500" color='primary.main'>
      View Student Details
    </Typography>
  </Stack>
);

export default Breadcrumb;
