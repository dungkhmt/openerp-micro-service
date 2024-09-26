// import { Button } from "@material-ui/core";
// import { withStyles } from "@material-ui/core/styles";
import {Button, styled} from "@mui/material";

// This is component in MUI v4
// const TertiaryButton = withStyles((theme) => ({
//   root: {
//     textTransform: "none",
//   },
// }))((props) => (
//   <Button color="primary" {...props}>
//     {props.children}
//   </Button>
// ));

const TertiaryButton = styled((props) => (
  <Button color="primary" {...props}>
    {props.children}
  </Button>
))(({ theme }) => ({
  textTransform: "none",
}));

export default TertiaryButton;
