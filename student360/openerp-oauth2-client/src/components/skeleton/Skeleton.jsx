import { Skeleton, Stack, useTheme } from "@mui/material";
import React from "react";

const SkeletonCourse = ({ limit = 4 }) => {
  const theme = useTheme();

  return (
    <Stack gap={2}>
      {[...Array(parseInt(limit))].map((_, index) => (
        <Stack key={`skeletonCell-${index}`}>
          <Skeleton
            margin={theme.spacing(0.5)}
            variant="rounded"
            width="100%"
            height={theme.spacing(18)}
          />
        </Stack>
      ))}
    </Stack>
  );
};

export default SkeletonCourse;
