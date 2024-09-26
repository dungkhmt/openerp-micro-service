import { TextField } from "@mui/material";
import { AppColors } from "../../shared/AppColors";

/**
 * @typedef Prop
 * @property {string} label
 * @property {any} value
 * @property {Function} onChange
 * @property {string} error
 * @property {import("react").HTMLInputTypeAttribute} type
 * @property {string} message
 * @property {boolean} readOnly
 * @property {boolean} isFullWidth
 * @property {boolean} required
 * @property {import("@mui/material").SxProps} sx
 * @param {Prop} props
 */
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
    isFullWidth,
    sx,
  } = props;
  return (
    <TextField
      sx={{
        mb: 2,
        ".MuiInputLabel-asterisk": {
          color: AppColors.error,
        },
        ...sx,
      }}
      fullWidth={isFullWidth}
      required={required}
      label={label}
      value={value}
      // placeholder="Default Value"
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
