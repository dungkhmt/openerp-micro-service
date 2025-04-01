const Stepper = () => {
  return {
    MuiStepper: {
      styleOverrides: {
        root: ({ theme }) => ({
          padding: theme.spacing(3, 0),
        }),
      },
    },
    MuiStepIcon: {
      styleOverrides: {
        root: ({ theme }) => ({
          color: theme.palette.text.disabled,
          "&.Mui-active": {
            color: theme.palette.primary.light,
          },
          "&.Mui-completed": {
            color: theme.palette.success.light,
          },
        }),
      },
    },
    MuiStepLabel: {
      styleOverrides: {
        label: ({ theme }) => ({
          color: theme.palette.text.secondary,
          "&.Mui-active": {
            color: theme.palette.primary.main,
            fontWeight: 600,
          },
          "&.Mui-completed": {
            color: theme.palette.success.main,
            fontWeight: 600,
          },
        }),
      },
    },
  };
};

export default Stepper;
