import { teal } from "@mui/material/colors";
import { styled } from "@mui/material/styles";
import Tab from "@mui/material/Tab";

export function a11yProps(index) {
  return {
    id: `full-width-tab-${index}`,
    "aria-controls": `full-width-tabpanel-${index}`,
  };
}

// This component use MUI v4
// export const AntTab = withStyles((theme) => ({
//   root: {
//     transition: "text-shadow .3s",
//     textTransform: "none",
//     minWidth: 72,
//     fontWeight: theme.typography.fontWeightRegular,
//     marginRight: theme.spacing(4),
//     fontFamily: [
//       "-apple-system",
//       "BlinkMacSystemFont",
//       '"Segoe UI"',
//       "Roboto",
//       '"Helvetica Neue"',
//       "Arial",
//       "sans-serif",
//       '"Apple Color Emoji"',
//       '"Segoe UI Emoji"',
//       '"Segoe UI Symbol"',
//     ].join(","),
//     "&:hover": {
//       opacity: 1,
//       // fontWeight: theme.typography.fontWeightMedium,
//       textShadow: "0 0 .65px, 0 0 .65px",
//     },
//     "&$selected": {
//       color: teal[800],
//       // fontWeight: theme.typography.fontWeightMedium,
//       textShadow: "0 0 .65px, 0 0 .65px",
//     },
//     "&:focus": {
//       color: teal[800],
//     },
//   },
//   selected: {},
// }))((props) => <Tab wrapped disableRipple {...props} />);

export const AntTab = styled((props) => (
  <Tab wrapped disableRipple {...props} />
))(({ theme }) => ({
  transition: "text-shadow .3s",
  textTransform: "none",
  minWidth: 72,
  marginRight: `${theme.spacing(1)} !important`,
  fontWeight: theme.typography.fontWeightRegular,
  fontSize: "0.875rem",
  fontFamily: [
    "-apple-system",
    "BlinkMacSystemFont",
    '"Segoe UI"',
    "Roboto",
    '"Helvetica Neue"',
    "Arial",
    "sans-serif",
    '"Apple Color Emoji"',
    '"Segoe UI Emoji"',
    '"Segoe UI Symbol"',
  ].join(","),
  "&:hover": {
    opacity: 1,
    // fontWeight: theme.typography.fontWeightMedium,
    textShadow: "0 0 .65px, 0 0 .65px",
  },
  "&.Mui-selected": {
    // fontWeight: theme.typography.fontWeightMedium,
    color: teal[800],
    textShadow: "0 0 .65px, 0 0 .65px",
  },
  "&.Mui-focusVisible": {
    color: teal[800],
  },
}));
