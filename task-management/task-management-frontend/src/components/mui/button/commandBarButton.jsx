import { Button } from "@mui/material";
import PropTypes from "prop-types";

const CommandBarButton = (props) => {
  return (
    <Button
      {...props}
      styles={{
        color: "#FFF",
        backgroundColor: "#1976d2",
        margin: "10px 0",
      }}
    >
      {props.children}
    </Button>
  );
};

CommandBarButton.propTypes = {
  children: PropTypes.node.isRequired,
};

export default CommandBarButton;
