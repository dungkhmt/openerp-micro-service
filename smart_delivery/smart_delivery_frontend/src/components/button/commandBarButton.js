import { Button } from "@mui/material";

const CommandBarButton = (props) => {
  return <Button {...props} styles={{
      color: "#FFF",
      backgroundColor: "#1976d2",
      margin: "10px 0",
    }}>
    {props.children}
  </Button>
}

export default CommandBarButton;