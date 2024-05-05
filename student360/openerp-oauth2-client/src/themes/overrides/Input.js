import { alpha } from '@mui/material';

export default function Input(theme) {
  return {
    MuiInputBase: {
      defaultProps: {
        fullWidth: true,
      },
      styleOverrides: {
        root: {
          '&.Mui-disabled svg': { color: theme.palette.text.disabled },
        },
        input: {
          '&::placeholder': {
            opacity: 1,
            color: theme.palette.text.disabled,
          },
        },
      },
    },
    MuiInput: {
      styleOverrides: {
        underline: {
          '&:before': {
            borderBottomColor: alpha(theme.palette.grey[500], 0.56),
          },
        },
      },
    },
    MuiFilledInput: {
      styleOverrides: {
        root: {
          backgroundColor: alpha(theme.palette.grey[500], 0.12),
          '&:hover': {
            backgroundColor: alpha(theme.palette.grey[500], 0.16),
          },
          '&.Mui-focused': {
            backgroundColor: theme.palette.action.focus,
          },
          '&.Mui-disabled': {
            backgroundColor: theme.palette.action.disabledBackground,
          },
        },
        underline: {
          '&:before': {
            borderBottomColor: alpha(theme.palette.grey[500], 0.56),
          },
        },
      },
    },
    MuiOutlinedInput: {
      styleOverrides: {
        root: {
          backgroundColor: theme.palette.grey[200],
          '.MuiOutlinedInput-notchedOutline': {
            borderColor: theme.palette.grey[300],
            transition: theme.transitions.create('border-color'),
          },
          '&:hover .MuiOutlinedInput-notchedOutline': {
            borderColor: theme.palette.secondary.light,
          },
          '&.Mui-focused .MuiOutlinedInput-notchedOutline': {
            borderColor: theme.palette.secondary.light,
          },
          '&.Mui-focused:hover .MuiOutlinedInput-notchedOutline': {
            borderColor: theme.palette.secondary.light,
          },
        },
        medium: {
          input: {
            padding: 14,
          },
        },
      },
    },
  };
}
