import { Paper, styled } from "@mui/material";

export const FilterWrapper = styled(Paper)(({ theme }) => ({
  display: "flex",
  justifyContent: "center",
  flexDirection: "column",
  gap: theme.spacing(3),
  minWidth: "300px",
  border: `1px solid ${theme.palette.divider}`,
  borderRadius: "8px",
  padding: theme.spacing(3),
  boxShadow: theme.shadows[1],

  [theme.breakpoints.up("md")]: {
    minWidth: "620px",
  },

  [theme.breakpoints.up("xl")]: {
    minWidth: "750px",
  },

  "& .MuiButton-root": {
    textTransform: "none",
    padding: theme.spacing(1, 2),
    borderRadius: "6px",
  },

  "& .action .MuiButton-root": {
    marginLeft: theme.spacing(2),
  },

  "& .btn_add-filter": {
    textTransform: "none",
    color: theme.palette.text.secondary,
    borderColor: theme.palette.divider,
    padding: theme.spacing(1, 2),
    justifyContent: "flex-start",

    "& svg": {
      fontSize: "16px",
      marginInlineStart: "4px",
    },

    "&:hover": {
      backgroundColor: theme.palette.action.hover,
    },
  },
}));
