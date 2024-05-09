export default function PaginationItem(theme) {
  return {
    MuiPaginationItem: {
      styleOverrides: {
        root: {
          color: theme.palette.grey[800],
          '&.Mui-selected': {
            backgroundColor: theme.palette.grey[300],
            borderRadius: '10px',
          },
        },
        invisible: {
          background: 'transparent',
        },
      },
    },
  };
}
