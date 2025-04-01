import React from "react";
import {IconButton, Paper, Stack, Typography} from "@mui/material";
import KeyboardBackspaceIcon from "@mui/icons-material/KeyboardBackspace";

const ProgrammingContestLayout = ({title, children, onBack}) => {
  return (
    <>
      <Stack direction="row" spacing={1} alignItems="center" mb={1.5}>
        <IconButton
          sx={{backgroundColor: "#0000000A"}}
          aria-label="back"
          onClick={onBack}
        >
          <KeyboardBackspaceIcon/>
        </IconButton>
        <Typography variant="h6" sx={{marginTop: "8px", marginBottom: "8px"}}>
          {title}
        </Typography>
      </Stack>

      <Paper elevation={1} sx={{padding: "16px 24px", borderRadius: 4}}>
        {children}
      </Paper>
    </>
  );
};

export default ProgrammingContestLayout;
