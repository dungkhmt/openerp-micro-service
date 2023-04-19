import { Tab, Tabs, styled } from "@mui/material";
import { grey } from "@mui/material/colors";

export const TCTabs = styled((props) => <Tabs {...props} />)(({ theme }) => ({
  minHeight: 38,
  position: "relative",
  display: "inline-block",
  backgroundColor: grey[100],
  ".MuiTabs-indicator": {
    minHeight: 38,
    borderRadius: 4,
  },
}));

export const TCTab = styled((props) => <Tab disableRipple {...props} />)(
  ({ theme }) => ({
    fontWeight: 600,
    fontSize: 14,
    minHeight: 38,
    zIndex: 1,
    color: grey[900],
    lineHeight: 0,
    "&.Mui-selected": {
      color: "#fff",
    },
  })
);
