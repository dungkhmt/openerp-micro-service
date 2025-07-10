import { Box, Stack, Typography } from "@mui/material";
import React from "react";

const EmptyState = ({ search }) => {
  return (
    <Stack
      width={"100%"}
      m={2.5}
      alignItems={"center"}
      justifyContent={"center"}
      maxHeight={350}
      height={"100%"}
    >
      <Box
        component="img"
        sx={{
          display: "block",
          width: 320,
          height: 320,
        }}
        src="../../assets/icons/empty-state.svg"
        alt=""
      />
      {search ? (
        <>
          <Typography variant="h6">
            No {search.charAt(0).toUpperCase() + search.slice(1)} Found
          </Typography>
          <Typography variant="contentXsRegular">
            Your search did not match any {search}s.
          </Typography>
          <Typography variant="contentXsRegular">Please try again!</Typography>
        </>
      ) : (
        <Typography sx={{ typography: "h6", mt: "-24px" }}>
          {" "}
          No data available{" "}
        </Typography>
      )}
    </Stack>
  );
};

export default EmptyState;
