import customShadows from '../customShadows';

export default function Popover(theme) {
  return {
    MuiPopover: {
      styleOverrides: {
        root: {
          color: theme.palette.grey[800],
        },
        paper: {
          boxShadow: customShadows().dropdown,
          borderRadius: theme.shape.borderRadius * 2,
        },
      },
    },
  };
}
