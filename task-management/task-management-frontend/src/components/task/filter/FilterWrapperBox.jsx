import { Box, styled } from "@mui/material";
import { hexToRGBA } from "../../utils/hex-to-rgba";

export const FilterWrapperBox = styled(Box)(({ theme }) => ({
  fontSize: "0.8rem",
  color: theme.palette.text.secondary,
  backgroundColor: theme.palette.background.paper,
  padding: theme.spacing(1, 2),
  borderRadius: "4px",
  border: `1px solid ${theme.palette.divider}`,
  cursor: "pointer",
  position: "relative",

  "&>svg": {
    position: "absolute",
    right: theme.spacing(2),
    top: "50%",
    transform: "translateY(-50%)",
  },

  "&:hover": {
    backgroundColor: hexToRGBA(theme.palette.primary.main, 0.1),
  },
}));
