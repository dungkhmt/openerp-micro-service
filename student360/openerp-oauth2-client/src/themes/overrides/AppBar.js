export default function AppBar(theme) {
  return {
    MuiAppBar: {
      styleOverrides: {
        root: {
          background: theme.palette.background.paper,
          boxShadow: 'none',
          borderBottom: `1px solid ${theme.palette.divider}`,
        },
      },
    },
  };
}
