import { FormControl, InputLabel, MenuItem, Select } from "@mui/material";
import PropTypes from "prop-types";

const SelectBasic = ({
  label,
  ref,
  onChange,
  error,
  data,
  value,
  valueItem,
  nameItem,
}) => {
  return (
    <FormControl fullWidth>
      <InputLabel id="demo-simple-select-label" sx={{ fontSize: "16px" }}>
        {label}
      </InputLabel>
      <Select
        labelId="demo-simple-select-label"
        id="demo-simple-select"
        label={label}
        ref={ref}
        defaultValue=""
        value={value}
        onChange={onChange}
        error={error}
      >
        {data.map((item) => (
          <MenuItem key={item[valueItem]} value={item[valueItem]}>
            {item[nameItem]}
          </MenuItem>
        ))}
      </Select>
    </FormControl>
  );
};

SelectBasic.propTypes = {
  label: PropTypes.string.isRequired,
  ref: PropTypes.func.isRequired,
  onChange: PropTypes.func.isRequired,
  error: PropTypes.bool.isRequired,
  data: PropTypes.array.isRequired,
  value: PropTypes.string.isRequired,
  valueItem: PropTypes.string.isRequired,
  nameItem: PropTypes.string.isRequired,
};

export default SelectBasic;
