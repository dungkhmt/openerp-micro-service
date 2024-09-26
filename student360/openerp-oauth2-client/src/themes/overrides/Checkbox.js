// Todo: replace checked and uncheck checkbox icon
export default function CheckBox(theme) {
  return {
    MuiCheckbox: {
      styleOverrides: {
        root: {
          padding: theme.spacing(0.5),
        },
      },
    },
  };
}
