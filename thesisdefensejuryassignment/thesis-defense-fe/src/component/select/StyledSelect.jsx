import {
  MenuItem,
  TextField,
  menuClasses,
  menuItemClasses,
  popoverClasses,
} from "@mui/material";

function StyledSelect({ options, children, SelectProps, ...others }) {
  return (
    <TextField
      size="small"
      select
      sx={{ minWidth: 300, mr: 2 }}
      SelectProps={{
        ...SelectProps,
        MenuProps: {
          sx: {
            [`& .${menuClasses.list}`]: {
              paddingLeft: 1,
              paddingRight: 1,
              [`& .${menuItemClasses.root}`]: {
                borderRadius: 2,
                my: 0.25,
              },
              "& .Mui-selected, .Mui-selected:hover": {
                color: "#ffffff",
                backgroundColor: "#1976d2", // updated backgroundColor
                "&.Mui-focusVisible": { background: "#1976d2" },
              },
            },
            [`& .${popoverClasses.paper}`]: {
              minWidth: 300,
              borderRadius: 2,
              boxShadow:
                "0 12px 28px 0 rgba(0, 0, 0, 0.2), 0 2px 4px 0 rgba(0, 0, 0, 0.1), inset 0 0 0 1px rgba(255, 255, 255, 0.5)",
            },
          },
        },
      }}
      {...others}
    >
      {children
        ? children
        : options.map((option) => (
            <MenuItem
              key={option.value}
              value={option.value}
              sx={{ px: 1, borderRadius: 1.5 }}
            >
              {option.label}
            </MenuItem>
          ))}
    </TextField>
  );
}

export default StyledSelect;
