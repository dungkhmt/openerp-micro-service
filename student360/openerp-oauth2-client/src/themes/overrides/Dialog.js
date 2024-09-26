export default function Dialog(theme) {
  return {
    MuiDialog: {
      styleOverrides: {
        paper: {
          borderRadius: theme.shape.borderRadius * 2.5,
        },
      },
    },
  };
}
