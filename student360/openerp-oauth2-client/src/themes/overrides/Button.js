import { alpha } from '@mui/material';

export default function Button(theme) {
  return {
    MuiButton: {
      defaultProps: {
        disableRipple: true,
      },
      styleOverrides: {
        root: {
          borderRadius: 100,
          boxShadow: 'none',
          '&:hover': {
            boxShadow: 'none',
          },
        },
        sizeLarge: {
          height: 48,
        },
        outlined: {
          border: `1px solid ${theme.palette.grey[600]}`,
          color: theme.palette.grey[800],
          '&:hover': {
            border: `1px solid ${theme.palette.grey[600]}`,
            backgroundColor: alpha(theme.palette.grey[600], 0.1),
          },
        },
      },
    },
  };
}
