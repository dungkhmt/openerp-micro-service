import { Button } from "@mui/material";
import { styled } from "@mui/material/styles";

const TertiaryButton = styled((props) => (
  <Button color="primary" {...props}>
    {props.children}
  </Button>
))(() => ({
  textTransform: "none",
}));

export default TertiaryButton;
