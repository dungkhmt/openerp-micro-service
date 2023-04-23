// import { withStyles } from "@material-ui/core/styles";
// import React from "react";
import Button from "@mui/material/Button";
import {blue} from "@mui/material/colors";
import {styled} from "@mui/material/styles";

// This component use API of MUI v4
// const PrimaryButton = withStyles((theme) => ({
//   root: {
//     textTransform: "none",
//     backgroundColor: blue[700],
//     color: theme.palette.getContrastText(blue[700]),
//     "&:hover": {
//       backgroundColor: blue[700],
//     },
//   },
// }))((props) => (
//   <Button variant="contained" {...props}>
//     {props.children}
//   </Button>
// ));

const StyledButton = styled(Button)(({ theme }) => ({
  textTransform: "none",
  backgroundColor: blue[700],
  color: theme.palette.getContrastText(blue[700]),
  "&:hover": {
    backgroundColor: blue[700],
  },
}));

const PrimaryButton = (props) => (
  <StyledButton variant="contained" {...props}>
    {props.children}
  </StyledButton>
);

export default PrimaryButton;
