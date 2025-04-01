import { Box, TextField, IconButton } from "@mui/material";
import { Icon } from "@iconify/react";
import PropTypes from "prop-types";

const SearchField = ({
  value,
  onChange,
  placeholder = "Tìm kiếm...",
  fullWidth = false,
  onClear = () => {},
  iconSize = 20,
  inputSx = {},
  containerSx = {},
}) => {
  return (
    <Box
      sx={{
        display: "flex",
        width: fullWidth ? "100%" : "auto",
        ...containerSx,
      }}
    >
      <TextField
        autoComplete="off"
        size="small"
        fullWidth={fullWidth}
        value={value}
        onChange={onChange}
        placeholder={placeholder}
        InputProps={{
          startAdornment: (
            <Box sx={{ mr: 2, display: "flex" }}>
              <Icon icon="mdi:magnify" fontSize={iconSize} />
            </Box>
          ),
          endAdornment: value && (
            <IconButton
              onClick={onClear}
              sx={{ padding: 0, marginRight: "-4px" }}
            >
              <Icon icon="mdi:close" fontSize={iconSize} />
            </IconButton>
          ),
        }}
        sx={{
          "& .MuiInputBase-root > svg": { mr: 2 },
          ...inputSx,
        }}
      />
    </Box>
  );
};

SearchField.propTypes = {
  value: PropTypes.string.isRequired,
  onChange: PropTypes.func.isRequired,
  placeholder: PropTypes.string,
  fullWidth: PropTypes.bool,
  onClear: PropTypes.func,
  iconSize: PropTypes.number,
  inputSx: PropTypes.object,
  containerSx: PropTypes.object,
};

export default SearchField;
