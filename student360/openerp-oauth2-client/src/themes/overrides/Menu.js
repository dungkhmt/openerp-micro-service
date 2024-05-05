import { alpha } from '@mui/material';
import customShadows from '../customShadows';

export default function Menu(theme) {
  return {
    MuiMenu: {
      defaultProps: {
        elevation: 0,
      },
      styleOverrides: {
        root: {
          borderRadius: '20px',
          color: theme.palette.grey[800],
          fontWeight: '600',
        },
        paper: {
          marginTop: theme.spacing(1),
          padding: theme.spacing(2),
          borderRadius: theme.shape.borderRadius * 2,
          boxShadow: customShadows().dropdown,
        },
        list: {
          margin: 0,
          padding: 0,
        },
      },
    },

    MuiMenuItem: {
      styleOverrides: {
        root: {
          padding: theme.spacing(1),
          borderRadius: theme.shape.borderRadius,
          transition: 'all 100ms ease-in-out',

          '&.Mui-selected': {
            backgroundColor: theme.palette.primary.main,
            color: theme.palette.common.white,

            '&:hover': {
              backgroundColor: theme.palette.primary.dark,
              color: theme.palette.common.white,
            },
          },

          ':hover': {
            backgroundColor: alpha(theme.palette.primary.main, 0.15),
            color: theme.palette.primary.main,
          },
        },
      },
    },
  };
}
