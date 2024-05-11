import React, { useState } from "react";
import { Box, Grid } from "@mui/material";

import TestMenu from "./test-menu";
import TestInstructions from "./test-instructions";
import TestQuestion from "./test-question";

export default function TestDisplay() {
  const [showTestInstructions, setShowTestInstructions] = useState(true);

  const handleShowInstructionsButtonClick = () => {
    setShowTestInstructions(true);
  };

  const handleCloseTestInstructions = () => {
    setShowTestInstructions(false);
  };

  return (
    <Grid
      container
      direction="column"
      justifyContent="center"
      alignItems="center"
      spacing={2}
      px={1}
    >
      <TestMenu
        onShowInstructionsButtonClick={handleShowInstructionsButtonClick}
      />
      <Grid item xs={12} lg={6}>
        {showTestInstructions ? (
          <TestInstructions
            onCloseTestInstructions={handleCloseTestInstructions}
          />
        ) : (
          <TestQuestion />
        )}
      </Grid>
    </Grid>
  );
}
