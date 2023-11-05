import { Box, SvgIcon, Typography } from "@mui/material";
import { FacebookCircularProgress } from "./progress-bar/CustomizedCircularProgress";
import { ReactComponent as Logo } from "../../assets/icons/logo.svg";

const AppLoading = () => {
  return (
    <Box
      sx={{
        height: "100vh",
        display: "flex",
        alignItems: "center",
        justifyContent: "center",
        flexDirection: "column",
      }}
    >
      <Box
        sx={{
          display: "flex",
          alignItems: "center",
          justifyContent: "center",
          flexDirection: "column",
          flexGrow: 1,
        }}
      >
        <SvgIcon sx={{ fontSize: 150, mb: 4 }} viewBox="0 0 150 150">
          <Logo width={132} height={132} x={9} y={9} />
        </SvgIcon>
        <Box>
          <FacebookCircularProgress />
        </Box>
      </Box>
      <Box>
        <Typography sx={{ mb: 4 }}>OpenERP Team</Typography>
      </Box>
    </Box>
  );
};

export { AppLoading };
