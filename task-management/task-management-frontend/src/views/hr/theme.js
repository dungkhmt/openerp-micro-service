// src/features/rosterConfiguration/theme.js
import {createTheme} from '@mui/material/styles';

export const theme = createTheme({
  palette: {
    primary: { main: '#1e88e5', light: '#6ab7ff', dark: '#005cb2' },
    secondary: { main: '#7e57c2', light: '#b085f5', dark: '#4d2c91' },
    success: { main: '#43a047' },
    error: { main: '#e53935' },
    info: { main: '#039be5'},
    background: { default: '#f4f5f7', paper: '#ffffff' },
  },
  typography: {
    fontFamily: '"Inter", "Roboto", "Helvetica", "Arial", sans-serif',
    h4: { fontWeight: 700, fontSize: '1.65rem', letterSpacing: '0.01em' },
    h5: { fontWeight: 600, fontSize: '1.2rem' },
    h6: { fontWeight: 600, fontSize: '1rem' },
    subtitle1: { fontWeight: 500, fontSize: '0.9rem'},
    body1: { fontSize: '0.875rem'},
    caption: { fontSize: '0.75rem', color: '#5f6368'}
  },
  components: {
    MuiPaper: { styleOverrides: { root: { borderRadius: 10, boxShadow: '0px 4px 12px rgba(0,0,0,0.05)' } } },
    MuiButton: { styleOverrides: { root: { borderRadius: 6, textTransform: 'none', fontWeight: 600 } } },
    MuiTextField: { defaultProps: { variant: 'outlined', size: 'small' } },
    MuiChip: { styleOverrides: { root: { fontWeight: 500, borderRadius: 16 }}},
    MuiCssBaseline: {
      styleOverrides: (themeParam) => ({
        body: {
          '&::-webkit-scrollbar': {
            width: '8px',
            height: '8px',
          },
          '&::-webkit-scrollbar-track': {
            background: themeParam.palette.grey[100],
            borderRadius: '4px',
          },
          '&::-webkit-scrollbar-thumb': {
            background: themeParam.palette.grey[300],
            borderRadius: '4px',
            '&:hover': {
              background: themeParam.palette.grey[400],
            },
          },
        },
        '.custom-scrollbar': {
          '&::-webkit-scrollbar': {
            width: '6px',
            height: '6px',
          },
          '&::-webkit-scrollbar-track': {
            background: themeParam.palette.grey[200],
            borderRadius: '3px',
          },
          '&::-webkit-scrollbar-thumb': {
            background: themeParam.palette.grey[400],
            borderRadius: '3px',
            '&:hover': {
              background: themeParam.palette.grey[500],
            },
          },
        },
      })
    },
    MuiBox: {
      styleOverrides: {}
    }
  }
});