export default function Tab(theme) {
  return {
    MuiTab: {
      styleOverrides: {
        root: {
          ...theme.typography.contentMBold,
          color: theme.palette.grey[800],
          margin: theme.spacing(1.5, 0),
          borderRadius: 80,
        },
      },
    },
    MuiTabs: {
      styleOverrides: {
        indicator: {
          backgroundColor: theme.palette.primary.light,
          height: 4,
        },
        scroller: {
          '::after': {
            content: '""',
            position: 'absolute',
            bottom: 0,
            left: 0,
            width: '100%',
            height: 1,
            backgroundColor: theme.palette.grey[300],
            zIndex: -1,
          },
        },
      },
    },
    MuiTabPanel: {
      styleOverrides: {
        root: {
          padding: 0,
        },
      },
    },
  };
}
