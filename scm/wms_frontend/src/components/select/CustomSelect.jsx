import CloseIcon from "@mui/icons-material/Close";
import { Autocomplete, Chip, CircularProgress, TextField } from "@mui/material";
import React, { useState } from "react";
import { useDebounce, usePrevious } from "react-use";
const CustomSelect = (props) => {
  const {
    onChange,
    loading,
    options,
    value,
    onSearch,
    label,
    error,
    message,
    sx,
    renderOption,
    multiple,
    fullWidth,
    readOnly,
    renderInput,
    refContainer,
    variant = "outlined",
    getOptionDisabled,
  } = props;
  const [keyword, setKeyword] = useState("");
  const prevKeyword = usePrevious(keyword);
  const [,] = useDebounce(
    () => (onSearch ? onSearch(keyword) : undefined),
    500,
    [keyword]
  );
  return (
    <Autocomplete
      ref={refContainer}
      sx={{ ...sx }}
      onChange={(event, value, reason) => {
        if (reason === "clear") {
          onChange(null);
        } else onChange(value);
      }}
      getOptionDisabled={getOptionDisabled}
      // onInputChange={(event, value, reson) => {
      //   if (reson === "reset") {
      //     console.log("reset");
      //   }
      // }}
      disableCloseOnSelect={multiple ? true : false}
      multiple={multiple ? true : false}
      loading={loading}
      fullWidth={fullWidth}
      getOptionLabel={(option) => option.name}
      isOptionEqualToValue={(option, value) => option.value == value.value}
      options={options ? options : []}
      value={value}
      renderTags={(v, getTagProps) =>
        v.map((option, index) => (
          <Chip
            key={index}
            size="small"
            //color="info"
            label={option.label}
            sx={{ borderRadius: 0 }}
            {...getTagProps({ index })}
            onDelete={() =>
              onChange(value.filter((el) => el.value !== option.value))
            }
            deleteIcon={<CloseIcon />}
          />
        ))
      }
      readOnly={readOnly ? true : false}
      disabled={readOnly}
      renderOption={
        renderOption
          ? renderOption
          : (props, option, { selected }) => <li {...props}>{option.name}</li>
      }
      renderInput={
        renderInput
          ? renderInput
          : (params) => (
              <TextField
                {...params}
                sx={{ mb: 2, ...sx }}
                variant={variant}
                size="small"
                value={keyword}
                error={error}
                helperText={message ? message : ""}
                label={label}
                onChange={({ currentTarget }) => {
                  setKeyword(currentTarget.value);
                }}
                onBlur={() => setKeyword("")}
                InputLabelProps={{
                  shrink: true,
                }}
                InputProps={{
                  ...params.InputProps,
                  endAdornment: (
                    <React.Fragment>
                      {loading ? (
                        <CircularProgress color="inherit" size={20} />
                      ) : null}
                      {params.InputProps.endAdornment}
                    </React.Fragment>
                  ),
                }}
              />
            )
      }
    />
  );
};

export default CustomSelect;
