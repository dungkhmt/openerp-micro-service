import CssBaseline from "@mui/material/CssBaseline";
import PropTypes from "prop-types";
import { ThemeProvider as MuiThemeProvider, createTheme } from "@mui/material";
import breakpoints from "./breakpoints";
import overrides from "./overrides";
import palette from "./palette";
import spacing from "./spacing";
import typography from "./typography";
import shadows from "./shadows";

const themeOptions = {
  breakpoints: breakpoints(),
  components: overrides(),
  palette: palette("light"),
  ...spacing,
  typography,
  shadow: shadows("light"),
  mixins: {
    toolbar: {
      minHeight: 64,
    },
  },
};

const ThemeProvider = ({ children }) => {
  const theme = createTheme(themeOptions);

  return (
    <MuiThemeProvider theme={theme}>
      <CssBaseline />
      {children}
    </MuiThemeProvider>
  );
};

ThemeProvider.propTypes = {
  children: PropTypes.node,
};

export default ThemeProvider;
