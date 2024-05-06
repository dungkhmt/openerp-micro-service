export default function Autocomplete(theme) {
  return {
    MuiAutocomplete: {
      styleOverrides: {
        tag: { background: theme.palette.grey[100] },
        paper: { padding: theme.spacing(1.5) },
        listbox: { padding: 0 },
        option: {
          borderRadius: theme.shape.borderRadius,
          ':not(:last-child)': { marginBottom: theme.spacing(1) },
        },
      },
    },
  };
}
