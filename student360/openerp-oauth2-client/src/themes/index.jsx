import { CssBaseline, ThemeProvider as MUIThemeProvider, createTheme } from '@mui/material';
import { StylesProvider } from '@mui/styles';
import React from 'react';
import GlobalStyles from './GlobalStyle';
import componentsOverride from './overrides';
import palette from './palette';
import typography from './typography';
import customShadows from './customShadows';

const ThemeProvider = ({ children }) => {
  const themeOptions = React.useMemo(
    () => ({
      palette,
      shape: { borderRadius: 12 },
      typography,
      customShadows: customShadows(),
    }),
    [],
  );
  const theme = createTheme(themeOptions);

  theme.components = componentsOverride(theme);

  return (
    <StylesProvider injectFirst>
      <MUIThemeProvider theme={theme}>
        <CssBaseline />
        <GlobalStyles />
        {children}
      </MUIThemeProvider>
    </StylesProvider>
  );
};

export default ThemeProvider;
