import { TextField } from "@mui/material";

const CustomInput = (props) => {
  const {
    label,
    value,
    onChange,
    error,
    message,
    type,
    readOnly,
    required,
    sx,
  } = props;
  return (
    <TextField
      sx={{ mb: 2, ...sx }}
      fullWidth
      required={required}
      label={label}
      value={value}
      onChange={onChange}
      error={error}
      helperText={message ? message : ""}
      variant="outlined"
      size="small"
      type={type}
      InputProps={{
        readOnly,
        disabled: readOnly,
      }}
      InputLabelProps={{
        shrink: true,
      }}
      inputProps={{ min: 0 }}
    />
  );
};
export default CustomInput;
