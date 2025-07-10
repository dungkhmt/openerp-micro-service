import ErrorIcon from "@mui/icons-material/Error";
import { Stack, Typography } from "@mui/material";

const NotAuthorized = () => {
  return (
    <Stack
      direction="column"
      alignItems="center"
      justifyContent="center"
      bgcolor={"#ffe6e6"}
      height={`calc(100vh - 112px)`}
      minWidth={500}
    >
      <ErrorIcon color="error" fontSize="large" />
      <Typography variant="h4">You need permissions</Typography>
      <Typography variant="h6" textAlign={"center"}>
        You do not have permission to access this component. Please contact
        administrator if you need help.
      </Typography>
    </Stack>
  );
};

export default NotAuthorized;
