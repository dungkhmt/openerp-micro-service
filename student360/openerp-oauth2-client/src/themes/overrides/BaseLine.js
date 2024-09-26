export default function BaseLine(theme) {
  return {
    MuiCssBaseline: {
      styleOverrides: {
        '#nprogress': {
          position: 'fixed',
          pointerEvents: 'none',
          zIndex: theme.zIndex.drawer + 100,
        },
        '#nprogress .bar': {
          background: theme.palette.primary.light,
        },
        '#nprogress .spinner-icon': {
          borderTopColor: theme.palette.primary.light,
          borderLeftColor: theme.palette.primary.light,
        },
        '#nprogress .peg': {
          boxShadow: `0 0 15px ${theme.palette.primary.light}, 0 0 8px${theme.palette.primary.light}`,
        },
      },
    },
  };
}
