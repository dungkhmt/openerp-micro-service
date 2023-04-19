import Tabs from "@mui/material/Tabs";
import { teal } from "@mui/material/colors";
import { styled } from "@mui/material/styles";
import { AntScrollButton } from "./AntScrollButton";

export const AntTabs = styled((props) => (
  <Tabs ScrollButtonComponent={AntScrollButton} {...props}>
    {props.children}
  </Tabs>
))(({ theme }) => ({
  position: "relative",
  borderBottom: "1px solid #e8e8e8",
  "& .MuiTabs-indicator": {
    backgroundColor: teal[800],
  },
}));
